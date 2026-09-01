package br.com.vercel.emerionloadservice.utils

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class TestDataBuilder(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun clearVendedores() {
        jdbcTemplate.update("DELETE FROM FINVEN")
    }

    fun insertVendedor(
        codVen: Long,
        nome: String,
    ) {
        jdbcTemplate.update(
            "INSERT INTO FINVEN (CODVEN, NOMVEN) VALUES (?, ?)",
            codVen,
            nome,
        )
    }
}
