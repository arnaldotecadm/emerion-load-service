package br.com.vercel.emerionloadservice.repository.projection

import java.time.LocalDateTime

interface InvoiceItemLinkProjection {
    val codEmp: Int
    val numres: String
    val dteres: LocalDateTime
    val seqRe2: Int
    val codClp: String?
    val codGru: String?
    val codSub: String?
    val codPro: String?
    val nronfs: String?
    val dataFaturamento: LocalDateTime?
    val totalFaturado: Double?
}
