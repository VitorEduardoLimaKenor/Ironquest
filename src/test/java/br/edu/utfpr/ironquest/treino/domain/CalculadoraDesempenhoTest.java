package br.edu.utfpr.ironquest.treino.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CalculadoraDesempenhoTest {

	@Test
	void volumeEhCargaVezesRepeticoes() {
		assertThat(CalculadoraDesempenho.volume(5, new BigDecimal("100"))).isEqualByComparingTo("500.00");
		assertThat(CalculadoraDesempenho.volume(12, new BigDecimal("22.5"))).isEqualByComparingTo("270.00");
	}

	@Test
	void umRmEstimadoUsaFormulaDeEpley() {
		// 100 × (1 + 5/30) = 116,666...
		assertThat(CalculadoraDesempenho.umRmEstimado(5, new BigDecimal("100"))).isEqualByComparingTo("116.67");
		// 105 × (1 + 3/30) = 115,5
		assertThat(CalculadoraDesempenho.umRmEstimado(3, new BigDecimal("105"))).isEqualByComparingTo("115.50");
	}

	@Test
	void umRmComUmaRepeticaoEhAPropriaCarga() {
		assertThat(CalculadoraDesempenho.umRmEstimado(1, new BigDecimal("140"))).isEqualByComparingTo("140.00");
	}

	@Test
	void cargaZeroResultaEmZero() {
		assertThat(CalculadoraDesempenho.volume(15, BigDecimal.ZERO)).isEqualByComparingTo("0");
		assertThat(CalculadoraDesempenho.umRmEstimado(15, BigDecimal.ZERO)).isEqualByComparingTo("0");
	}
}
