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
            preco = this.preco,
        )
    }
}
