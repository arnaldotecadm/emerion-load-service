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
            where pro.codgru = :codGru
            and pro.codsub = :codSub
            and pro.codpro = :codPro
        """
    )
    fun getProductByCodGruCodSubCodPro(codGru: String, codSub: String, codPro: String): ProductProjection
}