package br.com.vercel.emerionloadservice.model

import java.time.LocalDate

data class InvoiceItemLink(
    val codEmp: Int,
    val numres: String,
    val dteres: LocalDate,
    val seqRe2: Int,
    val codClp: String?,
    val codGru: String?,
    val codSub: String?,
    val codPro: String?,
    val nronfs: String?,
    val dataFaturamento: LocalDate?,
    val totalFaturado: Double?
) {
    val orderExternalId: String
        get() = "$codEmp-$dteres-$numres"
}

