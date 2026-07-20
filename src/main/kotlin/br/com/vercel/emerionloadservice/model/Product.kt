package br.com.vercel.emerionloadservice.model

import java.math.BigDecimal

data class Product(
    val codGru: String,
    val codSub: String,
    val codPro: String,
    val nome: String,
    val preco: BigDecimal?
) {
    val id: String
        get() = "$codGru.$codSub.$codPro"
}
