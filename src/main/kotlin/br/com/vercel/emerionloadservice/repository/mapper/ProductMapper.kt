package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.Product
import br.com.vercel.emerionloadservice.repository.projection.ProductProjection
import org.springframework.data.domain.Page

object ProductMapper {

    fun Page<ProductProjection>.toModel(): Page<Product> {
        return this.map { it.toModel() }
    }

    fun ProductProjection.toModel(): Product {
        return Product(
            codGru = this.codGru.trim(),
            codSub = this.codSub.trim(),
            codPro = this.codPro.trim(),
            nome = this.nome.trim(),
            descricaoReduzida = this.descricaoReduzida?.trim(),
            referenciaInterna = this.referenciaInterna?.trim(),
            ncm = this.ncm?.trim(),
            cest = this.cest?.trim(),
            origemProduto = this.origemProduto?.trim(),
            categoria = this.categoria?.trim(),
            tipo = this.tipo?.trim(),
            marca = this.marca?.trim(),
            unidade = this.unidade?.trim(),
            pesoLiquido = this.pesoLiquido,
            pesoBruto = this.pesoBruto,
            descontinuado = this.descontinuado == 1,
            codigoBarras = this.codigoBarras?.trim(),
            codigoBarrasProprio = this.codigoBarrasProprio?.trim(),
            preco = this.preco,
            preco2 = this.preco2,
            preco3 = this.preco3,
            preco4 = this.preco4,
            preco5 = this.preco5,
            descontoPadrao = this.descontoPadrao,
            estoqueDisponivel = this.estoqueDisponivel,
            estoqueMinimo = this.estoqueMinimo,
            estoqueReservado = this.estoqueReservado,
            estoqueAdquirido = this.estoqueAdquirido,
        )
    }
}
