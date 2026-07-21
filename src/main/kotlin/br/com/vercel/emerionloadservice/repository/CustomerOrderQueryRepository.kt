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
                (select cgcemp from geremp where codemp = 1) as cnpjEmpresa,
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
            order by p.numres
        """.trimIndent()

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        return jdbcTemplate.query(pagedQuery) { rs, _ ->
            CustomerOrderHeaderProjectionImpl(
                codCli = rs.getLong("codCli"),
                cnpjEmpresa = rs.getString("cnpjEmpresa"),
                numres = rs.getString("numres"),
                nronfe = rs.getString("nronfe"),
                dteres = rs.getTimestamp("dteres").toInstant(),
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
                re2.totre2 as valorTotal
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
                valorTotal = rs.getDouble("valorTotal")
            )
        }
    }
}
