package br.com.vercel.emerionloadservice.client.dto

import java.time.Instant
import java.time.LocalDateTime

data class ReceivableIngestionDto(
    val externalId: String,
    val cnpjEmpresa: String,
    val customerExternalId: Long,
    val sequencia: String?,
    val dataLancamento: Instant,
    val dataReferenciaPedido: LocalDateTime?,
    val valorOriginal: Double,
    val valorUtilizado: Double,
    val saldoAberto: Double,
    val situacao: String?
)

