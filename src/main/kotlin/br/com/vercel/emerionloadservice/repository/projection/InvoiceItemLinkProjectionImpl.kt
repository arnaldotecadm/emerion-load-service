package br.com.vercel.emerionloadservice.repository.projection

import java.time.LocalDateTime

data class InvoiceItemLinkProjectionImpl(
    override val codEmp: Int,
    override val numres: String,
    override val dteres: LocalDateTime,
    override val seqRe2: Int,
    override val codClp: String?,
    override val codGru: String?,
    override val codSub: String?,
    override val codPro: String?,
    override val nronfs: String?,
    override val dataFaturamento: LocalDateTime?,
    override val totalFaturado: Double?
) : InvoiceItemLinkProjection

