package br.edu.utfpr.ironquest.treino.repository.sessao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import br.edu.utfpr.ironquest.shared.domain.EntidadeBase_;
import br.edu.utfpr.ironquest.shared.exception.ParametroInvalidoException;
import br.edu.utfpr.ironquest.treino.domain.Serie;
import br.edu.utfpr.ironquest.treino.domain.Serie_;
import br.edu.utfpr.ironquest.treino.domain.SessaoTreino;
import br.edu.utfpr.ironquest.treino.domain.SessaoTreino_;
import br.edu.utfpr.ironquest.treino.repository.filter.SessaoTreinoFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

/**
 * Pesquisa paginada de sessões com Criteria API e metamodelo JPA ({@code SessaoTreino_}, gerado na compilação).
 * Spring Data liga esta classe ao {@code SessaoTreinoRepository} pelo sufixo "Impl".
 */
public class SessaoTreinoRepositoryImpl implements SessaoTreinoRepositoryQuery {

	private static final char ESCAPE = '!';

	/** Campos aceitos no parâmetro {@code sort}. */
	private static final Map<String, Function<Root<SessaoTreino>, Expression<?>>> ORDENACOES = new LinkedHashMap<>();

	static {
		ORDENACOES.put("dataTreino", root -> root.get(SessaoTreino_.dataTreino));
		ORDENACOES.put("iniciadaEm", root -> root.get(SessaoTreino_.iniciadaEm));
		ORDENACOES.put("volumeTotalKg", root -> root.get(SessaoTreino_.volumeTotalKg));
	}

	@PersistenceContext
	private EntityManager manager;

	@Override
	public Page<SessaoTreino> filtrar(SessaoTreinoFilter filtro, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<SessaoTreino> criteria = builder.createQuery(SessaoTreino.class);
		Root<SessaoTreino> root = criteria.from(SessaoTreino.class);
		// Relacionamentos exibidos na listagem: evita uma consulta extra por sessão.
		root.fetch(SessaoTreino_.usuario);
		root.fetch(SessaoTreino_.planoTreino, JoinType.LEFT);

		criteria.where(criarRestricoes(filtro, builder, criteria, root));
		criteria.orderBy(criarOrdenacao(pageable.getSort(), builder, root));

		TypedQuery<SessaoTreino> query = manager.createQuery(criteria);
		adicionarPaginacao(query, pageable);
		return new PageImpl<>(query.getResultList(), pageable, total(filtro));
	}

	private Predicate[] criarRestricoes(SessaoTreinoFilter filtro, CriteriaBuilder builder, AbstractQuery<?> query,
			Root<SessaoTreino> root) {
		List<Predicate> predicates = new ArrayList<>();

		if (filtro.usuarioId() != null) {
			predicates.add(builder.equal(root.get(SessaoTreino_.usuario).get(EntidadeBase_.id), filtro.usuarioId()));
		}
		if (filtro.status() != null) {
			predicates.add(builder.equal(root.get(SessaoTreino_.status), filtro.status()));
		}
		if (filtro.planoTreinoId() != null) {
			predicates.add(builder.equal(root.get(SessaoTreino_.planoTreino).get(EntidadeBase_.id),
					filtro.planoTreinoId()));
		}
		if (filtro.dataTreinoDe() != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get(SessaoTreino_.dataTreino), filtro.dataTreinoDe()));
		}
		if (filtro.dataTreinoAte() != null) {
			predicates.add(builder.lessThanOrEqualTo(root.get(SessaoTreino_.dataTreino), filtro.dataTreinoAte()));
		}
		if (StringUtils.hasText(filtro.observacoes())) {
			predicates.add(builder.like(builder.lower(root.get(SessaoTreino_.observacoes)),
					"%" + escaparCuringas(filtro.observacoes().trim().toLowerCase(Locale.ROOT)) + "%", ESCAPE));
		}
		if (filtro.exercicioId() != null) {
			// EXISTS evita sessões repetidas quando há várias séries do mesmo exercício.
			Subquery<Long> series = query.subquery(Long.class);
			Root<Serie> serie = series.from(Serie.class);
			series.select(serie.get(EntidadeBase_.id))
					.where(builder.equal(serie.get(Serie_.sessaoTreino), root),
							builder.equal(serie.get(Serie_.exercicio).get(EntidadeBase_.id), filtro.exercicioId()));
			predicates.add(builder.exists(series));
		}
		return predicates.toArray(Predicate[]::new);
	}

	/** Sem {@code sort}: treinos mais recentes primeiro. O id desempata para a paginação ser estável. */
	private List<Order> criarOrdenacao(Sort sort, CriteriaBuilder builder, Root<SessaoTreino> root) {
		List<Order> ordens = new ArrayList<>();
		if (sort.isUnsorted()) {
			ordens.add(builder.desc(root.get(SessaoTreino_.dataTreino)));
			ordens.add(builder.desc(root.get(SessaoTreino_.iniciadaEm)));
		}
		for (Sort.Order ordem : sort) {
			Function<Root<SessaoTreino>, Expression<?>> campo = ORDENACOES.get(ordem.getProperty());
			if (campo == null) {
				throw new ParametroInvalidoException("pesquisa.ordenacao-invalida", ordem.getProperty(),
						String.join(", ", ORDENACOES.keySet()));
			}
			ordens.add(ordem.isAscending() ? builder.asc(campo.apply(root)) : builder.desc(campo.apply(root)));
		}
		ordens.add(builder.desc(root.get(EntidadeBase_.id)));
		return ordens;
	}

	private void adicionarPaginacao(TypedQuery<?> query, Pageable pageable) {
		if (pageable.isPaged()) {
			query.setFirstResult((int) pageable.getOffset());
			query.setMaxResults(pageable.getPageSize());
		}
	}

	private long total(SessaoTreinoFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<SessaoTreino> root = criteria.from(SessaoTreino.class);
		criteria.select(builder.count(root)).where(criarRestricoes(filtro, builder, criteria, root));
		return manager.createQuery(criteria).getSingleResult();
	}

	private static String escaparCuringas(String texto) {
		return texto.replace("!", "!!").replace("%", "!%").replace("_", "!_");
	}
}
