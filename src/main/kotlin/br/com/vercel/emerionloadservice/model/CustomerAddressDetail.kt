package br.com.vercel.emerionloadservice.model

data class CustomerAddressDetail(
    val tipo: String,
    val cep: String?,
    val endereco: String?,
    val numero: String?,
    val referencia: String?,
    val bairro: String?,
    val cidade: String?,
    val uf: String?,
    val telefone: String?,
    val telefoneContato: String?,
    val complemento: String?,
    val fax: String?
)
