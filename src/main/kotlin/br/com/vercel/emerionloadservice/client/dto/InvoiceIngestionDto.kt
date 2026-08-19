package br.com.vercel.emerionloadservice.client.dto

import java.time.LocalDate

data class InvoiceIngestionDto(
    val externalId: String,
    val cnpjEmpresa: String,
    val codEmp: Int,
    val customerExternalId: Long?,
    val numres: String,
    val dteres: LocalDate,
    val nronfs: String?,
    val dataFaturamento: LocalDate?,
    val totalFaturado: Double?
)

