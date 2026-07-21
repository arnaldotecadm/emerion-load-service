package br.com.vercel.emerionloadservice.client.dto

import java.time.Instant

data class CustomerOrderIngestionDto(
    val externalId: String,
    val codCli: Long,
    val cnpjEmpresa: String?,
    val nronfe: String?,
    val dteres: Instant,
    val sitres: String?,
    val totger: Double,
    val totres: Double,
    val totipi: Double,
    val totsub: Double,
    val totdescinc: Double,
    val itens: List<CustomerOrderItemIngestionDto>
)
