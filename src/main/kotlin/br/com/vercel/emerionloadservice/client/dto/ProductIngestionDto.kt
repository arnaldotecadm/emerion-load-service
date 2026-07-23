package br.com.vercel.emerionloadservice.client.dto

import java.math.BigDecimal

data class ProductIngestionDto(
    val externalId: String,
    val cnpjEmpresa: String,
    val nome: String,
    val preco: BigDecimal?
)
