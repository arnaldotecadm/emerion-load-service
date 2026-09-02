package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.model.CustomerOrder
import br.com.vercel.emerionloadservice.model.CustomerOrderItem
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import java.sql.ResultSet

private const val BASE_QUERY_PEDRES = """
    select
        ped.codemp            as codigoEmpresa,
        ped.codcli            as codigoCliente,
        ped.cgccli           as cpfCnpj,
        ped.numres            as numeroPedido,
        ped.dteres            as dataPedido,
        ped.sitres            as statusPedido,
        ped.totger            as totalPedidoComImpostos,
        ped.totres            as totalPedidoSemImpostos,
        ped.totipi            as totalIpi,
        ped.toticm            as totalIcms,
        ped.totpis            as totalPis,
        ped.totcof            as totalCofins,
        ped.totsub            as totalSubstituicaoTributaria,
        ped.totdescinc        as totalDescontoIncondicional,
        ped.totfrt            as totalFrete,
        ped.totseg            as totalSeguro,
        ped.totoutdesp        as totalOutrasDespesas,
        ped.codven            as vendedorExternalId,
        ped.codatd            as atendenteCod,
        ped.dtfres            as dataEntregaPrevista,
        ped.codtra            as codigoTransportadora,
        ped.pedant            as pedidoAnterior,
        ped.regtrb            as regimeTributario,
        reg.nomregtrib      as nomeRegimeTributario,
        ped.codpfa            as codigoPadraoFaturamento
    from pedres ped
    left join finregtrib reg
        on reg.numregtrib = ped.regtrb
"""

/**
 * Handles paginated customer order queries using JdbcTemplate directly, since Firebird 1.5
 * only supports pagination via literal `FIRST`/`SKIP` values (see FirebirdPagination),
 * which Spring Data JPA's Pageable-based native queries cannot generate.
 *
 * Orders are paginated first (one page item per order header), then their items are
 * fetched in a single follow-up query and grouped back onto each order, mirroring the
 * header + item list shape used by the legacy emerion-cliente-loader.
 */
