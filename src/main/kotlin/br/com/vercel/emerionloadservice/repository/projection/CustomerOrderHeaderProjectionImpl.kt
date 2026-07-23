package br.com.vercel.emerionloadservice.repository.projection

import java.time.Instant

data class CustomerOrderHeaderProjectionImpl(
    override val codCli: Long,
    override val numres: String,
    override val nronfe: String?,
    override val dteres: Instant,
    override val sitres: String?,
    override val totger: Double,
    override val totres: Double,
    override val totipi: Double,
    override val totsub: Double,
    override val totdescinc: Double
) : CustomerOrderHeaderProjection
