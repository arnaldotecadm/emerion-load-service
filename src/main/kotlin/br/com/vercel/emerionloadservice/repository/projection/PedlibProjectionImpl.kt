package br.com.vercel.emerionloadservice.repository.projection

import java.time.LocalDateTime

data class PedlibProjectionImpl(
    val codigoEmpresa: Int,
    val dataPedido: LocalDateTime?,
    val numeroPedido: String,
    val numeroLiberacao: Int,
    val dataLiberacao: LocalDateTime?,
    val horaLiberacao: String?,
    val codigoCliente: Long?,
    val quantidadeSeparada: Int?,
    val totalLiberadoSemImpostos: Double?,
    val totalLiberadoComImpostos: Double?,
    val situacaoLiberacao: String?,
    val codigoVendedor: Long?,
    val comissaoLiberacao: Double?,
    val totalCusto: Double?,
)
