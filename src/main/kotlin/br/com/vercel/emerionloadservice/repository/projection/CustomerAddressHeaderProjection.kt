package br.com.vercel.emerionloadservice.repository.projection

interface CustomerAddressHeaderProjection {
    val codCli: Long
    val cnpjEmpresa: String?
    val cpfCnpj: String?
}
