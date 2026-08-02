package br.com.vercel.emerionloadservice.repository.projection

import java.math.BigDecimal

interface ProductProjection {
    val codGru: String
    val codSub: String
    val codPro: String
    val nome: String
    val descricaoReduzida: String?
    val referenciaInterna: String?
    val ncm: String?
    val cest: String?
    val origemProduto: String?
    val categoria: String?
    val tipo: String?
    val marca: String?
    val unidade: String?
    val pesoLiquido: BigDecimal?
    val pesoBruto: BigDecimal?
    val descontinuado: Int?
    val codigoBarras: String?
    val codigoBarrasProprio: String?
    val preco: BigDecimal?
    val preco2: BigDecimal?
    val preco3: BigDecimal?
    val preco4: BigDecimal?
    val preco5: BigDecimal?
}
