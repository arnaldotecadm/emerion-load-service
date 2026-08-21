package br.com.vercel.emerionloadservice.client.dto

import java.time.LocalDate

data class FinCrpIngestionDto(
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
    val flagPago: String?
)
