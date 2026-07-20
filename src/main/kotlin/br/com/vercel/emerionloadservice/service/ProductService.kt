package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Product
import br.com.vercel.emerionloadservice.repository.ProductQueryRepository
import br.com.vercel.emerionloadservice.repository.ProductRepository
import br.com.vercel.emerionloadservice.repository.mapper.ProductMapper.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val repository: ProductRepository,
    private val productQueryRepository: ProductQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient
) {

    fun getProducts(): List<Any>{
        return this.repository.getProducts()
    }

    fun getAllProducts(pageable: Pageable): Page<Product> {
        return productQueryRepository.findAllPaged(pageable).toModel()
    }

    fun getProductById(id: String): Product {
        val (codGru, codSub, codPro) = parseId(id)
        return repository.getProductByCodGruCodSubCodPro(codGru, codSub, codPro).toModel()
    }

    fun sendProductToIngestion(id: String) {
        val product = getProductById(id)
        ingestionServiceClient.sendProduct(product)
    }

    private fun parseId(id: String): Triple<String, String, String> {
        val parts = id.split(".")
        require(parts.size == 3) { "Product id must be in the format codGru.codSub.codPro" }
        val codGru = parts[0].padStart(CODGRU_LENGTH, '0')
        val codSub = parts[1].padStart(CODSUB_LENGTH, '0')
        val codPro = parts[2].padStart(CODPRO_LENGTH, '0')
        return Triple(codGru, codSub, codPro)
    }

    companion object {
        private const val CODGRU_LENGTH = 3
        private const val CODSUB_LENGTH = 4
        private const val CODPRO_LENGTH = 5
    }
}