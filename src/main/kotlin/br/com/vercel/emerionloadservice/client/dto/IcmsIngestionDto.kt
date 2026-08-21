package br.com.vercel.emerionloadservice.client.dto

data class IcmsIngestionDto(
    val cnpjEmpresa: String,
    val codigoIcms: String,
    val tipoIcms: String,
    val nomeIcms: String?,
    val ufEmitente: String?,
    val codigoRegimeTributario: String?,
    val aliquotaIcms: Double?,
    val percentualReducaoValorImposto: Double?,
    val percentualBaseCalculoIcms: Double?,
    val situacaoTributariaIcms: String?
)
