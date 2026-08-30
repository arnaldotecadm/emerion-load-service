package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.InvoiceProjection
import br.com.vercel.emerionloadservice.repository.projection.InvoiceProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class InvoiceQueryRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun findAllPaged(pageable: Pageable): Page<InvoiceProjection> {
        val baseQuery =
            """
            select
                fat.codemp as codEmp,
                p.codcli as codCli,
                fat.numres as numres,
                fat.dteres as dteres,
                fat.nronfs as nronfs,
                fat.dtafat as dataFaturamento,
                fat.totfat as totalFaturado
            from fatped fat
            left join pedres p
                on p.codemp = fat.codemp
                and p.dteres = fat.dteres
                and p.numres = fat.numres
            order by fat.codemp, fat.dteres, fat.numres, fat.nronfs
            """.trimIndent()

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        val content: List<InvoiceProjection> =
            jdbcTemplate.query(pagedQuery) { rs, _ ->
                InvoiceProjectionImpl(
                    codEmp = rs.getInt("codEmp"),
                    codCli = rs.getLong("codCli").takeIf { !rs.wasNull() },
                    numres = rs.getString("numres"),
                    dteres = rs.getTimestamp("dteres").toLocalDateTime(),
                    nronfs = rs.getString("nronfs"),
                    dataFaturamento = rs.getTimestamp("dataFaturamento")?.toLocalDateTime(),
                    totalFaturado = rs.getBigDecimal("totalFaturado")?.toDouble(),
                )
            }

        val total = jdbcTemplate.queryForObject("select count(*) from fatped", Long::class.java) ?: 0L
        return PageImpl(content, pageable, total)
    }

    fun findByOrder(
        codEmp: Int,
        dteres: LocalDate,
        numres: String,
    ): List<InvoiceProjection> {
        val query =
            """
            select
                fat.codemp as codEmp,
                p.codcli as codCli,
                fat.numres as numres,
                fat.dteres as dteres,
                fat.nronfs as nronfs,
                fat.dtafat as dataFaturamento,
                fat.totfat as totalFaturado
            from fatped fat
            left join pedres p
                on p.codemp = fat.codemp
                and p.dteres = fat.dteres
                and p.numres = fat.numres
            where fat.codemp = ?
                and fat.dteres = ?
                and fat.numres = ?
            order by fat.nronfs
            """.trimIndent()

        return jdbcTemplate.query(query, { rs, _ ->
            InvoiceProjectionImpl(
                codEmp = rs.getInt("codEmp"),
                codCli = rs.getLong("codCli").takeIf { !rs.wasNull() },
                numres = rs.getString("numres"),
                dteres = rs.getTimestamp("dteres").toLocalDateTime(),
                nronfs = rs.getString("nronfs"),
                dataFaturamento = rs.getTimestamp("dataFaturamento")?.toLocalDateTime(),
                totalFaturado = rs.getBigDecimal("totalFaturado")?.toDouble(),
            )
        }, codEmp, java.sql.Date.valueOf(dteres), numres)
    }
}
