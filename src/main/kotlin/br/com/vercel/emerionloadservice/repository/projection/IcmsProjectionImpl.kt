package br.com.vercel.emerionloadservice.repository.projection

data class IcmsProjectionImpl(
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
