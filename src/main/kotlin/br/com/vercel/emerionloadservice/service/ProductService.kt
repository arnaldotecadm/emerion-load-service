package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Product
import br.com.vercel.emerionloadservice.model.SendAllResult
import br.com.vercel.emerionloadservice.repository.ProductQueryRepository
import br.com.vercel.emerionloadservice.repository.mapper.ProductMapper.toModel
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ProductService(
    private val productQueryRepository: ProductQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient
) {

    fun getAllProducts(pageable: Pageable): Page<Product> {
        return productQueryRepository.findAllPaged(pageable).toModel()
    }

    fun getProductById(id: String): Product {
        val (codGru, codSub, codPro) = parseId(id)
        return productQueryRepository.getProductByCodGruCodSubCodPro(codGru, codSub, codPro)?.toModel()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    fun sendProductToIngestion(id: String) {
        val product = getProductById(id)
        ingestionServiceClient.sendProduct(product)
    }

    fun sendAllProductsToIngestion(pageable: Pageable): SendAllResult {
        var page = pageable
        var totalSent = 0
        var totalErrors = 0
        var totalPages = 0

        do {
            val currentPage = getAllProducts(page)
            totalPages = currentPage.totalPages

            currentPage.content.forEach { product ->
                try {
                    ingestionServiceClient.sendProduct(product)
                    totalSent++
                } catch (e: Exception) {
                    logger.error("Failed to send product {}: {}", product.id, e.message)
                    totalErrors++
                }
            }

            page = page.next()
        } while (currentPage.hasNext())

        return SendAllResult(totalSent = totalSent, totalErrors = totalErrors, totalPages = totalPages)
    }

    private fun parseId(id: String): Triple<String, String, String> {
        val parts = id.split(".")
        if (parts.size != 3) throw ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Product id must be in the format codGru.codSub.codPro"
        )
        val codGru = parts[0].padStart(CODGRU_LENGTH, '0')
        val codSub = parts[1].padStart(CODSUB_LENGTH, '0')
        val codPro = parts[2].padStart(CODPRO_LENGTH, '0')
        return Triple(codGru, codSub, codPro)
    }

    companion object {
        private const val CODGRU_LENGTH = 3
        private const val CODSUB_LENGTH = 4
        private const val CODPRO_LENGTH = 5
        private val logger = LoggerFactory.getLogger(ProductService::class.java)
    }
}