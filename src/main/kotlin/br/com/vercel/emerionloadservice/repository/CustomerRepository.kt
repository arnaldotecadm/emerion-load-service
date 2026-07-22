package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.data.DummyEntity
import br.com.vercel.emerionloadservice.repository.projection.CustomerProjection
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : PagingAndSortingRepository<DummyEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            select
                cli.codcli as id,
                cli.apecli as nomeFantasia,
                cli.nomcli as razaoSocial,
                cli.cgccli as cpfCnpj,
                cli.inscli as inscricaoEstadual,
                reg.nomregtrib as regimeTributario,
                case(cli.flbcli)
                    when '*' then 1
                    else 0
                end as bloqueado
            from fincli cli
            left join finregtrib reg on reg.numregtrib = cli.regtrb
            where 1 = 1
            --and cli.regtrb is not null
            and cli.codcli = :codCli
        """
    )
    fun getCustomerByCodCli(codCli: Long): CustomerProjection
}