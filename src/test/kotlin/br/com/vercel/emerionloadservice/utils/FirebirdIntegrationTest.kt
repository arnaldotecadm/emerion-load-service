package br.com.vercel.emerionloadservice.utils

import org.firebirdsql.testcontainers.FirebirdContainer
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeAll
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

abstract class FirebirdIntegrationTest {
    companion object {
        @JvmStatic
        val container: FirebirdContainer by lazy {
            FirebirdContainer("firebirdsql/firebird:3.0.9")
                .withReuse(true)
                .withUsername("SYSDBA")
                .withPassword("masterkey")
                .withEnableLegacyClientAuth()
                .also { it.start() }
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") {
                "jdbc:firebirdsql://${container.host}:${container.firstMappedPort}/${container.databaseName}"
            }

            registry.add("spring.datasource.username") {
                "SYSDBA"
            }

            registry.add("spring.datasource.password") {
                "masterkey"
            }
        }

        @JvmStatic
        @BeforeAll
        fun initializeDatabase() {
            Flyway
                .configure()
                .dataSource(
                    container.jdbcUrl,
                    container.username,
                    container.password,
                ).load()
                .migrate()
        }
    }
}
