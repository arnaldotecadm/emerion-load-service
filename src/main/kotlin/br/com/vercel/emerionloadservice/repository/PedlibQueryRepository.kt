package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.model.Pedlb2
import br.com.vercel.emerionloadservice.model.Pedlib
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import java.sql.ResultSet

private const val BASE_QUERY_PEDLIB = """
    select
        lib.codemp as codigoEmpresa,
        lib.dteres as dataPedido,
        lib.numres as numeroPedido,
        lib.seqlib as numeroLiberacao,
        lib.dtelib as dataLiberacao,
        lib.hrelib as horaLiberacao,
        lib.codcli as codigoCliente,
        lib.qtslib as quantidadeSeparada,
        lib.totlib as totalLiberadoSemImpostos,
        lib.totger as totalLiberadoComImpostos,
        lib.sitlib as situacaoLiberacao,
        lib.codven as codigoVendedor,
        lib.pcolib as comissaoLiberacao,
        lib.totcst as totalCusto
    from pedlib lib
"""

private const val BASE_QUERY_PEDLB2 = """
    select
        lb2.codemp as codigoEmpresa,
        lb2.dteres as dataPedido,
        lb2.numres as numeroPedido,
        lb2.seqlib as numeroLiberacao,
        lb2.seqlb2 as numeroSequenciaLiberacao,
        lb2.codclp as classificacaoItem,
        lb2.codgru as codigoGrupo,
        lb2.codsub as codigoSubGrupo,
        lb2.codpro as codigoProduto,
        lb2.deslb2 as descricaoItemLiberacao,
        lb2.qtplb2 as quantidadeNoPedido,
        lb2.qtslb2 as totalSeparado,
        lb2.sldlb2 as quantidadeRestante,
            
        lb2.totlb2 as totalValorLiquido,
        lb2.totge2 as totalValorBruto,
        lb2.DscLb2 as percentualDesconto,
        lb2.TotCst as totalCusto,
        lb2.PacLb2 as percentualDeAcrescimo,
        lb2.VluLb2 as precoVendaItem,
        lb2.vlqlb2 as precoPraticado,
        lb2.vcslb2 as custoPraticado
    from pedlb2 lb2
"""

