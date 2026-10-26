package br.edu.utfpr.ironquest.treino.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Cálculos de desempenho de uma série. Resultados com 2 casas decimais.
 */
public final class CalculadoraDesempenho {

	private static final BigDecimal TRINTA = BigDecimal.valueOf(30);

	private CalculadoraDesempenho() {
	}

	/** Volume (tonelagem) da série: carga × repetições. */
	public static BigDecimal volume(int repeticoes, BigDecimal cargaKg) {
		return cargaKg.multiply(BigDecimal.valueOf(repeticoes)).setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * 1RM estimado pela fórmula de Epley: carga × (1 + repetições / 30).
	 * Com 1 repetição, o 1RM é a própria carga.
	 */
	public static BigDecimal umRmEstimado(int repeticoes, BigDecimal cargaKg) {
		if (repeticoes == 1) {
			return cargaKg.setScale(2, RoundingMode.HALF_UP);
		}
		BigDecimal fator = BigDecimal.ONE.add(BigDecimal.valueOf(repeticoes).divide(TRINTA, 10, RoundingMode.HALF_UP));
		return cargaKg.multiply(fator).setScale(2, RoundingMode.HALF_UP);
	}
}
