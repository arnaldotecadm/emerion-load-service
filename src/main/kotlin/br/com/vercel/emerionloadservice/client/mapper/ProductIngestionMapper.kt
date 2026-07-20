package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.client.dto.ProductIngestionDto
import br.com.vercel.emerionloadservice.model.Product

object ProductIngestionMapper {

    // The receiving service generates its own internal id, so the local id
    // is sent as externalId to allow it to be traced back to the source record.
    fun Product.toIngestionDto(): ProductIngestionDto {
        return ProductIngestionDto(
            externalId = this.id,
            nome = this.nome,
            preco = this.preco
        )
    }
}
