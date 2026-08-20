package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.ProductProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

const val BASE_QUERY_ESTPRO = """
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
                pro.codune          as unidadeEntrada,
                pro.coduns          as unidadeSaida,
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
                ite.vb5ite          as preco5,
                pro.idepro          as descontoPadrao,
                Round(qte.QtdQte-((SELECT QTD_RESERVADA FROM RESERVAS(QTE.CODEMP,QTE.CODCLP,QTE.CODGRU,QTE.CODSUB,QTE.CODPRO))+qte.QtdRma),4) as estoqueDisponivel,
                qte.qtmqte          as estoqueMinimo,
                qte.qmaqte          as estoqueMaximo,
                qte.qtrqte          as estoqueReservado,
                qte.qtaqte          as estoqueAdquirido,
                qte.qtdqte          as estoqueAtual,
                qte.qtdrma          as estoqueRMA,
                
                pro.simpro          as similar,
                pro.qtdvol          as quantidadeVolumes,
                pro.qtdemb          as quantidadeEmbalagem,
                pro.locpro          as localizacao,
                pro.pescub          as cubagem,
                pro.cbaemb          as codigoBarrasEmbalagem,
                pro.IBSCBS_C_CLASS_TRIB as ibsCClassTrib,
                pro.IBSCBS_CST          as ibsCst,
                pro.COD_FCP_ENTRADA     as fcpEntrada,
                pro.COD_FCP_SAIDA       as fcpSaida,
                pro.ipisai          as ipiSaida,
                pro.ipient          as ipiEntrada,
                pro.icmsai          as icmSaida,
                pro.icment          as icmEntrada,
                pro.codsts          as icmStSaida,
                pro.codste          as icmStEntrada,
                pro.obspro          as observacao
                
            from estpro pro
            left join estite ite on ite.codclp = pro.codclp
                and ite.codgru = pro.codgru
                and ite.codsub = pro.codsub
                and ite.codpro = pro.codpro
                and ite.codemp = (select first 1 codemp from geremp)
            left join estqte qte on ite.codclp = pro.codclp
                and qte.codgru = pro.codgru
                and qte.codsub = pro.codsub
                and qte.codpro = pro.codpro
                and qte.codemp = (select first 1 codemp from geremp)            
        """

/**
 * Handles paginated product queries using JdbcTemplate directly, since Firebird 1.5
 * only supports pagination via literal `FIRST`/`SKIP` values (see FirebirdPagination),
 * which Spring Data JPA's Pageable-based native queries cannot generate.
 */
@Repository
class ProductQueryRepository(private val jdbcTemplate: JdbcTemplate) {

    private fun resultSetToModel(rs: ResultSet) =
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
            unidadeSaida = rs.getString("unidadeSaida"),
            unidadeEntrada = rs.getString("unidadeEntrada"),
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
            descontoPadrao = rs.getBigDecimal("descontoPadrao"),
            estoqueDisponivel = rs.getBigDecimal("estoqueDisponivel"),
            estoqueMinimo = rs.getBigDecimal("estoqueMinimo"),
            estoqueMaximo = rs.getBigDecimal("estoqueMaximo"),
            estoqueReservado = rs.getBigDecimal("estoqueReservado"),
            estoqueAdquirido = rs.getBigDecimal("estoqueAdquirido"),
            estoqueAtual = rs.getBigDecimal("estoqueAtual"),
            estoqueRMA = rs.getBigDecimal("estoqueRMA"),

            similar = rs.getString("similar"),
            quantidadeVolumes = rs.getBigDecimal("quantidadeVolumes"),
            quantidadeEmbalagem = rs.getBigDecimal("quantidadeEmbalagem"),
            localizacao = rs.getString("localizacao"),
            cubagem = rs.getBigDecimal("cubagem"),
            codigoBarrasEmbalagem = rs.getString("codigoBarrasEmbalagem"),
            ibsCClassTrib = rs.getString("ibsCClassTrib"),
            ibsCst = rs.getString("ibsCst"),
            fcpEntrada = rs.getString("fcpEntrada"),
            fcpSaida = rs.getString("fcpSaida"),
            ipiSaida = rs.getString("ipiSaida"),
            ipiEntrada = rs.getString("ipiEntrada"),
            icmSaida = rs.getString("icmSaida"),
            icmEntrada = rs.getString("icmEntrada"),
            icmStSaida = rs.getString("icmStSaida"),
            icmStEntrada = rs.getString("icmStEntrada"),
            observacao = rs.getString("observacao"),
        )

    fun findAllPaged(pageable: Pageable): Page<ProductProjectionImpl> {
        val pagedQuery = FirebirdPagination.applyFirstSkip(BASE_QUERY_ESTPRO.plus(" order by pro.codgru, pro.codsub, pro.codpro"), pageable)
        val content: List<ProductProjectionImpl> = jdbcTemplate.query(pagedQuery) { rs, _ -> resultSetToModel(rs) }
        val total = jdbcTemplate.queryForObject("select count(*) from estpro", Long::class.java) ?: 0L
        return PageImpl(content, pageable, total)
    }

    fun getProductByCodGruCodSubCodPro(codGru: String, codSub: String, codPro: String): ProductProjectionImpl? {
        val query = """
            $BASE_QUERY_ESTPRO
            where pro.codgru = ?
            and pro.codsub = ?
            and pro.codpro = ?
        """.trimIndent()
        return jdbcTemplate.query(
            query,
            { ps ->
                ps.setString(1, codGru)
                ps.setString(2, codSub)
                ps.setString(3, codPro)
            }
        ) { rs, _ -> resultSetToModel(rs) }.firstOrNull()
    }
}
