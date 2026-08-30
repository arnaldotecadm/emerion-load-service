package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.VendedorProjection
import br.com.vercel.emerionloadservice.repository.projection.VendedorProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository

private const val BASE_QUERY_VENDEDOR = """
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
        ven.dcaven as dataCadastro,
        ven.metrep as metaRepresentacao
    from finven ven
"""

/**
 * Handles paginated vendedor (salesman) queries using JdbcTemplate directly, since Firebird 1.5
 * only supports pagination via literal `FIRST`/`SKIP` values (see FirebirdPagination),
 * which Spring Data JPA's Pageable-based native queries cannot generate.
 */
@Repository
class VendedorQueryRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    private fun resultsetToModel(rs: java.sql.ResultSet): VendedorProjectionImpl =
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
            dataCadastro = rs.getTimestamp("dataCadastro")?.toLocalDateTime(),
            metaRepresentacao = rs.getBigDecimal("metaRepresentacao"),
        )

    fun findAllPaged(pageable: Pageable): Page<VendedorProjection> {
        val baseQuery = "$BASE_QUERY_VENDEDOR order by ven.codven"

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        val content: List<VendedorProjection> =
            jdbcTemplate.query(pagedQuery) { rs, _ ->
                resultsetToModel(rs)
            }

        val total = jdbcTemplate.queryForObject<Long>("select count(*) from finven") ?: 0L

        return PageImpl(content, pageable, total)
    }

    fun findByCodVen(codVen: Long): VendedorProjection? {
        val query = "$BASE_QUERY_VENDEDOR where ven.codven = ?"

        return jdbcTemplate
            .query(query, { rs, _ ->
                resultsetToModel(rs)
            }, codVen)
            .firstOrNull()
    }
}
