package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.model.CustomerCredit
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class CustomerCreditRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun getCreditsByCodCli(codCli: Long): List<CustomerCredit> =
        jdbcTemplate.query(
            """
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
            where cde.codcli = ?
            order by cde.seqcde
        """,
            { rs, _ ->
                CustomerCredit(
                    codCli = rs.getLong("codCli"),
                    sequencia = rs.getString("sequencia"),
                    data = rs.getTimestamp("data")?.toLocalDateTime()?.toLocalDate() ?: throw IllegalStateException("data cannot be null"),
                    dataPedido = rs.getTimestamp("dataPedido")?.toLocalDateTime()?.toLocalDate(),
                    valorUtilizado = rs.getDouble("valorUtilizado"),
                    valorTotal = rs.getDouble("valorTotal"),
                    saldo = rs.getDouble("saldo"),
                    situacao = rs.getString("situacao"),
                )
            },
            codCli,
        )
}
