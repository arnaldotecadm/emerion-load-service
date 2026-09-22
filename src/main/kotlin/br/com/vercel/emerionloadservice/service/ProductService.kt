package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Product
import br.com.vercel.emerionloadservice.model.ProductKey
import br.com.vercel.emerionloadservice.repository.ProductQueryRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ProductService(
    private val productQueryRepository: ProductQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient,
    private val chunkService: ChunkService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllProducts(pageable: Pageable): Page<Product> = productQueryRepository.findAllPaged(pageable)

    fun getProductById(id: String): Product {
        val (codGru, codSub, codPro) = parseId(id)
        return productQueryRepository.getProductByCodGruCodSubCodPro(codGru, codSub, codPro)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    fun sendProductToIngestion(id: String) {
        val product = getProductById(id)
        ingestionServiceClient.sendProduct(listOf(product))
    }

    fun processRecordsToWebservice(pageable: Pageable?) {
        val products = productQueryRepository.findAllToWebService(pageable ?: Pageable.ofSize(500))
        val batches = chunkService.chunkByJsonSize(products.content)

        val listOfUpdated =
            batches
                .map {
                    try {
                        ingestionServiceClient.sendProduct(it)
                        it.map { prod -> ProductKey(prod.codGru, prod.codSub, prod.codPro) } to true
                    } catch (e: Exception) {
                        logger.error(
                            "Error while sending product ${
                                it.map { prod ->
                                    ProductKey(
                                        prod.codGru,
                                        prod.codSub,
                                        prod.codPro,
                                    )
                                }
                            }",
                            e,
                        )
                        it.map { prod -> ProductKey(prod.codGru, prod.codSub, prod.codPro) } to false
                    }
                }.filter { it.second }
                .flatMap { it.first }
                .toList()

        if (listOfUpdated.isNotEmpty()) {
            productQueryRepository.updateSentToWebService(listOfUpdated)
        }
    }

    private fun parseId(id: String): Triple<String, String, String> {
        val parts = id.split(".")
        if (parts.size != 3) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Product id must be in the format codGru.codSub.codPro",
            )
        }
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
