package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.model.CustomerAddress
import br.com.vercel.emerionloadservice.repository.mapper.CustomerAddressMapper.toModel
import br.com.vercel.emerionloadservice.repository.projection.CustomerAddressHeaderProjection
import br.com.vercel.emerionloadservice.repository.projection.CustomerAddressHeaderProjectionImpl
import br.com.vercel.emerionloadservice.repository.projection.CustomerAddressRowProjection
import br.com.vercel.emerionloadservice.repository.projection.CustomerAddressRowProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

/**
 * Handles paginated customer address queries using JdbcTemplate directly, since Firebird 1.5
 * only supports pagination via literal `FIRST`/`SKIP` values (see FirebirdPagination),
 * which Spring Data JPA's Pageable-based native queries cannot generate.
 *
 * Mirrors the header + address list shape from the legacy emerion-cliente-loader: customers
 * are paginated first (one page item per customer), then their address detail rows
 * (Faturamento, Cobranca, Compras, Entrega) are fetched in a single follow-up query and
 * grouped back onto each customer.
 */
@Repository
class CustomerAddressQueryRepository(private val jdbcTemplate: JdbcTemplate) {

    fun findAllPaged(pageable: Pageable): Page<CustomerAddress> {
        val total = jdbcTemplate.queryForObject("select count(*) from fincli", Long::class.java) ?: 0L
        val headers = findHeadersPaged(pageable)
        if (headers.isEmpty()) {
            return PageImpl(emptyList(), pageable, total)
        }

        val rowsByCodCli = findAddressRows(headers.map { it.codCli }).groupBy { it.codCli }
        val content = headers.map { header -> header.toModel(rowsByCodCli[header.codCli].orEmpty()) }

        return PageImpl(content, pageable, total)
    }

    private fun findHeadersPaged(pageable: Pageable): List<CustomerAddressHeaderProjection> {
        val baseQuery = """
            select
                codcli as codCli,
                (select cgcemp from geremp where codemp = 1) as cnpjEmpresa,
                cgccli as cpfCnpj
            from fincli
            order by codcli
        """.trimIndent()

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        return jdbcTemplate.query(pagedQuery) { rs, _ ->
            CustomerAddressHeaderProjectionImpl(
                codCli = rs.getLong("codCli"),
                cnpjEmpresa = rs.getString("cnpjEmpresa"),
                cpfCnpj = rs.getString("cpfCnpj")
            )
        }
    }

    private fun findAddressRows(codClis: List<Long>): List<CustomerAddressRowProjection> {
        // Values come from a prior query result (never user input), so they are safe to
        // inline as a literal IN list; Firebird 1.5 native queries can't bind IN (:list).
        val idList = codClis.joinToString(",")

        // Firebird 1.5 does not support UNION inside a derived table (subquery in FROM), so
        // the codCli filter is repeated in every branch and ORDER BY references column
        // position (1 = codCli, 2 = tipo) applied to the union as a whole.
        val query = """
            select
                codcli as codCli, cast('FATURAMENTO' as varchar(20)) as tipo,
                cefcli as cep, tefcli as telefone, enfcli as endereco, nrfcli as numero,
                rffcli as referencia, bafcli as bairro, cifcli as cidade, uffcli as uf,
                pt1cli || '-' || fo1cli as telefoneContato, cofcli as complemento,
                pc1cli || '-' || fc1cli as fax
            from fincli
            where codcli in ($idList)
            union all
            select
                codcli, cast('COBRANCA' as varchar(20)),
                ceccli, teccli, enccli, nrccli,
                rfccli, baccli, ciccli, ufccli,
                pt2cli || '-' || fo2cli, coccli,
                pc2cli || '-' || fc2cli
            from fincli
            where codcli in ($idList)
            union all
            select
                codcli, cast('COMPRAS' as varchar(20)),
                ceacli, teacli, enacli, nracli,
                rfacli, baacli, ciacli, ufacli,
                pt3cli || '-' || fo3cli, comcli,
                pc3cli || '-' || fc3cli
            from fincli
            where codcli in ($idList)
            union all
            select
                codcli, cast('ENTREGA' as varchar(20)),
                ceecli, teecli, enecli, nrecli,
                rfecli, baecli, ciecli, ufecli,
                pt4cli || '-' || fo4cli, coecli,
                pc4cli || '-' || fc4cli
            from fincli
            where codcli in ($idList)
            order by 1, 2
        """.trimIndent()

        return jdbcTemplate.query(query) { rs, _ ->
            CustomerAddressRowProjectionImpl(
                codCli = rs.getLong("codCli"),
                tipo = rs.getString("tipo"),
                cep = rs.getString("cep"),
                endereco = rs.getString("endereco"),
                numero = rs.getString("numero"),
                referencia = rs.getString("referencia"),
                bairro = rs.getString("bairro"),
                cidade = rs.getString("cidade"),
                uf = rs.getString("uf"),
                telefone = rs.getString("telefone"),
                telefoneContato = rs.getString("telefoneContato"),
                complemento = rs.getString("complemento"),
                fax = rs.getString("fax")
            )
        }
    }
}
