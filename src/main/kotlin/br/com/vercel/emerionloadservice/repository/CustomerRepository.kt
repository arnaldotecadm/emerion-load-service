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
                cli.codcli          as id,
                cli.apecli          as nomeFantasia,
                cli.nomcli          as razaoSocial,
                cli.cgccli          as cpfCnpj,
                cli.inscli          as inscricaoEstadual,
                reg.nomregtrib      as regimeTributario,
                case(cli.flbcli)
                    when '*' then 1
                    else 0
                end                 as bloqueado,
                cli.dtncli          as dataNascimento,
                cli.dcacli          as dataCadastro,
                cli.dteatu          as dataUltimaAtualizacao,
                cli.em1cli          as email1,
                cli.em2cli          as email2,
                cli.webcli          as website,
                cli.limcli          as limiteCredito,
                cli.obscli          as observacoes,
                cli.cnae            as cnae,
                cli.codven          as vendedorExternalId,
                ven.nomven          as nomeVendedor,
                cli.codtcl          as codigoTipoCliente,
                cli.codgcl          as codigoGrupoCliente,
                cli.codccl          as codigoCategoriaCliente
            from fincli cli
            left join finregtrib reg on reg.numregtrib = cli.regtrb
            left join finven ven     on ven.codven = cli.codven
            where cli.codcli = :codCli
        """
    )
    fun getCustomerByCodCli(codCli: Long): CustomerProjection?
}