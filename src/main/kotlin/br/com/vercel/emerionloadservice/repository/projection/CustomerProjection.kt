package br.com.vercel.emerionloadservice.repository.projection

interface CustomerProjection {
    val id: Long
    val nomeFantasia: String
    val razaoSocial: String
    val cpfCnpj: String
    val inscricaoEstadual: String?
    val regimeTributario: String?
    val bloqueado: Int
}