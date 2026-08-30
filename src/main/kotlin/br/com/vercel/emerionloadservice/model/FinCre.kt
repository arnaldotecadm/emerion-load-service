package br.com.vercel.emerionloadservice.model

import java.time.LocalDate

data class FinCre(
    val codigoEmpresa: Int,
    val dataEmissao: LocalDate?,
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
    val parcelas: List<FinCrp>,
)
