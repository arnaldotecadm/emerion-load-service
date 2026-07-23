package br.com.vercel.emerionloadservice.client.dto

import java.time.LocalDate

data class CustomerOrderIngestionDto(
    val externalId: String,
    val customerExternalId: Long,
    val cnpjEmpresa: String,
    val cpfCnpj: String?,
    val nronfe: String?,
    val dteres: LocalDate,
    val sitres: String?,
    val totger: Double,
    val totres: Double,
    val totipi: Double,
    val totsub: Double,
    val totdescinc: Double,
    val itens: List<CustomerOrderItemIngestionDto>
)
