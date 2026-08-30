package br.com.vercel.emerionloadservice.model

import java.time.LocalDate

data class Invoice(
    val codEmp: Int,
    val codCli: Long?,
    val numres: String,
    val dteres: LocalDate,
    val nronfs: String?,
    val dataFaturamento: LocalDate?,
    val totalFaturado: Double?,
) {
    val orderExternalId: String
        get() = "$codEmp-$dteres-$numres"
}
