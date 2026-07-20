package br.com.vercel.emerionloadservice.repository.projection

data class CustomerProjectionImpl(
    override val id: Long,
    override val nomeFantasia: String,
    override val razaoSocial: String,
    override val cpfCnpj: String,
    override val inscricaoEstadual: String?,
    override val regimeTributario: String?,
    override val bloqueado: Int
) : CustomerProjection
