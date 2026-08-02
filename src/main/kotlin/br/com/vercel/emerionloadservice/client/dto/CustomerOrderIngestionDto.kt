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
    val totfrt: Double?,
    val totseg: Double?,
    val totoutdesp: Double?,
    val vendedorExternalId: Long?,
    val atendenteCod: String?,
    val dataEntregaPrevista: LocalDate?,
    val descontoComercial: Double?,
    val descontoRegional: Double?,
    val codigoTransportadora: String?,
    val linhaReserva: String?,
    val pedidoAnterior: String?,
    val regimeTributario: String?,
    val nomeRegimeTributario: String?,
    val itens: List<CustomerOrderItemIngestionDto>
)
