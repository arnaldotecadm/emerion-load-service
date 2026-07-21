package br.com.vercel.emerionloadservice.model

data class CustomerOrderItem(
    val codGru: String,
    val codSub: String,
    val codPro: String,
    val descricao: String?,
    val quantidade: Double,
    val valorUnitario: Double,
    val valorTotal: Double
) {
    val produto: String
        get() = "$codGru.$codSub.$codPro"
}
