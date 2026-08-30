package br.com.vercel.emerionloadservice.client.dto

import java.time.LocalDate

data class PedlibIngestionDto(
    val cnpjEmpresa: String,
    val codigoEmpresa: Int,
    val dataPedido: LocalDate?,
    val numeroPedido: String,
    val numeroLiberacao: Int,
    val dataLiberacao: LocalDate?,
    val horaLiberacao: String?,
    val codigoCliente: Long?,
    val quantidadeSeparada: Int?,
    val totalLiberadoSemImpostos: Double?,
    val totalLiberadoComImpostos: Double?,
    val situacaoLiberacao: String?,
    val codigoVendedor: Long?,
    val comissaoLiberacao: Double?,
    val totalCusto: Double?,
    val detalhes: List<Pedlb2IngestionDto>,
)
