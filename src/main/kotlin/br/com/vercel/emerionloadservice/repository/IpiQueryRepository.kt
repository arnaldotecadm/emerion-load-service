package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.IpiProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

private const val BASE_QUERY_IPI = """
    select
        ipi.FLGATIVO        as flgAtivo,
        ipi.codipi          as codigoIpi,
        ipi.tipipi          as tipoIpi,
        ipi.NOMIPI          as nomeIpi,
        ipi.clsipi          as ncmIPI,
        ipi.COD_ENQ         as codigoEnquadramentoLegal,
        ipi.cstipi          as cstIpi,
        sip.nomsip          as descricaoSituacaoTributariaIpi,
        ipi.peripi          as aliquotaIpi,
        ipi.basipi          as percentualBaseCalculoIpi,
        ipi.FLG_SINEIF20    as flgSineif20,
        ipi.codtxf          as codigoTextoFiscal,
        ipi.cstpis          as cstPis,
        pis.nompis          as descricaoSituacaoTributariaPis,
        ipi.ALIQ_PIS        as aliquotaPis,
        ipi.FLG_DESC_ZF_PIS as incluiDescontoSuframaPis,
        ipi.cstcof          as cstCofins,
        cof.nomcof          as descricaoSituacaoTributariaCofins,
        ipi.ALIQ_COF        as aliquotaCofins,
        ipi.FLG_DESC_ZF_COF as incluiDescontoSuframaCofins
    from estipi ipi
    left join estsip sip on sip.signfe = ipi.cstipi and sip.tipsip = ipi.tipipi
    left join estpis pis on pis.signfe = ipi.cstpis
    left join estcof cof on cof.signfe = ipi.cstcof
"""

/**
 * Handles IPI/PIS/COFINS tax table queries using JdbcTemplate directly, since Firebird 1.5
 * only supports pagination via literal `FIRST`/`SKIP` values (see FirebirdPagination).
 *
 * Records are identified by the composite key (codipi, tipipi).
 */
@Repository
class IpiQueryRepository(private val jdbcTemplate: JdbcTemplate) {

    fun findAllPaged(pageable: Pageable): Page<IpiProjectionImpl> {
        val total = jdbcTemplate.queryForObject("select count(*) from estipi", Long::class.java) ?: 0L

        val pagedQuery = FirebirdPagination.applyFirstSkip(
            "$BASE_QUERY_IPI order by ipi.codipi, ipi.tipipi",
            pageable
        )

        val content = jdbcTemplate.query(pagedQuery) { rs, _ -> mapRow(rs) }

        return PageImpl(content, pageable, total)
    }

    fun findByKey(codipi: String, tipipi: String): IpiProjectionImpl? {
        // Values come from path variables (caller-controlled strings), so we use
        // a PreparedStatement via JdbcTemplate to avoid SQL injection.
        val query = "$BASE_QUERY_IPI where ipi.codipi = ? and ipi.tipipi = ?"

        return jdbcTemplate.query(query, { rs, _ -> mapRow(rs) }, codipi, tipipi).firstOrNull()
    }

    private fun mapRow(rs: java.sql.ResultSet) = IpiProjectionImpl(
        flgAtivo = rs.getString("flgAtivo"),
        codigoIpi = rs.getString("codigoIpi"),
        tipoIpi = rs.getString("tipoIpi"),
        nomeIpi = rs.getString("nomeIpi"),
        ncmIpi = rs.getString("ncmIPI"),
        codigoEnquadramentoLegal = rs.getString("codigoEnquadramentoLegal"),
        cstIpi = rs.getString("cstIpi"),
        descricaoSituacaoTributariaIpi = rs.getString("descricaoSituacaoTributariaIpi"),
        aliquotaIpi = rs.getBigDecimal("aliquotaIpi")?.toDouble(),
        percentualBaseCalculoIpi = rs.getBigDecimal("percentualBaseCalculoIpi")?.toDouble(),
        flgSineif20 = rs.getString("flgSineif20"),
        codigoTextoFiscal = rs.getString("codigoTextoFiscal"),
        cstPis = rs.getString("cstPis"),
        descricaoSituacaoTributariaPis = rs.getString("descricaoSituacaoTributariaPis"),
        aliquotaPis = rs.getBigDecimal("aliquotaPis")?.toDouble(),
        incluiDescontoSuframaPis = rs.getString("incluiDescontoSuframaPis"),
        cstCofins = rs.getString("cstCofins"),
        descricaoSituacaoTributariaCofins = rs.getString("descricaoSituacaoTributariaCofins"),
        aliquotaCofins = rs.getBigDecimal("aliquotaCofins")?.toDouble(),
        incluiDescontoSuframaCofins = rs.getString("incluiDescontoSuframaCofins")
    )
}
