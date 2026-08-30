package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.ReceivableProjection
import br.com.vercel.emerionloadservice.repository.projection.ReceivableProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class ReceivableQueryRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun findAllPaged(pageable: Pageable): Page<ReceivableProjection> {
        val baseQuery =
            """
            select
                cde.codcli as codCli,
                cde.seqcde as sequencia,
                cde.dtecde as dataLancamento,
                cde.dteped as dataReferenciaPedido,
                cde.valcde as valorOriginal,
                cde.usacde as valorUtilizado,
                cde.sldcde as saldoAberto,
                cde.sitcde as situacao
            from fincde cde
            order by cde.codcli, cde.seqcde
            """.trimIndent()

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        val content: List<ReceivableProjection> =
            jdbcTemplate.query(pagedQuery) { rs, _ ->
                ReceivableProjectionImpl(
                    codCli = rs.getLong("codCli"),
                    sequencia = rs.getString("sequencia"),
                    dataLancamento = rs.getTimestamp("dataLancamento").toInstant(),
                    dataReferenciaPedido = rs.getTimestamp("dataReferenciaPedido")?.toLocalDateTime(),
                    valorOriginal = rs.getDouble("valorOriginal"),
                    valorUtilizado = rs.getDouble("valorUtilizado"),
                    saldoAberto = rs.getDouble("saldoAberto"),
                    situacao = rs.getString("situacao"),
                )
            }

        val total = jdbcTemplate.queryForObject("select count(*) from fincde", Long::class.java) ?: 0L
        return PageImpl(content, pageable, total)
    }

    fun findByCodCli(codCli: Long): List<ReceivableProjection> {
        val query =
            """
            select
                cde.codcli as codCli,
                cde.seqcde as sequencia,
                cde.dtecde as dataLancamento,
                cde.dteped as dataReferenciaPedido,
                cde.valcde as valorOriginal,
                cde.usacde as valorUtilizado,
                cde.sldcde as saldoAberto,
                cde.sitcde as situacao
            from fincde cde
            where cde.codcli = ?
            order by cde.seqcde
            """.trimIndent()

        return jdbcTemplate.query(query, { rs, _ ->
            ReceivableProjectionImpl(
                codCli = rs.getLong("codCli"),
                sequencia = rs.getString("sequencia"),
                dataLancamento = rs.getTimestamp("dataLancamento").toInstant(),
                dataReferenciaPedido = rs.getTimestamp("dataReferenciaPedido")?.toLocalDateTime(),
                valorOriginal = rs.getDouble("valorOriginal"),
                valorUtilizado = rs.getDouble("valorUtilizado"),
                saldoAberto = rs.getDouble("saldoAberto"),
                situacao = rs.getString("situacao"),
            )
        }, codCli)
    }
}
