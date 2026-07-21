package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.data.DummyEntity
import br.com.vercel.emerionloadservice.repository.projection.CustomerCreditProjection
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerCreditRepository : PagingAndSortingRepository<DummyEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
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
            where cde.codcli = :codCli
            order by cde.seqcde
        """
    )
    fun getCreditsByCodCli(codCli: Long): List<CustomerCreditProjection>
}
