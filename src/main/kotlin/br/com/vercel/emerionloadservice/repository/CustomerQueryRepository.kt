package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.CustomerProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

const val BASE_QUERY = """
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
                cli.codccl          as codigoCategoriaCliente,
                cli.uffcli          as uf,
                cli.codmcr          as macroRegiao,
                cli.codmrg          as microRegiao,
                cli.codset          as setor,
                
                -- Endereco de Faturamento
                cli.cefcli          as faturamentoCep,
                cli.tefcli          as faturamentoTipoEndereco,
                cli.enfcli          as faturamentoEndereco,
                cli.nrfcli          as faturamentoNumero,
                cli.rffcli          as faturamentoComplemento,
                cli.bafcli          as faturamentoBairro,
                cli.cifcli          as faturamentoCidade,
                cli.uffcli          as faturamentoUf,
                cli.pt1cli          as faturamentoDDDTelefone,
                cli.fo1cli          as faturamentoTelefone,
                cli.pf1cli          as faturamentoDDDFax,
                cli.fa1cli          as faturamentoFax,
                cli.cofcli          as faturamentoContato,
                cli.pc1cli          as faturamentoDDDCelular,
                cli.fc1cli          as faturamentoCelular,
                
                -- Endereco de Cobranca
                cli.ceccli          as cobrancaCep,
                cli.teccli          as cobrancaTipoEndereco,
                cli.enccli          as cobrancaEndereco,
                cli.nrccli          as cobrancaNumero,
                cli.rfccli          as cobrancaComplemento,
                cli.baccli          as cobrancaBairro,
                cli.ciccli          as cobrancaCidade,
                cli.ufccli          as cobrancaUf,
                cli.pt2cli          as cobrancaDDDTelefone,
                cli.fo2cli          as cobrancaTelefone,
                cli.pf2cli          as cobrancaDDDFax,
                cli.fa2cli          as cobrancaFax,
                cli.coccli          as cobrancaContato,
                cli.pc2cli          as cobrancaDDDCelular,
                cli.fc2cli          as cobrancaCelular,
                
                -- Endereco de Entrega
                cli.ceecli          as entregaCep,
                cli.teecli          as entregaTipoEndereco,
                cli.enecli          as entregaEndereco,
                cli.nrecli          as entregaNumero,
                cli.rfecli          as entregaComplemento,
                cli.baecli          as entregaBairro,
                cli.ciecli          as entregaCidade,
                cli.ufecli          as entregaUf,
                cli.pt4cli          as entregaDDDTelefone,
                cli.fo4cli          as entregaTelefone,
                cli.pf4cli          as entregaDDDFax,
                cli.fa4cli          as entregaFax,
                cli.coecli          as entregaContato,
                cli.pc4cli          as entregaDDDCelular,
                cli.fc4cli          as entregaCelular
            from fincli cli
            left join finregtrib reg on reg.numregtrib = cli.regtrb
            left join finven ven     on ven.codven = cli.codven
        """

/**
 * Handles paginated customer queries using JdbcTemplate directly, since Firebird 1.5
 * only supports pagination via literal `FIRST`/`SKIP` values (see FirebirdPagination),
 * which Spring Data JPA's Pageable-based native queries cannot generate.
 */
@Repository
class CustomerQueryRepository(private val jdbcTemplate: JdbcTemplate) {

