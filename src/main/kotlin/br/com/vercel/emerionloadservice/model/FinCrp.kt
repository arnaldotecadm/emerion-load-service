package br.com.vercel.emerionloadservice.model

import java.time.LocalDate

data class FinCrp(
    val numeroParcela: Int?,
    val flagIncobravel: String?,
    val dataIncobravel: LocalDate?,
    val dataVencimento: LocalDate?,
    val prazoEmDias: Int?,
    val valorParcela: Double?,
    val numeroBancario: String?,
    val codigoBanco: String?,
    val nomeBanco: String?,
    val observacoes: String?,
    val flagCartaAnuencia: String?,
    val dataCartaAnuencia: LocalDate?,
    val flagPago: String?,
)
