package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.InvoiceProjection
import br.com.vercel.emerionloadservice.repository.projection.InvoiceProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate

private const val BASE_QUERY_INVOICE = """
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
"""

@Repository
class InvoiceQueryRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun findAllPaged(pageable: Pageable): Page<InvoiceProjection> {
        val baseQuery = "$BASE_QUERY_INVOICE order by fat.codemp, fat.dteres, fat.numres, fat.nronfs"

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        val content: List<InvoiceProjection> =
            jdbcTemplate.query(pagedQuery) { rs, _ ->
                resultsetToModel(rs)
            }

        val total = jdbcTemplate.queryForObject<Long>("select count(*) from fatped") ?: 0L
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
            resultsetToModel(rs)
        }, codEmp, java.sql.Date.valueOf(dteres), numres)
    }

    private fun resultsetToModel(rs: ResultSet): InvoiceProjectionImpl =
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
