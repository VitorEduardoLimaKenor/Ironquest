package br.edu.utfpr.ironquest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;

/**
 * MySQL real em container para os testes: as migrações Flyway e a validação do Hibernate rodam contra o
 * mesmo banco usado em produção. Requer Docker em execução.
 */
@TestConfiguration(proxyBeanMethods = false)
public class MySqlTestcontainersConfig {

	@Bean
	@ServiceConnection
	MySQLContainer mysql() {
		return new MySQLContainer("mysql:8.4");
	}
}
