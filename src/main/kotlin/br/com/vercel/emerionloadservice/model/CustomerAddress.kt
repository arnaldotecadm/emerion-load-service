package br.com.vercel.emerionloadservice.model

data class CustomerAddress(
    val codCli: Long,
    val cpfCnpj: String?,
    val enderecos: List<CustomerAddressDetail>
)
