package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.VendedorProjection
import br.com.vercel.emerionloadservice.repository.projection.VendedorProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

/**
 * Handles paginated vendedor (salesman) queries using JdbcTemplate directly, since Firebird 1.5
 * only supports pagination via literal `FIRST`/`SKIP` values (see FirebirdPagination),
 * which Spring Data JPA's Pageable-based native queries cannot generate.
 */
@Repository
class VendedorQueryRepository(private val jdbcTemplate: JdbcTemplate) {

    fun findAllPaged(pageable: Pageable): Page<VendedorProjection> {
        val baseQuery = """
            select
                ven.codven as id,
                ven.nomven as nome,
                ven.apeven as apelido,
                ven.cgcven as cpfCnpj,
                ven.fonven as telefone,
                ven.celven as celular,
                ven.emaven as email,
                ven.cidven as cidade,
                ven.sigufe as uf,
                ven.flgati as situacao,
                ven.sldven as saldo,
                ven.dcaven as dataCadastro
            from finven ven
            order by ven.codven
        """.trimIndent()

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        val content: List<VendedorProjection> = jdbcTemplate.query(pagedQuery) { rs, _ ->
            VendedorProjectionImpl(
                id = rs.getLong("id"),
                nome = rs.getString("nome"),
                apelido = rs.getString("apelido"),
                cpfCnpj = rs.getString("cpfCnpj"),
                telefone = rs.getString("telefone"),
                celular = rs.getString("celular"),
                email = rs.getString("email"),
                cidade = rs.getString("cidade"),
                uf = rs.getString("uf"),
                situacao = rs.getString("situacao"),
                saldo = rs.getBigDecimal("saldo"),
                dataCadastro = rs.getTimestamp("dataCadastro")?.toLocalDateTime()
            )
        }

        val total = jdbcTemplate.queryForObject("select count(*) from finven", Long::class.java) ?: 0L

        return PageImpl(content, pageable, total)
    }
}
