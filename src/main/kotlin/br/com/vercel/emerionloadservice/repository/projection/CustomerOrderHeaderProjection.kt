package br.com.vercel.emerionloadservice.repository.projection

import java.time.Instant

interface CustomerOrderHeaderProjection {
    val codCli: Long
    val numres: String
    val nronfe: String?
    val dteres: Instant
    val sitres: String?
    val totger: Double
    val totres: Double
    val totipi: Double
    val totsub: Double
    val totdescinc: Double
}
