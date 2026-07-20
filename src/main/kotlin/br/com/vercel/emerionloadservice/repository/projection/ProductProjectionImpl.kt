package br.com.vercel.emerionloadservice.repository.projection

import java.math.BigDecimal

data class ProductProjectionImpl(
    override val codGru: String,
    override val codSub: String,
    override val codPro: String,
    override val nome: String,
    override val preco: BigDecimal?
) : ProductProjection
