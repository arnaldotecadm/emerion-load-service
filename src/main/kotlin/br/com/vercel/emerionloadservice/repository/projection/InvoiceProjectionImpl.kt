package br.com.vercel.emerionloadservice.repository.projection

import java.time.LocalDateTime

data class InvoiceProjectionImpl(
    val codEmp: Int,
    val codCli: Long?,
    val numres: String,
    val dteres: LocalDateTime,
    val nronfs: String?,
    val dataFaturamento: LocalDateTime?,
    val totalFaturado: Double?,
)
