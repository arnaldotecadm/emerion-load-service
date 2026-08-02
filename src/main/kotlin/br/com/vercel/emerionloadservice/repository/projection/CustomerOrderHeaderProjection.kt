package br.com.vercel.emerionloadservice.repository.projection

import java.time.LocalDateTime

interface CustomerOrderHeaderProjection {
    val codCli: Long
    val cpfCnpj: String?
    val numres: String
    val nronfe: String?
    val dteres: LocalDateTime
    val sitres: String?
    val totger: Double
    val totres: Double
    val totipi: Double
    val totsub: Double
    val totdescinc: Double
    val totfrt: Double?
    val totseg: Double?
    val totoutdesp: Double?
    val vendedorExternalId: Long?
    val atendenteCod: String?
    val dataEntregaPrevista: LocalDateTime?
    val descontoComercial: Double?
    val descontoRegional: Double?
    val codigoTransportadora: String?
    val linhaReserva: String?
    val pedidoAnterior: String?
    val regimeTributario: String?
    val nomeRegimeTributario: String?
}
