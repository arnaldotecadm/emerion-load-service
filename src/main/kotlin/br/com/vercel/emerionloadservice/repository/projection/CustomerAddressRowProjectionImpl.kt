package br.com.vercel.emerionloadservice.repository.projection

data class CustomerAddressRowProjectionImpl(
    override val codCli: Long,
    override val tipo: String,
    override val cep: String?,
    override val endereco: String?,
    override val numero: String?,
    override val referencia: String?,
    override val bairro: String?,
    override val cidade: String?,
    override val uf: String?,
    override val telefone: String?,
    override val telefoneContato: String?,
    override val complemento: String?,
    override val fax: String?
) : CustomerAddressRowProjection
