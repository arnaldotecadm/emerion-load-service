package br.com.vercel.emerionloadservice.model

import java.time.Instant

data class CustomerOrder(
    val codCli: Long,
    val numres: String,
    val nronfe: String?,
    val dteres: Instant,
    val sitres: String?,
    val totger: Double,
    val totres: Double,
    val totipi: Double,
    val totsub: Double,
    val totdescinc: Double,
    val itens: List<CustomerOrderItem>
)
