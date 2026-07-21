package br.com.vercel.emerionloadservice.client.dto

data class CustomerAddressIngestionDto(
    val externalId: Long,
    val cnpjEmpresa: String?,
    val cpfCnpj: String?,
    val enderecos: List<CustomerAddressDetailIngestionDto>
)
