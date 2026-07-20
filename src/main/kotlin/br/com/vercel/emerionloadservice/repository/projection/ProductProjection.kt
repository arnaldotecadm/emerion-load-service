package br.com.vercel.emerionloadservice.repository.projection

import java.math.BigDecimal

interface ProductProjection {
    val codGru: String
    val codSub: String
    val codPro: String
    val nome: String
    val preco: BigDecimal?
}
