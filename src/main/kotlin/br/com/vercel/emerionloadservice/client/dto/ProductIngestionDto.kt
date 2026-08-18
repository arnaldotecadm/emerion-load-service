package br.com.vercel.emerionloadservice.client.dto

import java.math.BigDecimal

data class ProductIngestionDto(
    val externalId: String,
    val cnpjEmpresa: String,
    val nome: String,
    val descricaoReduzida: String?,
    val referenciaInterna: String?,
    val ncm: String?,
    val cest: String?,
    val origemProduto: String?,
    val categoria: String?,
    val tipo: String?,
    val marca: String?,
    val unidade: String?,
    val pesoLiquido: BigDecimal?,
    val pesoBruto: BigDecimal?,
    val descontinuado: Boolean?,
    val codigoBarras: String?,
    val codigoBarrasProprio: String?,
    val preco: BigDecimal?,
    val preco2: BigDecimal?,
    val preco3: BigDecimal?,
    val preco4: BigDecimal?,
    val preco5: BigDecimal?,
    val descontoPadrao: BigDecimal?,
    val estoqueDisponivel: BigDecimal?,
    val estoqueMinimo: BigDecimal?,
    val estoqueReservado: BigDecimal?,
    val estoqueAdquirido: BigDecimal?
)
