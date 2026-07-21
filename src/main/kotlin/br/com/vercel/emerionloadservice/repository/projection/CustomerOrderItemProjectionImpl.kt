package br.com.vercel.emerionloadservice.repository.projection

data class CustomerOrderItemProjectionImpl(
    override val numres: String,
    override val codGru: String,
    override val codSub: String,
    override val codPro: String,
    override val descricao: String?,
    override val quantidade: Double,
    override val valorUnitario: Double,
    override val valorTotal: Double
) : CustomerOrderItemProjection
