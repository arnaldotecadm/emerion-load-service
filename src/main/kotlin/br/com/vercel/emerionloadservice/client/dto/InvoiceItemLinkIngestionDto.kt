package br.com.vercel.emerionloadservice.client.dto

import java.time.LocalDate

data class InvoiceItemLinkIngestionDto(
    val externalId: String,
    val cnpjEmpresa: String,
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
)

