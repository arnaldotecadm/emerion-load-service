package br.com.vercel.emerionloadservice.repository.projection

data class CustomerAddressHeaderProjectionImpl(
    override val codCli: Long,
    override val cnpjEmpresa: String?,
    override val cpfCnpj: String?
) : CustomerAddressHeaderProjection
