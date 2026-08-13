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
                pro.codgru          as codGru,
                pro.codsub          as codSub,
                pro.codpro          as codPro,
                pro.dscpro          as nome,
                pro.dsrpro          as descricaoReduzida,
                pro.refpro          as referenciaInterna,
                pro.codncm          as ncm,
                pro.cest            as cest,
                pro.codst1          as origemProduto,
                pro.codcat          as categoria,
                pro.codtip          as tipo,
                pro.codmrc          as marca,
                pro.codune          as unidade,
                pro.pesliq          as pesoLiquido,
                pro.pesbrt          as pesoBruto,
                case pro.flbpro
                    when 'D' then 1
                    else 0
                end                 as descontinuado,
                pro.cbapro          as codigoBarrasProprio,
                pro.codbar          as codigoBarras,
                ite.vb1ite          as preco,
                ite.vb2ite          as preco2,
                ite.vb3ite          as preco3,
                ite.vb4ite          as preco4,
                ite.vb5ite          as preco5
            from estpro pro
            left join estite ite on ite.codclp = pro.codclp
                and ite.codgru = pro.codgru
                and ite.codsub = pro.codsub
                and ite.codpro = pro.codpro
                and ite.codemp = (select first 1 codemp from geremp)
            order by pro.codgru, pro.codsub, pro.codpro
        """.trimIndent()

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        val content: List<ProductProjection> = jdbcTemplate.query(pagedQuery) { rs, _ ->
            ProductProjectionImpl(
                codGru = rs.getString("codGru"),
                codSub = rs.getString("codSub"),
                codPro = rs.getString("codPro"),
                nome = rs.getString("nome"),
                descricaoReduzida = rs.getString("descricaoReduzida"),
                referenciaInterna = rs.getString("referenciaInterna"),
                ncm = rs.getString("ncm"),
                cest = rs.getString("cest"),
                origemProduto = rs.getString("origemProduto"),
                categoria = rs.getString("categoria"),
                tipo = rs.getString("tipo"),
                marca = rs.getString("marca"),
                unidade = rs.getString("unidade"),
                pesoLiquido = rs.getBigDecimal("pesoLiquido"),
                pesoBruto = rs.getBigDecimal("pesoBruto"),
                descontinuado = rs.getInt("descontinuado"),
                codigoBarras = rs.getString("codigoBarras"),
                codigoBarrasProprio = rs.getString("codigoBarrasProprio"),
                preco = rs.getBigDecimal("preco"),
                preco2 = rs.getBigDecimal("preco2"),
                preco3 = rs.getBigDecimal("preco3"),
                preco4 = rs.getBigDecimal("preco4"),
                preco5 = rs.getBigDecimal("preco5"),
            )
        }

        val total = jdbcTemplate.queryForObject("select count(*) from estpro", Long::class.java) ?: 0L

        return PageImpl(content, pageable, total)
    }
}
