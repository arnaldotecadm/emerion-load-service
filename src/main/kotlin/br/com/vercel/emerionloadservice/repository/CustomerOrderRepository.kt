package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.data.DummyEntity
import br.com.vercel.emerionloadservice.repository.projection.CustomerOrderHeaderProjection
import br.com.vercel.emerionloadservice.repository.projection.CustomerOrderItemProjection
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerOrderRepository : PagingAndSortingRepository<DummyEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
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
            where p.numres = :numres
        """
    )
    fun getHeaderByNumres(numres: String): CustomerOrderHeaderProjection

    @Query(
        nativeQuery = true,
        value = """
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
            where re2.numres = :numres
            order by re2.seqre2
        """
    )
    fun getItemsByNumres(numres: String): List<CustomerOrderItemProjection>
}
