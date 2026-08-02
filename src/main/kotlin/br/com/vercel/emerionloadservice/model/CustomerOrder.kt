package br.com.vercel.emerionloadservice.model

import java.time.LocalDate

data class CustomerOrder(
    val codCli: Long,
    val cpfCnpj: String?,
    val numres: String,
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
    val itens: List<CustomerOrderItem>
)
