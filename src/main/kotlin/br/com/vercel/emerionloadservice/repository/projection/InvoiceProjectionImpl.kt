package br.com.vercel.emerionloadservice.repository.projection

import java.time.LocalDateTime

data class InvoiceProjectionImpl(
    override val codEmp: Int,
    override val codCli: Long?,
    override val numres: String,
    override val dteres: LocalDateTime,
    override val nronfs: String?,
    override val dataFaturamento: LocalDateTime?,
    override val totalFaturado: Double?,
) : InvoiceProjection
