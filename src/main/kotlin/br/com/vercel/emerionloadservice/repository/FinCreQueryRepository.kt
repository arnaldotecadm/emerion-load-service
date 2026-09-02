package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.model.FinCre
import br.com.vercel.emerionloadservice.repository.mapper.FinCreMapper.toModel
import br.com.vercel.emerionloadservice.repository.projection.FinCrpProjectionImpl
import br.com.vercel.emerionloadservice.repository.support.FirebirdPagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import java.sql.ResultSet

private const val BASE_QUERY_FINCRE = """
    select
        cre.codemp      as codigoEmpresa,
        cre.dtecre      as dataEmissao,
        cre.numcre      as documento,
        cre.codpla      as codigoCondicaoRecebimento,
        pla.nompla      as nomeCondicaoRecebimento,
        emp.nomemp      as nomeEmpresa,
        cre.codcom      as codigoComissao,
        com.percom      as percentualComissao,
        cre.codcli      as codigoCliente,
        cli.nomcli      as nomeCliente,
        cre.codven      as codigoVendedor,
        ven.nomven      as nomeVendedor,
        cre.codtdo      as codigoTipoDocumento,
        tdo.nomtdo      as nomeTipoDocumento
    from fincre cre
    left join finpla pla on pla.codpla = cre.codpla
    left join geremp emp on emp.codemp = cre.codemp
    left join fincom com on com.codcom = cre.codcom
    left join fincli cli on cli.codcli = cre.codcli
    left join finven ven on ven.codven = cre.codven
    left join fintdo tdo on tdo.codtdo = cre.codtdo
"""

private const val BASE_QUERY_FINCRP = """
    select
        crp.codemp          as codigoEmpresa,
        crp.dtecre          as dataEmissao,
        crp.numcre          as documento,
        crp.numcrp          as numeroParcela,
        crp.flginc          as flagIncobravel,
        crp.dteinc          as dataIncobravel,
        crp.dtvcrp          as dataVencimento,
        crp.pracrp          as prazoEmDias,
        crp.vlpcrp          as valorParcela,
        crp.nosnum          as numeroBancario,
        crp.codban          as codigoBanco,
        ban.nomban          as nomeBanco,
        crp.obscrp          as observacoes,
        crp.flganu          as flagCartaAnuencia,
        crp.dteanu          as dataCartaAnuencia,
        crp.flpcrp          as flagPago
    from fincrp crp
    left join finban ban on ban.codban = crp.codban
"""

/**
 * Handles credit note queries using JdbcTemplate directly, since Firebird 1.5
 * only supports pagination via literal `FIRST`/`SKIP` values (see FirebirdPagination).
 *
 * fincre headers are paginated first; their fincrp parcelas are then fetched in a
 * single follow-up query and grouped back onto each header by composite key
 * (codemp, dtecre, numcre), mirroring the header+items pattern used for orders.
 *
 * Records are identified by the composite key (codigoEmpresa, dataEmissao, documento).
 */
