package br.com.vercel.emerionloadservice.model

data class Icms(
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
