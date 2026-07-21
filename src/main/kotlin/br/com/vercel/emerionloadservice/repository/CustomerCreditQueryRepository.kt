package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.CustomerCreditProjection
import br.com.vercel.emerionloadservice.repository.projection.CustomerCreditProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

/**
 * Handles paginated customer credit queries using JdbcTemplate directly, since Firebird 1.5
 * only supports pagination via literal `FIRST`/`SKIP` values (see FirebirdPagination),
 * which Spring Data JPA's Pageable-based native queries cannot generate.
 *
 * A customer can have several credit movements (fincde rows), so each page contains
 * one row per movement rather than one row per customer.
 */
@Repository
class CustomerCreditQueryRepository(private val jdbcTemplate: JdbcTemplate) {

    fun findAllPaged(pageable: Pageable): Page<CustomerCreditProjection> {
        val baseQuery = """
            select
                cde.codcli as codCli,
                cde.seqcde as sequencia,
                cde.dtecde as data,
                cde.dteped as dataPedido,
                cde.usacde as valorUtilizado,
                cde.valcde as valorTotal,
                cde.sldcde as saldo,
                cde.sitcde as situacao
            from fincde cde
            order by cde.codcli, cde.seqcde
        """.trimIndent()

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        val content: List<CustomerCreditProjection> = jdbcTemplate.query(pagedQuery) { rs, _ ->
            CustomerCreditProjectionImpl(
                codCli = rs.getLong("codCli"),
                sequencia = rs.getString("sequencia"),
                data = rs.getTimestamp("data").toInstant(),
                dataPedido = rs.getTimestamp("dataPedido")?.toLocalDateTime(),
                valorUtilizado = rs.getDouble("valorUtilizado"),
                valorTotal = rs.getDouble("valorTotal"),
                saldo = rs.getDouble("saldo"),
                situacao = rs.getString("situacao")
            )
        }

        val total = jdbcTemplate.queryForObject("select count(*) from fincde", Long::class.java) ?: 0L

        return PageImpl(content, pageable, total)
    }
}
