package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.data.DummyEntity
import br.com.vercel.emerionloadservice.repository.projection.CustomerAddressHeaderProjection
import br.com.vercel.emerionloadservice.repository.projection.CustomerAddressRowProjection
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerAddressRepository : PagingAndSortingRepository<DummyEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            select
                codcli as codCli,
                (select cgcemp from geremp where codemp = 1) as cnpjEmpresa,
                cgccli as cpfCnpj
            from fincli
            where codcli = :codCli
        """
    )
    fun getHeaderByCodCli(codCli: Long): CustomerAddressHeaderProjection

    // Firebird 1.5 does not support UNION inside a derived table (subquery in FROM),
    // so the codCli filter is repeated in every branch and ORDER BY is applied to the
    // union as a whole instead of wrapping it in an outer select. It also requires
    // ORDER BY on a UNION to reference column position rather than the alias from the
    // first branch, hence "order by 2" (tipo).
    @Query(
        nativeQuery = true,
        value = """
            select
                codcli as codCli, cast('FATURAMENTO' as varchar(20)) as tipo,
                cefcli as cep, tefcli as telefone, enfcli as endereco, nrfcli as numero,
                rffcli as referencia, bafcli as bairro, cifcli as cidade, uffcli as uf,
                pt1cli || '-' || fo1cli as telefoneContato, cofcli as complemento,
                pc1cli || '-' || fc1cli as fax
            from fincli
            where codcli = :codCli
            union all
            select
                codcli, cast('COBRANCA' as varchar(20)),
                ceccli, teccli, enccli, nrccli,
                rfccli, baccli, ciccli, ufccli,
                pt2cli || '-' || fo2cli, coccli,
                pc2cli || '-' || fc2cli
            from fincli
            where codcli = :codCli
            union all
            select
                codcli, cast('COMPRAS' as varchar(20)),
                ceacli, teacli, enacli, nracli,
                rfacli, baacli, ciacli, ufacli,
                pt3cli || '-' || fo3cli, comcli,
                pc3cli || '-' || fc3cli
            from fincli
            where codcli = :codCli
            union all
            select
                codcli, cast('ENTREGA' as varchar(20)),
                ceecli, teecli, enecli, nrecli,
                rfecli, baecli, ciecli, ufecli,
                pt4cli || '-' || fo4cli, coecli,
                pc4cli || '-' || fc4cli
            from fincli
            where codcli = :codCli
            order by 2
        """
    )
    fun getAddressRowsByCodCli(codCli: Long): List<CustomerAddressRowProjection>
}
