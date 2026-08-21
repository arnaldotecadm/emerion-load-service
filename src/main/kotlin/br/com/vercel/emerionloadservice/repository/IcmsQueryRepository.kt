package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.IcmsProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

private const val BASE_QUERY_ICMS = """
    select
        icms.codicm         as codigoIcms,
        icms.tipicm         as tipoIcms,
        icms.NOMICM         as nomeIcms,
        icms.UFEMITENTE     as ufEmitente,
        icms.CODREGTRIB     as codigoRegimeTributario,
        icms.PERICM         as aliquotaIcms,
        icms.REDICM         as percentualReducaoValorImposto,
        icms.BASICM         as percentualBaseCalculoIcms,
        icms.CODST2         as situacaoTributariaIcms
    from esticm icms
"""

/**
 * Handles ICMS tax table queries using JdbcTemplate directly, since Firebird 1.5
 * only supports pagination via literal `FIRST`/`SKIP` values (see FirebirdPagination).
 *
 * Records are identified by the composite key (codicm, tipicm).
 */
@Repository
class IcmsQueryRepository(private val jdbcTemplate: JdbcTemplate) {

    private fun resultsetToModel(rs: ResultSet) =
        IcmsProjectionImpl(
            codigoIcms = rs.getString("codigoIcms"),
            tipoIcms = rs.getString("tipoIcms"),
            nomeIcms = rs.getString("nomeIcms"),
            ufEmitente = rs.getString("ufEmitente"),
            codigoRegimeTributario = rs.getString("codigoRegimeTributario"),
            aliquotaIcms = rs.getBigDecimal("aliquotaIcms")?.toDouble(),
            percentualReducaoValorImposto = rs.getBigDecimal("percentualReducaoValorImposto")?.toDouble(),
            percentualBaseCalculoIcms = rs.getBigDecimal("percentualBaseCalculoIcms")?.toDouble(),
            situacaoTributariaIcms = rs.getString("situacaoTributariaIcms")
        )

    fun findAllPaged(pageable: Pageable): Page<IcmsProjectionImpl> {
        val total = jdbcTemplate.queryForObject("select count(*) from esticm", Long::class.java) ?: 0L

        val pagedQuery = FirebirdPagination.applyFirstSkip(
            "$BASE_QUERY_ICMS order by icms.codicm, icms.tipicm",
            pageable
        )

        val content = jdbcTemplate.query(pagedQuery) { rs, _ ->
            resultsetToModel(rs)
        }

        return PageImpl(content, pageable, total)
    }

    fun findByKey(codicm: String, tipicm: String): IcmsProjectionImpl? {
        // Values come from path variables (caller-controlled strings), so we use
        // a PreparedStatement via JdbcTemplate to avoid SQL injection.
        val query = "$BASE_QUERY_ICMS where icms.codicm = ? and icms.tipicm = ?"

        return jdbcTemplate.query(query, { rs, _ ->
            resultsetToModel(rs)
        }, codicm, tipicm).firstOrNull()
    }
}
