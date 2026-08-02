package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.CustomerProjection
import br.com.vercel.emerionloadservice.repository.projection.CustomerProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

/**
 * Handles paginated customer queries using JdbcTemplate directly, since Firebird 1.5
 * only supports pagination via literal `FIRST`/`SKIP` values (see FirebirdPagination),
 * which Spring Data JPA's Pageable-based native queries cannot generate.
 */
@Repository
class CustomerQueryRepository(private val jdbcTemplate: JdbcTemplate) {

    fun findAllPaged(pageable: Pageable): Page<CustomerProjection> {
        val baseQuery = """
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
            order by cli.codcli
        """.trimIndent()

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        val content: List<CustomerProjection> = jdbcTemplate.query(pagedQuery) { rs, _ ->
            CustomerProjectionImpl(
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
            )
        }

        val total = jdbcTemplate.queryForObject("select count(*) from fincli", Long::class.java) ?: 0L

        return PageImpl(content, pageable, total)
    }
}
