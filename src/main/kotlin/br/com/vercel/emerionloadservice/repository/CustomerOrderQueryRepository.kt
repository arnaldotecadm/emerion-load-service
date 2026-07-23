package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.model.CustomerOrder
import br.com.vercel.emerionloadservice.repository.mapper.CustomerOrderMapper.toModel
import br.com.vercel.emerionloadservice.repository.projection.CustomerOrderHeaderProjection
import br.com.vercel.emerionloadservice.repository.projection.CustomerOrderHeaderProjectionImpl
import br.com.vercel.emerionloadservice.repository.projection.CustomerOrderItemProjection
import br.com.vercel.emerionloadservice.repository.projection.CustomerOrderItemProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

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
class CustomerOrderQueryRepository(private val jdbcTemplate: JdbcTemplate) {

    fun findAllPaged(pageable: Pageable): Page<CustomerOrder> {
        val total = jdbcTemplate.queryForObject("select count(*) from pedres", Long::class.java) ?: 0L
        val headers = findHeadersPaged(pageable)
        if (headers.isEmpty()) {
            return PageImpl(emptyList(), pageable, total)
        }

        val itemsByNumres = findItems(headers.map { it.numres }).groupBy { it.numres }
        val content = headers.map { header -> header.toModel(itemsByNumres[header.numres].orEmpty()) }

        return PageImpl(content, pageable, total)
    }

    private fun findHeadersPaged(pageable: Pageable): List<CustomerOrderHeaderProjection> {
        val baseQuery = """
            select
                p.codcli as codCli,
                fc.cgccli as cpfCnpj,
                p.numres as numres,
                fat.nronfs as nronfe,
                p.dteres as dteres,
                p.sitres as sitres,
                p.totger as totger,
                p.totres as totres,
                p.totipi as totipi,
                p.totsub as totsub,
                p.totdescinc as totdescinc
            from pedres p
            left join fatped fat
                on fat.codemp = p.codemp
                and fat.dteres = p.dteres
                and fat.numres = p.numres
            left join fincli fc
                on fc.codcli = p.codcli
            order by p.numres
        """.trimIndent()

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        return jdbcTemplate.query(pagedQuery) { rs, _ ->
            CustomerOrderHeaderProjectionImpl(
                codCli = rs.getLong("codCli"),
                cpfCnpj = rs.getString("cpfCnpj"),
                numres = rs.getString("numres"),
                nronfe = rs.getString("nronfe"),
                dteres = rs.getTimestamp("dteres").toLocalDateTime(),
                sitres = rs.getString("sitres"),
                totger = rs.getDouble("totger"),
                totres = rs.getDouble("totres"),
                totipi = rs.getDouble("totipi"),
                totsub = rs.getDouble("totsub"),
                totdescinc = rs.getDouble("totdescinc")
            )
        }
    }

    private fun findItems(numresList: List<String>): List<CustomerOrderItemProjection> {
        // Values come from a prior query result (never user input), so they are safe to
        // inline as a literal IN list; Firebird 1.5 native queries can't bind IN (:list).
        val idList = numresList.joinToString(",") { "'$it'" }

        val query = """
            select
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
                re2.codcfo as codCfo
            from pedre2 re2
            where re2.numres in ($idList)
            order by re2.numres, re2.seqre2
        """.trimIndent()

        return jdbcTemplate.query(query) { rs, _ ->
            CustomerOrderItemProjectionImpl(
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
                codCfo = rs.getString("codCfo")
            )
        }
    }

    // The bundled Jaybird driver (2.2.15) predates JDBC 4.1's getObject(column, Class),
    // so nullable INTEGER columns must be read via getInt + wasNull instead.
    private fun ResultSet.getNullableInt(columnLabel: String): Int? {
        val value = getInt(columnLabel)
        return if (wasNull()) null else value
    }
}
