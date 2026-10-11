package br.com.vercel.emerionloadservice.model

import java.time.LocalDate

data class CustomerQuotation(
    val codigoEmpresa: Int,
    val codigoCliente: Long,
    val cpfCnpj: String?,
    val numeroPedido: String,
    val dataPedido: LocalDate,
    val statusPedido: String?,
    val totalPedidoComImpostos: Double,
    val totalPedidoSemImpostos: Double,
    val totalIpi: Double?,
    val totalIcms: Double?,
    val totalPis: Double?,
    val totalCofins: Double?,
    val totalSubstituicaoTributaria: Double?,
    val totalDescontoIncondicional: Double?,
    val totalFrete: Double?,
    val totalSeguro: Double?,
    val totalOutrasDespesas: Double?,
    val vendedorExternalId: Long?,
    val atendenteCod: Long?,
    val regimeTributario: String?,
    val nomeRegimeTributario: String?,
    val codigoPadraoFaturamento: String?,
)
