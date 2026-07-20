package br.com.vercel.emerionloadservice.model

data class Customer(
    val id: Long,
    val nomeFantasia: String,
    val razaoSocial: String,
    val cpfCnpj: String,
    val inscricaoEstadual: String?,
    val regimeTributario: String?,
    val bloqueado: Boolean
)