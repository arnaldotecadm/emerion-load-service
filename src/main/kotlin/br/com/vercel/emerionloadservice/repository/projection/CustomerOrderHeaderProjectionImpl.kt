package br.com.vercel.emerionloadservice.repository.projection

import java.time.LocalDate
import java.time.LocalDateTime

data class CustomerOrderHeaderProjectionImpl(
    override val codCli: Long,
    override val cpfCnpj: String?,
    override val numres: String,
    override val nronfe: String?,
    override val dataFaturamento: LocalDate?,
    override val totalFaturado: Double?,
    override val dteres: LocalDateTime,
    override val sitres: String?,
    override val totger: Double,
    override val totres: Double,
    override val totipi: Double,
    override val totsub: Double,
    override val totdescinc: Double,
    override val totfrt: Double,
    override val totseg: Double,
    override val totoutdesp: Double,
    override val vendedorExternalId: Long?,
    override val atendenteCod: String?,
    override val dataEntregaPrevista: LocalDateTime?,
    override val descontoComercial: Double?,
    override val descontoRegional: Double?,
    override val codigoTransportadora: String?,
    override val linhaReserva: String?,
    override val pedidoAnterior: String?,
    override val regimeTributario: String?,
    override val nomeRegimeTributario: String?,
    override val dataProcessamentoComercial: LocalDate?,
    override val dataProcessamentoFinanceiro: LocalDate?,
    override val dataRejeicao: LocalDate?,
    override val observacaoRejeicao: String?,
    override val dataEntrega: LocalDate?,
    override val dataFinalizacao: LocalDate?,
    override val codigoPagamento: String?,
    override val descricaoPagamento: String?
) : CustomerOrderHeaderProjection
