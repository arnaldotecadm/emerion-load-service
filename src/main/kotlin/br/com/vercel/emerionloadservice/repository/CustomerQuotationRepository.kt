package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.model.CustomerQuotation
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import java.sql.ResultSet

private const val BASE_QUERY_QUOTATION = """
    select
        cta.codemp            as codigoEmpresa,
        cta.codcli            as codigoCliente,
        cli.cgccli           as cpfCnpj,
        cta.numres            as numeroPedido,
        cta.dteres            as dataPedido,
        cta.sitcta            as statusPedido,
        cta.totger            as totalPedidoComImpostos,
        cta.totite            as totalPedidoSemImpostos,
        cta.totipi            as totalIpi,
        cta.toticm            as totalIcms,
        cta.totpis            as totalPis,
        cta.totcof            as totalCofins,
        cta.totsub            as totalSubstituicaoTributaria,
        cta.totdescinc        as totalDescontoIncondicional,
        cta.totfrt            as totalFrete,
        cta.totseg            as totalSeguro,
        cta.totoutdesp        as totalOutrasDespesas,
        cta.codven            as vendedorExternalId,
        cta.codatd            as atendenteCod,
        cta.regtrb            as regimeTributario,
        reg.nomregtrib      as nomeRegimeTributario,
        cta.codpfa            as codigoPadraoFaturamento
    from pedcta cta
    left join fincli cli
        on cli.codcli = cta.codcli
    left join finregtrib reg
        on reg.numregtrib = cta.regtrb
"""

@Repository
class CustomerQuotationRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    private fun mapQuotation(rs: ResultSet): CustomerQuotation =
        CustomerQuotation(
            codigoEmpresa = rs.getInt("codigoEmpresa"),
            codigoCliente = rs.getLong("codigoCliente"),
            cpfCnpj = rs.getString("cpfCnpj"),
            numeroPedido = rs.getString("numeroPedido"),
            dataPedido = rs.getTimestamp("dataPedido").toLocalDateTime().toLocalDate(),
            statusPedido = rs.getString("statusPedido"),
            totalPedidoComImpostos = rs.getDouble("totalPedidoComImpostos"),
            totalPedidoSemImpostos = rs.getDouble("totalPedidoSemImpostos"),
            totalIpi = rs.getBigDecimal("totalIpi")?.toDouble(),
            totalIcms = rs.getBigDecimal("totalIcms")?.toDouble(),
            totalPis = rs.getBigDecimal("totalPis")?.toDouble(),
            totalCofins = rs.getBigDecimal("totalCofins")?.toDouble(),
            totalSubstituicaoTributaria = rs.getBigDecimal("totalSubstituicaoTributaria")?.toDouble(),
            totalDescontoIncondicional = rs.getBigDecimal("totalDescontoIncondicional")?.toDouble(),
            totalFrete = rs.getBigDecimal("totalFrete")?.toDouble(),
            totalSeguro = rs.getBigDecimal("totalSeguro")?.toDouble(),
            totalOutrasDespesas = rs.getBigDecimal("totalOutrasDespesas")?.toDouble(),
            vendedorExternalId = rs.getLong("vendedorExternalId").takeIf { !rs.wasNull() },
            atendenteCod = rs.getLong("atendenteCod").takeIf { !rs.wasNull() },
            regimeTributario = rs.getString("regimeTributario"),
            nomeRegimeTributario = rs.getString("nomeRegimeTributario"),
            codigoPadraoFaturamento = rs.getString("codigoPadraoFaturamento"),
        )

    fun findAllPaged(pageable: Pageable): Page<CustomerQuotation> {
        val query =
            FirebirdPagination.applyFirstSkip(
                "$BASE_QUERY_QUOTATION order by cta.dteres desc, cta.codemp, cta.numres",
                pageable,
            )
        val content = jdbcTemplate.query(query) { rs, _ -> mapQuotation(rs) }
        val total = jdbcTemplate.queryForObject<Long>("select count(*) from pedcta") ?: 0L
        return PageImpl(content, pageable, total)
    }

    fun findAllToWebService(pageable: Pageable): Page<CustomerQuotation> {
        val query =
            FirebirdPagination.applyFirstSkip(
                """
                $BASE_QUERY_QUOTATION
                where coalesce(cta.SENT_TO_WEBSERVICE, 0) = 0
                order by cta.dteres desc, cta.codemp, cta.numres
                """.trimIndent(),
                pageable,
            )
        val content = jdbcTemplate.query(query) { rs, _ -> mapQuotation(rs) }
        val total = jdbcTemplate.queryForObject<Long>("select count(*) from pedcta") ?: 0L
        return PageImpl(content, pageable, total)
    }

    fun updateSentToWebService(numeroPedidoList: List<String>) {
        if (numeroPedidoList.isEmpty()) return

        val placeholders = numeroPedidoList.joinToString(", ") { "?" }
        jdbcTemplate.update(
            "update pedcta set SENT_TO_WEBSERVICE = 1 where numres in ($placeholders)",
            *numeroPedidoList.toTypedArray(),
        )
    }

    fun findByKey(numeroPedido: String): CustomerQuotation? =
        jdbcTemplate
            .query(
                "$BASE_QUERY_QUOTATION where cta.numres = ?",
                { rs, _ -> mapQuotation(rs) },
                numeroPedido,
            ).firstOrNull()
}