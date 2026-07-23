package br.com.vercel.emerionloadservice.client.dto

import java.time.Instant
import java.time.LocalDateTime

data class CustomerCreditIngestionDto(
    val customerExternalId: Long,
    val cnpjEmpresa: String,
    val sequencia: String?,
    val data: Instant,
    val dataPedido: LocalDateTime?,
    val valorUtilizado: Double,
    val valorTotal: Double,
    val saldo: Double,
    val situacao: String?,
    val tipo: String
)
