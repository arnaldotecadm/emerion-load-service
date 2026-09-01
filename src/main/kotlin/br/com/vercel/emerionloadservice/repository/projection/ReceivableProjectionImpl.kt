package br.com.vercel.emerionloadservice.repository.projection

import java.time.Instant
import java.time.LocalDateTime

data class ReceivableProjectionImpl(
    val codCli: Long,
    val sequencia: String?,
    val dataLancamento: Instant,
    val dataReferenciaPedido: LocalDateTime?,
    val valorOriginal: Double,
    val valorUtilizado: Double,
    val saldoAberto: Double,
    val situacao: String?,
)
