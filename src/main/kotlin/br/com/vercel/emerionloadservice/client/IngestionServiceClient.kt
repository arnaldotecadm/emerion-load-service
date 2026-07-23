package br.com.vercel.emerionloadservice.client

import br.com.vercel.emerionloadservice.client.mapper.CustomerAddressIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.CustomerCreditIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.CustomerIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.CustomerOrderIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.ProductIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.VendedorIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.model.CustomerAddress
import br.com.vercel.emerionloadservice.model.CustomerCredit
import br.com.vercel.emerionloadservice.model.CustomerOrder
import br.com.vercel.emerionloadservice.model.Product
import br.com.vercel.emerionloadservice.model.Vendedor
import br.com.vercel.emerionloadservice.service.CompanyProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
class IngestionServiceClient(
    private val restClient: RestClient,
    private val companyProvider: CompanyProvider,
    @Value("\${ingestion-service.base-url}") private val baseUrl: String,
    @Value("\${ingestion-service.endpoints.customer}") private val customerEndpoint: String,
    @Value("\${ingestion-service.endpoints.product}") private val productEndpoint: String,
    @Value("\${ingestion-service.endpoints.customer-address}") private val customerAddressEndpoint: String,
    @Value("\${ingestion-service.endpoints.customer-credit}") private val customerCreditEndpoint: String,
    @Value("\${ingestion-service.endpoints.customer-order}") private val customerOrderEndpoint: String,
    @Value("\${ingestion-service.endpoints.vendedor}") private val vendedorEndpoint: String
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun sendCustomer(customer: Customer) {
        val url = "$baseUrl$customerEndpoint"
        val dto = customer.toIngestionDto(companyProvider.getCompanyCnpj())

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
        val dto = product.toIngestionDto(companyProvider.getCompanyCnpj())

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

    fun sendCustomerAddress(address: CustomerAddress) {
        val url = "$baseUrl$customerAddressEndpoint"
        val dto = address.toIngestionDto(companyProvider.getCompanyCnpj())

        logger.info("Sending {} address(es) of customer {} to ingestion service at {}", dto.enderecos.size, dto.externalId, url)
        try {
            restClient.post()
                .uri(url)
                .body(dto)
                .retrieve()
                .toBodilessEntity()
            logger.info("Address(es) of customer {} sent successfully to ingestion service", dto.externalId)
        } catch (e: RestClientException) {
            logger.error("Failed to send address(es) of customer {} to ingestion service", dto.externalId, e)
            throw e
        }
    }

    fun sendCustomerCredits(credits: List<CustomerCredit>) {
        if (credits.isEmpty()) return

        val url = "$baseUrl$customerCreditEndpoint"
        val dtos = credits.toIngestionDto(companyProvider.getCompanyCnpj())
        val customerExternalId = dtos.first().customerExternalId

        logger.info("Sending {} credit(s) of customer {} to ingestion service at {}", dtos.size, customerExternalId, url)
        try {
            restClient.post()
                .uri(url)
                .body(dtos)
                .retrieve()
                .toBodilessEntity()
            logger.info("Credit(s) of customer {} sent successfully to ingestion service", customerExternalId)
        } catch (e: RestClientException) {
            logger.error("Failed to send credit(s) of customer {} to ingestion service", customerExternalId, e)
            throw e
        }
    }

    fun sendCustomerOrder(order: CustomerOrder) {
        val url = "$baseUrl$customerOrderEndpoint"
        val dto = order.toIngestionDto(companyProvider.getCompanyCnpj())

        logger.info("Sending order {} ({} item(s)) to ingestion service at {}", dto.externalId, dto.itens.size, url)
        try {
            restClient.post()
                .uri(url)
                .body(dto)
                .retrieve()
                .toBodilessEntity()
            logger.info("Order {} sent successfully to ingestion service", dto.externalId)
        } catch (e: RestClientException) {
            logger.error("Failed to send order {} to ingestion service", dto.externalId, e)
            throw e
        }
    }

    fun sendVendedor(vendedor: Vendedor) {
        val url = "$baseUrl$vendedorEndpoint"
        val dto = vendedor.toIngestionDto(companyProvider.getCompanyCnpj())

        logger.info("Sending vendedor {} to ingestion service at {}", dto.externalId, url)
        try {
            restClient.post()
                .uri(url)
                .body(dto)
                .retrieve()
                .toBodilessEntity()
            logger.info("Vendedor {} sent successfully to ingestion service", dto.externalId)
        } catch (e: RestClientException) {
            logger.error("Failed to send vendedor {} to ingestion service", dto.externalId, e)
            throw e
        }
    }
}
