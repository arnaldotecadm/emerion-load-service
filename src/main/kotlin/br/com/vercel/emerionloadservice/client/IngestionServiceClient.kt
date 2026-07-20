package br.com.vercel.emerionloadservice.client

import br.com.vercel.emerionloadservice.client.mapper.CustomerIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.ProductIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.model.Product
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
class IngestionServiceClient(
    private val restClient: RestClient,
    @Value("\${ingestion-service.base-url}") private val baseUrl: String,
    @Value("\${ingestion-service.endpoints.customer}") private val customerEndpoint: String,
    @Value("\${ingestion-service.endpoints.product}") private val productEndpoint: String
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun sendCustomer(customer: Customer) {
        val url = "$baseUrl$customerEndpoint"
        val dto = customer.toIngestionDto()

        logger.info("Sending customer {} to ingestion service at {}", dto.externalId, url)
        try {
            restClient.post()
                .uri(url)
                .body(dto)
                .retrieve()
                .toBodilessEntity()
            logger.info("Customer {} sent successfully to ingestion service", dto.externalId)
        } catch (e: RestClientException) {
            logger.error("Failed to send customer {} to ingestion service", dto.externalId, e)
            throw e
        }
    }

    fun sendProduct(product: Product) {
        val url = "$baseUrl$productEndpoint"
        val dto = product.toIngestionDto()

        logger.info("Sending product {} to ingestion service at {}", dto.externalId, url)
        try {
            restClient.post()
                .uri(url)
                .body(dto)
                .retrieve()
                .toBodilessEntity()
            logger.info("Product {} sent successfully to ingestion service", dto.externalId)
        } catch (e: RestClientException) {
            logger.error("Failed to send product {} to ingestion service", dto.externalId, e)
            throw e
        }
    }
}
