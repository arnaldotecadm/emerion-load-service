package br.com.vercel.emerionloadservice.model

data class CustomerAddress(
    val codCli: Long,
    val cnpjEmpresa: String?,
    val cpfCnpj: String?,
    val enderecos: List<CustomerAddressDetail>
)
