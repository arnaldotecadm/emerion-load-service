package br.com.vercel.emerionloadservice.repository.projection

import java.time.LocalDateTime

data class CustomerOrderHeaderProjectionImpl(
    override val codCli: Long,
    override val cpfCnpj: String?,
    override val numres: String,
    override val nronfe: String?,
    override val dteres: LocalDateTime,
    override val sitres: String?,
    override val totger: Double,
    override val totres: Double,
    override val totipi: Double,
    override val totsub: Double,
    override val totdescinc: Double
) : CustomerOrderHeaderProjection
