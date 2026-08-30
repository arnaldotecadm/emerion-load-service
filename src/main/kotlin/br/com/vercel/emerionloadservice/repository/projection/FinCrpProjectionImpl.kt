package br.com.vercel.emerionloadservice.repository.projection

import java.time.LocalDateTime

data class FinCrpProjectionImpl(
    val codigoEmpresa: Int,
    val dataEmissao: LocalDateTime?,
    val documento: String,
    val numeroParcela: Int?,
    val flagIncobravel: String?,
    val dataIncobravel: LocalDateTime?,
    val dataVencimento: LocalDateTime?,
    val prazoEmDias: Int?,
    val valorParcela: Double?,
    val numeroBancario: String?,
    val codigoBanco: String?,
    val nomeBanco: String?,
    val observacoes: String?,
    val flagCartaAnuencia: String?,
    val dataCartaAnuencia: LocalDateTime?,
    val flagPago: String?,
)
