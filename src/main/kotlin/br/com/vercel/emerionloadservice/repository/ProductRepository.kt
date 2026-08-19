package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.data.DummyEntity
import br.com.vercel.emerionloadservice.repository.projection.ProductProjection
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : PagingAndSortingRepository<DummyEntity, Long>{

    @Query(
        nativeQuery = true,
        value = """
            select * from geremp
        """
    )
    fun getProducts() : List<Any>

    @Query(
        nativeQuery = true,
        value = """
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
                ite.vb5ite          as preco5,
                pro.idepro          as descontoPadrao,
                ite.qtsite          as estoqueDisponivel,
                ite.qtmite          as estoqueMinimo,
                ite.qtrite          as estoqueReservado,
                ite.qtaite          as estoqueAdquirido
            from estpro pro
            left join estite ite on ite.codclp = pro.codclp
                and ite.codgru = pro.codgru
                and ite.codsub = pro.codsub
                and ite.codpro = pro.codpro
                and ite.codemp = (
                    select first 1 i2.codemp
                    from estite i2
                    where i2.codclp = pro.codclp
                    and i2.codgru = pro.codgru
                    and i2.codsub = pro.codsub
                    and i2.codpro = pro.codpro
                )
            where pro.codgru = :codGru
            and pro.codsub = :codSub
            and pro.codpro = :codPro
        """
    )
    fun getProductByCodGruCodSubCodPro(codGru: String, codSub: String, codPro: String): ProductProjection?
}