    private fun resultSetToModel(rs: ResultSet): CustomerProjectionImpl = CustomerProjectionImpl(
        id = rs.getLong("id"),
        nomeFantasia = rs.getString("nomeFantasia"),
        razaoSocial = rs.getString("razaoSocial"),
        cpfCnpj = rs.getString("cpfCnpj"),
        inscricaoEstadual = rs.getString("inscricaoEstadual"),
        regimeTributario = rs.getString("regimeTributario"),
        bloqueado = rs.getInt("bloqueado"),
        dataNascimento = rs.getTimestamp("dataNascimento")?.toLocalDateTime(),
        dataCadastro = rs.getTimestamp("dataCadastro")?.toLocalDateTime(),
        dataUltimaAtualizacao = rs.getTimestamp("dataUltimaAtualizacao")?.toLocalDateTime(),
        email1 = rs.getString("email1"),
        email2 = rs.getString("email2"),
        website = rs.getString("website"),
        limiteCredito = rs.getBigDecimal("limiteCredito"),
        observacoes = rs.getString("observacoes"),
        cnae = rs.getString("cnae"),
        vendedorExternalId = rs.getLong("vendedorExternalId").takeIf { !rs.wasNull() },
        nomeVendedor = rs.getString("nomeVendedor"),
        codigoTipoCliente = rs.getString("codigoTipoCliente"),
        codigoGrupoCliente = rs.getString("codigoGrupoCliente"),
        codigoCategoriaCliente = rs.getString("codigoCategoriaCliente"),
        uf = rs.getString("uf"),
        macroRegiao = rs.getString("macroRegiao"),
        microRegiao = rs.getString("microRegiao"),
        setor = rs.getString("setor"),

        faturamentoCep = rs.getString("faturamentoCep"),
        faturamentoTipoEndereco = rs.getString("faturamentoTipoEndereco"),
        faturamentoEndereco = rs.getString("faturamentoEndereco"),
        faturamentoNumero = rs.getString("faturamentoNumero"),
        faturamentoComplemento = rs.getString("faturamentoComplemento"),
        faturamentoBairro = rs.getString("faturamentoBairro"),
        faturamentoCidade = rs.getString("faturamentoCidade"),
        faturamentoUf = rs.getString("faturamentoUf"),
        faturamentoDDDTelefone = rs.getString("faturamentoDDDTelefone"),
        faturamentoTelefone = rs.getString("faturamentoTelefone"),
        faturamentoDDDFax = rs.getString("faturamentoDDDFax"),
        faturamentoFax = rs.getString("faturamentoFax"),
        faturamentoContato = rs.getString("faturamentoContato"),
        faturamentoDDDCelular = rs.getString("faturamentoDDDCelular"),
        faturamentoCelular = rs.getString("faturamentoCelular"),

        cobrancaCep = rs.getString("cobrancaCep"),
        cobrancaTipoEndereco = rs.getString("cobrancaTipoEndereco"),
        cobrancaEndereco = rs.getString("cobrancaEndereco"),
        cobrancaNumero = rs.getString("cobrancaNumero"),
        cobrancaComplemento = rs.getString("cobrancaComplemento"),
        cobrancaBairro = rs.getString("cobrancaBairro"),
        cobrancaCidade = rs.getString("cobrancaCidade"),
        cobrancaUf = rs.getString("cobrancaUf"),
        cobrancaDDDTelefone = rs.getString("cobrancaDDDTelefone"),
        cobrancaTelefone = rs.getString("cobrancaTelefone"),
        cobrancaDDDFax = rs.getString("cobrancaDDDFax"),
        cobrancaFax = rs.getString("cobrancaFax"),
        cobrancaContato = rs.getString("cobrancaContato"),
        cobrancaDDDCelular = rs.getString("cobrancaDDDCelular"),
        cobrancaCelular = rs.getString("cobrancaCelular"),

        entregaCep = rs.getString("entregaCep"),
        entregaTipoEndereco = rs.getString("entregaTipoEndereco"),
        entregaEndereco = rs.getString("entregaEndereco"),
        entregaNumero = rs.getString("entregaNumero"),
        entregaComplemento = rs.getString("entregaComplemento"),
        entregaBairro = rs.getString("entregaBairro"),
        entregaCidade = rs.getString("entregaCidade"),
        entregaUf = rs.getString("entregaUf"),
        entregaDDDTelefone = rs.getString("entregaDDDTelefone"),
        entregaTelefone = rs.getString("entregaTelefone"),
        entregaDDDFax = rs.getString("entregaDDDFax"),
        entregaFax = rs.getString("entregaFax"),
        entregaContato = rs.getString("entregaContato"),
        entregaDDDCelular = rs.getString("entregaDDDCelular"),
        entregaCelular = rs.getString("entregaCelular")
    )

    fun findAllPaged(pageable: Pageable): Page<CustomerProjectionImpl> {
        val pagedQuery = FirebirdPagination.applyFirstSkip(BASE_QUERY.plus(" order by cli.codcli"), pageable)
        val content: List<CustomerProjectionImpl> = jdbcTemplate.query(pagedQuery) { rs, _ -> resultSetToModel(rs) }
        val total = jdbcTemplate.queryForObject("select count(*) from fincli", Long::class.java) ?: 0L
        return PageImpl(content, pageable, total)
    }


    fun getCustomerByCodCli(codCli: Long): CustomerProjectionImpl? {
        val query = """
            $BASE_QUERY
            WHERE cli.codcli = ?
        """.trimIndent()
        return jdbcTemplate.query(
            query,
            { ps -> ps.setLong(1, codCli) }
        ) { rs, _ -> resultSetToModel(rs) }.firstOrNull()
    }
}
