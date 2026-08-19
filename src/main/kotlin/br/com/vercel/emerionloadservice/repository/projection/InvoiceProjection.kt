package br.com.vercel.emerionloadservice.repository.projection

import java.time.LocalDateTime

interface InvoiceProjection {
    val codEmp: Int
    val codCli: Long?
    val numres: String
    val dteres: LocalDateTime
    val nronfs: String?
    val dataFaturamento: LocalDateTime?
    val totalFaturado: Double?
}

