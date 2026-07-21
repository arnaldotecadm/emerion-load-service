package br.com.vercel.emerionloadservice.repository.projection

import java.time.Instant
import java.time.LocalDateTime

interface CustomerCreditProjection {
    val codCli: Long
    val sequencia: String?
    val data: Instant
    val dataPedido: LocalDateTime?
    val valorUtilizado: Double
    val valorTotal: Double
    val saldo: Double
    val situacao: String?
}
