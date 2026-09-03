package br.com.vercel.emerionloadservice.model

import java.time.LocalDate

data class CustomerCredit(
    val codCli: Long,
    val sequencia: String?,
    val data: LocalDate,
    val dataPedido: LocalDate?,
    val valorUtilizado: Double,
    val valorTotal: Double,
    val saldo: Double,
    val situacao: String?,
) {
    // Mirrors the legacy rule: a credit entry with usage is an outgoing (SAIDA) movement.
    val tipo: String
        get() = if (valorUtilizado > 0) "SAIDA" else "ENTRADA"
}