@Repository
class PedlibQueryRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    private data class PedlibKey(
        val codigoEmpresa: Int,
        val dataPedido: java.time.LocalDate?,
        val numeroPedido: String,
        val numeroLiberacao: Int,
    )

    fun findAllPaged(pageable: Pageable): Page<Pedlib> {
        val total = jdbcTemplate.queryForObject<Long>("select count(*) from pedlib") ?: 0L
        val headers = findHeadersPaged(pageable)
        if (headers.isEmpty()) return PageImpl(emptyList(), pageable, total)

        val detailsByKey = findDetails(headers).groupBy { toKey(it) }
        val content =
            headers.map { header ->
                header.copy(detalhes = detailsByKey[toKey(header)].orEmpty())
            }

        return PageImpl(content, pageable, total)
    }

    fun findByKey(numres: String): Pedlib? {
        val headerQuery = "$BASE_QUERY_PEDLIB where lib.numres = ? order by lib.seqlib"
        val header =
            jdbcTemplate
                .query(headerQuery, { rs, _ -> mapHeader(rs) }, numres)
                .firstOrNull() ?: return null

        val detailsQuery =
            """
            $BASE_QUERY_PEDLB2
            where lb2.numres = ? and lb2.seqlib = ?
            order by lb2.seqlb2
            """.trimIndent()
        val details =
            jdbcTemplate.query(
                detailsQuery,
                { rs, _ -> mapDetail(rs) },
                numres,
                header.numeroLiberacao,
            )

        return header.copy(detalhes = details)
    }

    private fun findHeadersPaged(pageable: Pageable): List<Pedlib> {
        val query =
            FirebirdPagination.applyFirstSkip(
                "$BASE_QUERY_PEDLIB order by lib.numres desc, lib.seqlib",
                pageable,
            )
        return jdbcTemplate.query(query) { rs, _ -> mapHeader(rs) }
    }

    private fun findDetails(headers: List<Pedlib>): List<Pedlb2> {
        val numresList = headers.joinToString(",") { it.numeroPedido }
        val details =
            jdbcTemplate.query(
                """
                $BASE_QUERY_PEDLB2
                where lb2.numres in ($numresList)
                order by lb2.codemp, lb2.dteres, lb2.numres, lb2.seqlib, lb2.seqlb2
                """.trimIndent(),
            ) { rs, _ -> mapDetail(rs) }

        val headerKeys = headers.map(::toKey).toSet()
        return details.filter { toKey(it) in headerKeys }
    }

    private fun mapHeader(rs: ResultSet) =
        Pedlib(
            codigoEmpresa = rs.getInt("codigoEmpresa"),
            dataPedido = rs.getTimestamp("dataPedido")?.toLocalDateTime()?.toLocalDate(),
            numeroPedido = rs.getString("numeroPedido"),
            numeroLiberacao = rs.getInt("numeroLiberacao"),
            dataLiberacao = rs.getTimestamp("dataLiberacao")?.toLocalDateTime()?.toLocalDate(),
            horaLiberacao = rs.getString("horaLiberacao"),
            codigoCliente = rs.getLong("codigoCliente").takeIf { !rs.wasNull() },
            quantidadeSeparada = rs.getInt("quantidadeSeparada").takeIf { !rs.wasNull() },
            totalLiberadoSemImpostos = rs.getBigDecimal("totalLiberadoSemImpostos")?.toDouble(),
            totalLiberadoComImpostos = rs.getBigDecimal("totalLiberadoComImpostos")?.toDouble(),
            situacaoLiberacao = rs.getString("situacaoLiberacao"),
            codigoVendedor = rs.getLong("codigoVendedor").takeIf { !rs.wasNull() },
            comissaoLiberacao = rs.getBigDecimal("comissaoLiberacao")?.toDouble(),
            totalCusto = rs.getBigDecimal("totalCusto")?.toDouble(),
            detalhes = emptyList(),
        )

    private fun mapDetail(rs: ResultSet) =
        Pedlb2(
            codigoEmpresa = rs.getInt("codigoEmpresa"),
            dataPedido = rs.getTimestamp("dataPedido")?.toLocalDateTime(),
            numeroPedido = rs.getString("numeroPedido"),
            numeroLiberacao = rs.getInt("numeroLiberacao"),
            numeroSequenciaLiberacao = rs.getInt("numeroSequenciaLiberacao"),
            classificacaoItem = rs.getString("classificacaoItem"),
            codigoGrupo = rs.getString("codigoGrupo"),
            codigoSubGrupo = rs.getString("codigoSubGrupo"),
            codigoProduto = rs.getString("codigoProduto"),
            descricaoItemLiberacao = rs.getString("descricaoItemLiberacao"),
            quantidadeNoPedido = rs.getBigDecimal("quantidadeNoPedido")?.toDouble(),
            totalSeparado = rs.getBigDecimal("totalSeparado")?.toDouble(),
            quantidadeRestante = rs.getBigDecimal("quantidadeRestante")?.toDouble(),
            totalValorLiquido = rs.getBigDecimal("totalValorLiquido")?.toDouble(),
            totalValorBruto = rs.getBigDecimal("totalValorBruto")?.toDouble(),
            percentualDesconto = rs.getBigDecimal("percentualDesconto")?.toDouble(),
            totalCusto = rs.getBigDecimal("totalCusto")?.toDouble(),
            percentualDeAcrescimo = rs.getBigDecimal("percentualDeAcrescimo")?.toDouble(),
            precoVendaItem = rs.getBigDecimal("precoVendaItem")?.toDouble(),
            precoPraticado = rs.getBigDecimal("precoPraticado")?.toDouble(),
            custoPraticado = rs.getBigDecimal("custoPraticado")?.toDouble(),
        )

    private fun toKey(header: Pedlib) =
        PedlibKey(
            header.codigoEmpresa,
            header.dataPedido,
            header.numeroPedido,
            header.numeroLiberacao,
        )

    private fun toKey(detail: Pedlb2) =
        PedlibKey(
            detail.codigoEmpresa,
            detail.dataPedido?.toLocalDate(),
            detail.numeroPedido,
            detail.numeroLiberacao,
        )
}
