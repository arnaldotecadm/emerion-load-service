package br.com.vercel.emerionloadservice.repository.projection

import java.math.BigDecimal
import java.time.LocalDateTime

interface VendedorProjection {
    val id: Long
    val nome: String
    val apelido: String?
    val cpfCnpj: String?
    val telefone: String?
    val celular: String?
    val email: String?
    val cidade: String?
    val uf: String?
    val situacao: String?
    val saldo: BigDecimal?
    val dataCadastro: LocalDateTime?
}
