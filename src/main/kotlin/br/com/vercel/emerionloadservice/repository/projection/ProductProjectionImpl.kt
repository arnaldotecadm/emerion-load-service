package br.com.vercel.emerionloadservice.repository.projection

import java.math.BigDecimal

data class ProductProjectionImpl(
    override val codGru: String,
    override val codSub: String,
    override val codPro: String,
    override val nome: String,
    override val descricaoReduzida: String?,
    override val referenciaInterna: String?,
    override val ncm: String?,
    override val cest: String?,
    override val origemProduto: String?,
    override val categoria: String?,
    override val tipo: String?,
    override val marca: String?,
    override val unidade: String?,
    override val pesoLiquido: BigDecimal?,
    override val pesoBruto: BigDecimal?,
    override val descontinuado: Int?,
    override val codigoBarras: String?,
    override val codigoBarrasProprio: String?,
    override val preco: BigDecimal?,
    override val preco2: BigDecimal?,
    override val preco3: BigDecimal?,
    override val preco4: BigDecimal?,
    override val preco5: BigDecimal?
) : ProductProjection
