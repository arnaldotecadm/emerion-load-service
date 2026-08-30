package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.repository.projection.ReceivableProjection
import br.com.vercel.emerionloadservice.repository.projection.ReceivableProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import java.sql.ResultSet

private const val BASE_QUERY_RECEIVABLE = """
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
"""

@Repository
class ReceivableQueryRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    private fun resultsetToModel(rs: ResultSet): ReceivableProjectionImpl =
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

    fun findAllPaged(pageable: Pageable): Page<ReceivableProjection> {
        val baseQuery = "$BASE_QUERY_RECEIVABLE order by cde.codcli, cde.seqcde"

        val pagedQuery = FirebirdPagination.applyFirstSkip(baseQuery, pageable)

        val content: List<ReceivableProjection> =
            jdbcTemplate.query(pagedQuery) { rs, _ ->
                resultsetToModel(rs)
            }

        val total = jdbcTemplate.queryForObject<Long>("select count(*) from fincde") ?: 0L
        return PageImpl(content, pageable, total)
    }

    fun findByCodCli(codCli: Long): List<ReceivableProjection> {
        val query = "$BASE_QUERY_RECEIVABLE where cde.codcli = ? order by cde.seqcde"

        return jdbcTemplate.query(query, { rs, _ ->
            resultsetToModel(rs)
        }, codCli)
    }
}
