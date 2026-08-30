package br.com.vercel.emerionloadservice.repository.projection

import java.time.LocalDateTime

data class FinCreProjectionImpl(
    val codigoEmpresa: Int,
    val dataEmissao: LocalDateTime?,
    val documento: String,
    val codigoCondicaoRecebimento: String?,
    val nomeCondicaoRecebimento: String?,
    val nomeEmpresa: String?,
    val codigoComissao: String?,
    val percentualComissao: Double?,
    val codigoCliente: Long?,
    val nomeCliente: String?,
    val codigoVendedor: Long?,
    val nomeVendedor: String?,
    val codigoTipoDocumento: String?,
    val nomeTipoDocumento: String?,
)
