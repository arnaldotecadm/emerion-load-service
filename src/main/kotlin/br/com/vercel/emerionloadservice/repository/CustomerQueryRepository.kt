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
            where cli.regtrb is not null
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
                bloqueado = rs.getInt("bloqueado")
            )
        }

        val total = jdbcTemplate.queryForObject(
            "select count(*) from fincli cli where cli.regtrb is not null",
            Long::class.java
        ) ?: 0L

        return PageImpl(content, pageable, total)
    }
}
