package br.com.vercel.emerionloadservice.repository.projection

interface CustomerOrderItemProjection {
    val numres: String
    val codGru: String
    val codSub: String
    val codPro: String
    val descricao: String?
    val quantidade: Double
    val valorUnitario: Double
    val valorTotal: Double
}