@Repository
class CustomerOrderQueryRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    private data class OrderBusinessKey(
        val codEmp: Int,
        val dteres: java.time.LocalDate,
        val numres: String,
    )

    private fun mapHeader(rs: ResultSet) =
        CustomerOrder(
            codigoEmpresa = rs.getInt("codigoEmpresa"),
            codigoCliente = rs.getLong("codigoCliente"),
            cpfCnpj = rs.getString("cpfCnpj"),
            numeroPedido = rs.getString("numeroPedido"),
            dataPedido = rs.getTimestamp("dataPedido").toLocalDateTime(),
            statusPedido = rs.getString("statusPedido"),
            totalPedidoComImpostos = rs.getDouble("totalPedidoComImpostos"),
            totalPedidoSemImpostos = rs.getDouble("totalPedidoSemImpostos"),
            totalIpi = rs.getDouble("totalIpi"),
            totalIcms = rs.getDouble("totalIcms"),
            totalPis = rs.getDouble("totalPis"),
            totalCofins = rs.getDouble("totalCofins"),
            totalSubstituicaoTributaria = rs.getDouble("totalSubstituicaoTributaria"),
            totalDescontoIncondicional = rs.getDouble("totalDescontoIncondicional"),
            totalFrete = rs.getBigDecimal("totalFrete")?.toDouble(),
            totalSeguro = rs.getBigDecimal("totalSeguro")?.toDouble(),
            totalOutrasDespesas = rs.getBigDecimal("totalOutrasDespesas")?.toDouble(),
            vendedorExternalId = rs.getLong("vendedorExternalId"),
            dataEntregaPrevista = rs.getTimestamp("dataEntregaPrevista")?.toLocalDateTime(),
            codigoTransportadora = rs.getString("codigoTransportadora"),
            pedidoAnterior = rs.getString("pedidoAnterior"),
            regimeTributario = rs.getString("regimeTributario"),
            nomeRegimeTributario = rs.getString("nomeRegimeTributario"),
            codigoPadraoFaturamento = rs.getString("codigoPadraoFaturamento"),
            itens = emptyList(),
        )

    fun findAllPaged(pageable: Pageable): Page<CustomerOrder> {
        val total = jdbcTemplate.queryForObject<Long>("select count(*) from pedres") ?: 0L
        val headers = findHeadersPaged(pageable)
        if (headers.isEmpty()) {
            return PageImpl(emptyList(), pageable, total)
        }

        val headerKeys =
            headers.map { OrderBusinessKey(it.codigoEmpresa, it.dataPedido.toLocalDate(), it.numeroPedido) }.toSet()
        val itemsByOrderKey =
            findItems(headers.map { it.numeroPedido }, headerKeys)
                .groupBy { OrderBusinessKey(it.codEmp, it.dteres, it.numres) }
        val content =
            headers.map { header ->
                val orderKey = OrderBusinessKey(header.codigoEmpresa, header.dataPedido.toLocalDate(), header.numeroPedido)
                header.copy(itens = itemsByOrderKey[orderKey].orEmpty())
            }

        return PageImpl(content, pageable, total)
    }

    fun findByKey(numres: String): CustomerOrder? {
        val headerQuery = "$BASE_QUERY_PEDRES where ped.numres = ?"
        val header =
            jdbcTemplate
                .query(headerQuery, { rs, _ -> mapHeader(rs) }, numres)
                .firstOrNull() ?: return null

        val itemsByOrderKey =
            findItems(
                listOf(numres),
                setOf(OrderBusinessKey(header.codigoEmpresa, header.dataPedido.toLocalDate(), header.numeroPedido)),
            )

        return header.copy(itens = itemsByOrderKey)
    }

    private fun findHeadersPaged(pageable: Pageable): List<CustomerOrder> {
        val query =
            FirebirdPagination.applyFirstSkip(
                "$BASE_QUERY_PEDRES order by ped.dteres desc",
                pageable,
            )
        return jdbcTemplate.query(query) { rs, _ -> mapHeader(rs) }
    }

    private fun findItems(
        numresList: List<String>,
        headerKeys: Set<OrderBusinessKey>,
    ): List<CustomerOrderItem> {
        // Values come from a prior query result (never user input), so they are safe to
        // inline as a literal IN list; Firebird 1.5 native queries can't bind IN (:list).
        val idList = numresList.joinToString(",") { "'$it'" }

        val query =
            """
            select
                re2.codemp as codEmp,
                re2.dteres as dteres,
                re2.numres as numres,
                re2.codgru as codGru,
                re2.codsub as codSub,
                re2.codpro as codPro,
                re2.desre2 as descricao,
                re2.qtpre2 as quantidade,
                re2.vlqre2 as valorUnitario,
                re2.totre2 as valorTotal,
                re2.seqre2 as seqRe2,
                re2.codclp as codClp,
                re2.codst1 as codSt1,
                re2.codund as codUnd,
                re2.vlure2 as vluRe2,
                re2.dscre2 as dscRe2,
                re2.dsrre2 as dsrRe2,
                re2.icmre2 as icmsAliquota,
                re2.basicm as icmsBase,
                re2.toticm as icmsValor,
                re2.redicm as icmsReducaoBase,
                re2.bassub as icmsSubstituicaoBase,
                re2.totsub as icmsSubstituicaoValor,
                re2.icmsub as icmsSubstituicaoAliquota,
                re2.mrgsub as icmsSubstituicaoMargem,
                re2.redsub as icmsSubstituicaoReducaoBase,
                re2.ipire2 as ipiAliquota,
                re2.basipi as ipiBase,
                re2.totipi as ipiValor,
                re2.clsipi as ipiClassificacao,
                re2.cstipi as ipiCst,
                re2.baspis as pisBase,
                re2.aliqpis as pisAliquota,
                re2.totpis as pisValor,
                re2.cstpis as pisCst,
                re2.bascof as cofinsBase,
                re2.aliqcof as cofinsAliquota,
                re2.totcof as cofinsValor,
                re2.cstcof as cofinsCst,
                re2.totdsr as descontoValor,
                re2.totfrt as freteValor,
                re2.totseg as seguroValor,
                re2.totoutdesp as outrasDespesasValor,
                re2.totitetrb as totalItemTributado,
                re2.totren as totRen,
                re2.totge2 as totGe2,
                re2.obsre2 as observacao,
                re2.numpedcompra as pedidoCompraCliente,
                re2.numitemcompra as itemPedidoCompraCliente,
                re2.nrore2 as nroRe2,
                re2.flgval as flgVal,
                re2.flgpac as flgPac,
                re2.flglib as flgLib,
                re2.codcfo as codCfo,
                re2.codcor as codcor,
                re2.codtam as codtam,
                re2.dspre2 as descricaoNFe,
                re2.liqre2 as pesoLiquido,
                re2.brtre2 as pesoBruto,
                re2.refre2 as referencia,
                re2.qtfre2 as quantidadeFaturada,
                re2.qtsre2 as quantidadeSeparada,
                re2.totcst  as custoTotal,
                re2.lucrol  as lucroValor,
                re2.lucrop  as lucroPorcentagem,
                re2.sldre2  as saldoQuantidade,
                re2.vdsre2  as descontoItemValor,
                re2.totdsc  as descontoItemTotal
            from pedre2 re2
            where re2.numres in ($idList)
            order by re2.numres, re2.seqre2
            """.trimIndent()

        return jdbcTemplate
            .query(query) { rs, _ ->
                CustomerOrderItem(
                    codEmp = rs.getInt("codEmp"),
                    dteres = rs.getTimestamp("dteres").toLocalDateTime().toLocalDate(),
                    numres = rs.getString("numres"),
                    codGru = rs.getString("codGru"),
                    codSub = rs.getString("codSub"),
                    codPro = rs.getString("codPro"),
                    descricao = rs.getString("descricao"),
                    quantidade = rs.getDouble("quantidade"),
                    valorUnitario = rs.getDouble("valorUnitario"),
                    valorTotal = rs.getDouble("valorTotal"),
                    seqRe2 = rs.getInt("seqRe2"),
                    codClp = rs.getString("codClp"),
                    codSt1 = rs.getString("codSt1"),
                    codUnd = rs.getString("codUnd"),
                    vluRe2 = rs.getBigDecimal("vluRe2")?.toDouble(),
                    dscRe2 = rs.getBigDecimal("dscRe2")?.toDouble(),
                    dsrRe2 = rs.getBigDecimal("dsrRe2")?.toDouble(),
                    icmsAliquota = rs.getBigDecimal("icmsAliquota")?.toDouble(),
                    icmsBase = rs.getBigDecimal("icmsBase")?.toDouble(),
                    icmsValor = rs.getBigDecimal("icmsValor")?.toDouble(),
                    icmsReducaoBase = rs.getBigDecimal("icmsReducaoBase")?.toDouble(),
                    icmsSubstituicaoBase = rs.getBigDecimal("icmsSubstituicaoBase")?.toDouble(),
                    icmsSubstituicaoValor = rs.getBigDecimal("icmsSubstituicaoValor")?.toDouble(),
                    icmsSubstituicaoAliquota = rs.getBigDecimal("icmsSubstituicaoAliquota")?.toDouble(),
                    icmsSubstituicaoMargem = rs.getBigDecimal("icmsSubstituicaoMargem")?.toDouble(),
                    icmsSubstituicaoReducaoBase = rs.getBigDecimal("icmsSubstituicaoReducaoBase")?.toDouble(),
                    ipiAliquota = rs.getBigDecimal("ipiAliquota")?.toDouble(),
                    ipiBase = rs.getBigDecimal("ipiBase")?.toDouble(),
                    ipiValor = rs.getBigDecimal("ipiValor")?.toDouble(),
                    ipiClassificacao = rs.getString("ipiClassificacao"),
                    ipiCst = rs.getString("ipiCst"),
                    pisBase = rs.getBigDecimal("pisBase")?.toDouble(),
                    pisAliquota = rs.getBigDecimal("pisAliquota")?.toDouble(),
                    pisValor = rs.getBigDecimal("pisValor")?.toDouble(),
                    pisCst = rs.getString("pisCst"),
                    cofinsBase = rs.getBigDecimal("cofinsBase")?.toDouble(),
                    cofinsAliquota = rs.getBigDecimal("cofinsAliquota")?.toDouble(),
                    cofinsValor = rs.getBigDecimal("cofinsValor")?.toDouble(),
                    cofinsCst = rs.getString("cofinsCst"),
                    descontoValor = rs.getBigDecimal("descontoValor")?.toDouble(),
                    freteValor = rs.getBigDecimal("freteValor")?.toDouble(),
                    seguroValor = rs.getBigDecimal("seguroValor")?.toDouble(),
                    outrasDespesasValor = rs.getBigDecimal("outrasDespesasValor")?.toDouble(),
                    totalItemTributado = rs.getBigDecimal("totalItemTributado")?.toDouble(),
                    totRen = rs.getBigDecimal("totRen")?.toDouble(),
                    totGe2 = rs.getBigDecimal("totGe2")?.toDouble(),
                    observacao = rs.getString("observacao"),
                    pedidoCompraCliente = rs.getString("pedidoCompraCliente"),
                    itemPedidoCompraCliente = rs.getNullableInt("itemPedidoCompraCliente"),
                    nroRe2 = rs.getNullableInt("nroRe2"),
                    flgVal = rs.getString("flgVal"),
                    flgPac = rs.getString("flgPac"),
                    flgLib = rs.getString("flgLib"),
                    codCfo = rs.getString("codCfo"),
                    codcor = rs.getString("codcor"),
                    codtam = rs.getString("codtam"),
                    descricaoNFe = rs.getString("descricaoNFe"),
                    pesoLiquido = rs.getBigDecimal("pesoLiquido")?.toDouble(),
                    pesoBruto = rs.getBigDecimal("pesoBruto")?.toDouble(),
                    referencia = rs.getString("referencia"),
                    quantidadeFaturada = rs.getBigDecimal("quantidadeFaturada")?.toDouble(),
                    quantidadeSeparada = rs.getBigDecimal("quantidadeSeparada")?.toDouble(),
                    custoTotal = rs.getBigDecimal("custoTotal")?.toDouble(),
                    lucroValor = rs.getBigDecimal("lucroValor")?.toDouble(),
                    lucroPorcentagem = rs.getBigDecimal("lucroPorcentagem")?.toDouble(),
                    saldoQuantidade = rs.getBigDecimal("saldoQuantidade")?.toDouble(),
                    descontoItemValor = rs.getBigDecimal("descontoItemValor")?.toDouble(),
                    descontoItemTotal = rs.getBigDecimal("descontoItemTotal")?.toDouble(),
                )
            }.filter { headerKeys.contains(OrderBusinessKey(it.codEmp, it.dteres, it.numres)) }
    }

    // The bundled Jaybird driver (2.2.15) predates JDBC 4.1's getObject(column, Class),
    // so nullable INTEGER columns must be read via getInt + wasNull instead.
    private fun ResultSet.getNullableInt(columnLabel: String): Int? {
        val value = getInt(columnLabel)
        return if (wasNull()) null else value
    }
}
