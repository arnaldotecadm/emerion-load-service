package br.com.vercel.emerionloadservice.repository.projection

import java.math.BigDecimal
import java.time.LocalDateTime

data class VendedorProjectionImpl(
    override val id: Long,
    override val nome: String,
    override val apelido: String?,
    override val cpfCnpj: String?,
    override val telefone: String?,
    override val celular: String?,
    override val email: String?,
    override val cidade: String?,
    override val uf: String?,
    override val situacao: String?,
    override val saldo: BigDecimal?,
    override val dataCadastro: LocalDateTime?
) : VendedorProjection
