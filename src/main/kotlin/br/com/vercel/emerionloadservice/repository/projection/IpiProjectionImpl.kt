package br.com.vercel.emerionloadservice.repository.projection

data class IpiProjectionImpl(
    val flgAtivo: String?,
    val codigoIpi: String,
    val tipoIpi: String,
    val nomeIpi: String?,
    val ncmIpi: String?,
    val codigoEnquadramentoLegal: String?,
    val cstIpi: String?,
    val descricaoSituacaoTributariaIpi: String?,
    val aliquotaIpi: Double?,
    val percentualBaseCalculoIpi: Double?,
    val flgSineif20: String?,
    val codigoTextoFiscal: String?,
    val cstPis: String?,
    val descricaoSituacaoTributariaPis: String?,
    val aliquotaPis: Double?,
    val incluiDescontoSuframaPis: String?,
    val cstCofins: String?,
    val descricaoSituacaoTributariaCofins: String?,
    val aliquotaCofins: Double?,
    val incluiDescontoSuframaCofins: String?,
)
