package br.com.vercel.emerionloadservice.client.dto

import java.math.BigDecimal
import java.time.LocalDate

data class VendedorIngestionDto(
    val externalId: Long,
    val nome: String,
    val apelido: String?,
    val cpfCnpj: String?,
    val telefone: String?,
    val celular: String?,
    val email: String?,
    val cidade: String?,
    val uf: String?,
    val situacao: String?,
    val saldo: BigDecimal?,
    val dataCadastro: LocalDate?
)
