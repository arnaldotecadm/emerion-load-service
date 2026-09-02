package br.com.vercel.emerionloadservice.model

import java.time.LocalDateTime

data class Pedlb2(
    val codigoEmpresa: Int,
    val dataPedido: LocalDateTime?,
    val numeroPedido: String,
    val numeroLiberacao: Int,
    val numeroSequenciaLiberacao: Int,
    val classificacaoItem: String?,
    val codigoGrupo: String?,
    val codigoSubGrupo: String?,
    val codigoProduto: String?,
    val descricaoItemLiberacao: String?,
    val quantidadeNoPedido: Double?,
    val totalSeparado: Double?,
    val quantidadeRestante: Double?,
    val totalValorLiquido: Double?,
    val totalValorBruto: Double?,
    val percentualDesconto: Double?,
    val totalCusto: Double?,
    val percentualDeAcrescimo: Double?,
    val precoVendaItem: Double?,
    val precoPraticado: Double?,
    val custoPraticado: Double?,
)
