package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.ProductProjection
import br.com.vercel.emerionloadservice.repository.projection.ProductProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

/**
 * Handles paginated product queries using JdbcTemplate directly, since Firebird 1.5
 * only supports pagination via literal `FIRST`/`SKIP` values (see FirebirdPagination),
 * which Spring Data JPA's Pageable-based native queries cannot generate.
 */
@Repository
class ProductQueryRepository(private val jdbcTemplate: JdbcTemplate) {

    fun findAllPaged(pageable: Pageable): Page<ProductProjection> {
        val baseQuery = """
            select
                pro.codgru as codGru,
                pro.codsub as codSub,
                pro.codpro as codPro,
                pro.dscpro as nome,
                (
                    select first 1 ite.vb1ite
                    from estite ite
                    where ite.codclp = pro.codclp
                    and ite.codgru = pro.codgru
                    and ite.codsub = pro.codsub
                    and ite.codpro = pro.codpro
                ) as preco
            from estpro pro
            order by pro.codgru, pro.codsub, pro.codpro
        """.trimIndent()

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        val content: List<ProductProjection> = jdbcTemplate.query(pagedQuery) { rs, _ ->
            ProductProjectionImpl(
                codGru = rs.getString("codGru"),
                codSub = rs.getString("codSub"),
                codPro = rs.getString("codPro"),
                nome = rs.getString("nome"),
                preco = rs.getBigDecimal("preco")
            )
        }

        val total = jdbcTemplate.queryForObject("select count(*) from estpro", Long::class.java) ?: 0L

        return PageImpl(content, pageable, total)
    }
}
