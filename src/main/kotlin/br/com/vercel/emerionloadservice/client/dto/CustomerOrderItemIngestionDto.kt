package br.com.vercel.emerionloadservice.client.dto

data class CustomerOrderItemIngestionDto(
    val produto: String,
    val descricao: String?,
    val quantidade: Double,
    val valorUnitario: Double,
    val valorTotal: Double
)