@Repository
class FinCreQueryRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    private data class FinCreKey(
        val codigoEmpresa: Int,
        val dataEmissao: java.time.LocalDate?,
        val documento: String,
    )

    fun findAllPaged(pageable: Pageable): Page<FinCre> {
        val total = jdbcTemplate.queryForObject<Long>("select count(*) from fincre") ?: 0L

        val headers = findHeadersPaged(pageable)
        if (headers.isEmpty()) return PageImpl(emptyList(), pageable, total)

        val parcelasByKey = findParcelas(headers).groupBy { toKey(it) }

        val content =
            headers.map { header ->
                header.copy(
                    parcelas = parcelasByKey[toKey(header)].orEmpty().map { it.toModel() },
                )
            }

        return PageImpl(content, pageable, total)
    }

    fun findByKey(documento: String): FinCre? {
        val query = "$BASE_QUERY_FINCRE where cre.numcre = ?"

        val header =
            jdbcTemplate
                .query(
                    query,
                    { rs, _ -> mapHeader(rs) },
                    documento,
                ).firstOrNull() ?: return null

        val parcelasQuery = "$BASE_QUERY_FINCRP where crp.numcre = ? order by crp.numcrp"

        val parcelas =
            jdbcTemplate.query(
                parcelasQuery,
                { rs, _ -> mapParcela(rs) },
                documento,
            )

        return header.copy(parcelas = parcelas.map { it.toModel() })
    }

    private fun findHeadersPaged(pageable: Pageable): List<FinCre> {
        val pagedQuery =
            FirebirdPagination.applyFirstSkip(
                "$BASE_QUERY_FINCRE order by cre.numcre desc ",
                pageable,
            )
        return jdbcTemplate.query(pagedQuery) { rs, _ -> mapHeader(rs) }
    }

    // Parcelas for a page of headers fetched in one query using an IN list.
    // Values come from a prior query result (never user input), so they are safe to
    // inline as a literal IN list; Firebird 1.5 native queries can't bind IN (:list).
    private fun findParcelas(headers: List<FinCre>): List<FinCrpProjectionImpl> {
        val numcreList = headers.joinToString(",") { "'${it.documento}'" }
        val query =
            """
            $BASE_QUERY_FINCRP
            where crp.numcre in ($numcreList)
            order by crp.codemp, crp.dtecre, crp.numcre, crp.numcrp
            """.trimIndent()
        return jdbcTemplate.query(query) { rs, _ -> mapParcela(rs) }
    }

    private fun mapHeader(rs: ResultSet) =
        FinCre(
            codigoEmpresa = rs.getInt("codigoEmpresa"),
            dataEmissao = rs.getTimestamp("dataEmissao")?.toLocalDateTime()?.toLocalDate(),
            documento = rs.getBigDecimal("documento").toLong().toString(),
            codigoCondicaoRecebimento = rs.getString("codigoCondicaoRecebimento"),
            nomeCondicaoRecebimento = rs.getString("nomeCondicaoRecebimento"),
            nomeEmpresa = rs.getString("nomeEmpresa"),
            codigoComissao = rs.getString("codigoComissao"),
            percentualComissao = rs.getBigDecimal("percentualComissao")?.toDouble(),
            codigoCliente = rs.getLong("codigoCliente").takeIf { !rs.wasNull() },
            nomeCliente = rs.getString("nomeCliente"),
            codigoVendedor = rs.getLong("codigoVendedor").takeIf { !rs.wasNull() },
            nomeVendedor = rs.getString("nomeVendedor"),
            codigoTipoDocumento = rs.getString("codigoTipoDocumento"),
            nomeTipoDocumento = rs.getString("nomeTipoDocumento"),
            parcelas = emptyList(),
        )

    private fun mapParcela(rs: ResultSet) =
        FinCrpProjectionImpl(
            codigoEmpresa = rs.getInt("codigoEmpresa"),
            dataEmissao = rs.getTimestamp("dataEmissao")?.toLocalDateTime(),
            documento = rs.getBigDecimal("documento").toLong().toString(),
            numeroParcela = rs.getInt("numeroParcela").takeIf { !rs.wasNull() },
            flagIncobravel = rs.getString("flagIncobravel"),
            dataIncobravel = rs.getTimestamp("dataIncobravel")?.toLocalDateTime(),
            dataVencimento = rs.getTimestamp("dataVencimento")?.toLocalDateTime(),
            prazoEmDias = rs.getInt("prazoEmDias").takeIf { !rs.wasNull() },
            valorParcela = rs.getBigDecimal("valorParcela")?.toDouble(),
            numeroBancario = rs.getString("numeroBancario"),
            codigoBanco = rs.getString("codigoBanco"),
            nomeBanco = rs.getString("nomeBanco"),
            observacoes = rs.getString("observacoes"),
            flagCartaAnuencia = rs.getString("flagCartaAnuencia"),
            dataCartaAnuencia = rs.getTimestamp("dataCartaAnuencia")?.toLocalDateTime(),
            flagPago = rs.getString("flagPago"),
        )

    private fun toKey(h: FinCre) = FinCreKey(h.codigoEmpresa, h.dataEmissao, h.documento)

    private fun toKey(p: FinCrpProjectionImpl) = FinCreKey(p.codigoEmpresa, p.dataEmissao?.toLocalDate(), p.documento)
}
