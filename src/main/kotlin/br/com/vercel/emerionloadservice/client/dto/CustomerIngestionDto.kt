package br.com.vercel.emerionloadservice.client.dto

data class CustomerIngestionDto(
    val externalId: Long,
    val cnpjEmpresa: String,
    val nomeFantasia: String,
    val razaoSocial: String,
    val cpfCnpj: String,
    val inscricaoEstadual: String?,
    val regimeTributario: String?,
    val bloqueado: Boolean
)
