package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.InvoiceItemLinkProjection
import br.com.vercel.emerionloadservice.repository.projection.InvoiceItemLinkProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class InvoiceItemLinkQueryRepository(private val jdbcTemplate: JdbcTemplate) {

    fun findAllPaged(pageable: Pageable): Page<InvoiceItemLinkProjection> {
        val baseQuery = """
            select
                d2.codemp as codEmp,
                d2.numres as numres,
                d2.dteres as dteres,
                d2.seqre2 as seqRe2,
                re2.codclp as codClp,
                re2.codgru as codGru,
                re2.codsub as codSub,
                re2.codpro as codPro,
                fat.nronfs as nronfs,
                fat.dtafat as dataFaturamento,
                fat.totfat as totalFaturado
            from fatde2 d2
            left join pedre2 re2
                on re2.codemp = d2.codemp
                and re2.dteres = d2.dteres
                and re2.numres = d2.numres
                and re2.seqre2 = d2.seqre2
            left join fatped fat
                on fat.codemp = d2.codemp
                and fat.dteres = d2.dteres
                and fat.numres = d2.numres
            order by d2.codemp, d2.dteres, d2.numres, d2.seqre2
        """.trimIndent()

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        val content: List<InvoiceItemLinkProjection> = jdbcTemplate.query(pagedQuery) { rs, _ ->
            InvoiceItemLinkProjectionImpl(
                codEmp = rs.getInt("codEmp"),
                numres = rs.getString("numres"),
                dteres = rs.getTimestamp("dteres").toLocalDateTime(),
                seqRe2 = rs.getNullableInt("seqRe2") ?: 0,
                codClp = rs.getString("codClp"),
                codGru = rs.getString("codGru"),
                codSub = rs.getString("codSub"),
                codPro = rs.getString("codPro"),
                nronfs = rs.getString("nronfs"),
                dataFaturamento = rs.getTimestamp("dataFaturamento")?.toLocalDateTime(),
                totalFaturado = rs.getBigDecimal("totalFaturado")?.toDouble()
            )
        }

        val total = jdbcTemplate.queryForObject("select count(*) from fatde2", Long::class.java) ?: 0L
        return PageImpl(content, pageable, total)
    }

    fun findByOrderItem(codEmp: Int, dteres: LocalDate, numres: String, seqRe2: Int): List<InvoiceItemLinkProjection> {
        val query = """
            select
                d2.codemp as codEmp,
                d2.numres as numres,
                d2.dteres as dteres,
                d2.seqre2 as seqRe2,
                re2.codclp as codClp,
                re2.codgru as codGru,
                re2.codsub as codSub,
                re2.codpro as codPro,
                fat.nronfs as nronfs,
                fat.dtafat as dataFaturamento,
                fat.totfat as totalFaturado
            from fatde2 d2
            left join pedre2 re2
                on re2.codemp = d2.codemp
                and re2.dteres = d2.dteres
                and re2.numres = d2.numres
                and re2.seqre2 = d2.seqre2
            left join fatped fat
                on fat.codemp = d2.codemp
                and fat.dteres = d2.dteres
                and fat.numres = d2.numres
            where d2.codemp = ?
                and d2.dteres = ?
                and d2.numres = ?
                and d2.seqre2 = ?
            order by fat.nronfs
        """.trimIndent()

        return jdbcTemplate.query(query, { rs, _ ->
            InvoiceItemLinkProjectionImpl(
                codEmp = rs.getInt("codEmp"),
                numres = rs.getString("numres"),
                dteres = rs.getTimestamp("dteres").toLocalDateTime(),
                seqRe2 = rs.getNullableInt("seqRe2") ?: 0,
                codClp = rs.getString("codClp"),
                codGru = rs.getString("codGru"),
                codSub = rs.getString("codSub"),
                codPro = rs.getString("codPro"),
                nronfs = rs.getString("nronfs"),
                dataFaturamento = rs.getTimestamp("dataFaturamento")?.toLocalDateTime(),
                totalFaturado = rs.getBigDecimal("totalFaturado")?.toDouble()
            )
        }, codEmp, java.sql.Date.valueOf(dteres), numres, seqRe2)
    }

    private fun ResultSet.getNullableInt(columnLabel: String): Int? {
        val value = getInt(columnLabel)
        return if (wasNull()) null else value
    }
}
