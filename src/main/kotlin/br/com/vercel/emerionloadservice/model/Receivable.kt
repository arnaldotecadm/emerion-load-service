package br.com.vercel.emerionloadservice.model

import java.time.Instant
import java.time.LocalDateTime

data class Receivable(
    val codCli: Long,
    val sequencia: String?,
    val dataLancamento: Instant,
    val dataReferenciaPedido: LocalDateTime?,
    val valorOriginal: Double,
    val valorUtilizado: Double,
    val saldoAberto: Double,
    val situacao: String?,
)
