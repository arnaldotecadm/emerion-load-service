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
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.server.ResponseStatusException

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

    private fun <T> sendToIngestion(entityName: String, externalId: Any?, action: () -> T): T {
        return try {
            action()
        } catch (e: RestClientResponseException) {
            logger.error("Failed to send {} {} to ingestion service", entityName, externalId, e)
            throw ResponseStatusException(
                e.statusCode,
                e.responseBodyAsString?.takeIf(String::isNotBlank)
                    ?: "Failed to send $entityName $externalId to ingestion service",
                e
            )
        } catch (e: RestClientException) {
            logger.error("Failed to send {} {} to ingestion service", entityName, externalId, e)
            throw ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to send $entityName $externalId to ingestion service", e)
        }
    }

    fun sendCustomer(customer: Customer) {
        val url = "$baseUrl$customerEndpoint"
        val dto = customer.toIngestionDto(companyProvider.getCompanyCnpj())

        logger.info("Sending customer {} to ingestion service at {}", dto.externalId, url)
        sendToIngestion("customer", dto.externalId) {
            restClient.post()
                .uri(url)
                .body(dto)
                .retrieve()
                .toBodilessEntity()
        }
        logger.info("Customer {} sent successfully to ingestion service", dto.externalId)
    }

    fun sendProduct(product: Product) {
        val url = "$baseUrl$productEndpoint"
        val dto = product.toIngestionDto(companyProvider.getCompanyCnpj())

        logger.info("Sending product {} to ingestion service at {}", dto.externalId, url)
        sendToIngestion("product", dto.externalId) {
            restClient.post()
                .uri(url)
                .body(dto)
                .retrieve()
                .toBodilessEntity()
        }
        logger.info("Product {} sent successfully to ingestion service", dto.externalId)
    }

    fun sendCustomerAddress(address: CustomerAddress) {
        val url = "$baseUrl$customerAddressEndpoint"
        val dto = address.toIngestionDto(companyProvider.getCompanyCnpj())

        logger.info("Sending {} address(es) of customer {} to ingestion service at {}", dto.enderecos.size, dto.externalId, url)
        sendToIngestion("customer address", dto.externalId) {
            restClient.post()
                .uri(url)
                .body(dto)
                .retrieve()
                .toBodilessEntity()
        }
        logger.info("Address(es) of customer {} sent successfully to ingestion service", dto.externalId)
    }

    fun sendCustomerCredits(credits: List<CustomerCredit>) {
        if (credits.isEmpty()) return

        val url = "$baseUrl$customerCreditEndpoint"
        val dtos = credits.toIngestionDto(companyProvider.getCompanyCnpj())
        val customerExternalId = dtos.first().customerExternalId

        logger.info("Sending {} credit(s) of customer {} to ingestion service at {}", dtos.size, customerExternalId, url)
        sendToIngestion("customer credit", customerExternalId) {
            restClient.post()
                .uri(url)
                .body(dtos)
                .retrieve()
                .toBodilessEntity()
        }
        logger.info("Credit(s) of customer {} sent successfully to ingestion service", customerExternalId)
    }

    fun sendCustomerOrder(order: CustomerOrder) {
        val url = "$baseUrl$customerOrderEndpoint"
        val dto = order.toIngestionDto(companyProvider.getCompanyCnpj())

        logger.info("Sending order {} ({} item(s)) to ingestion service at {}", dto.externalId, dto.itens.size, url)
        sendToIngestion("customer order", dto.externalId) {
            restClient.post()
                .uri(url)
                .body(dto)
                .retrieve()
                .toBodilessEntity()
        }
        logger.info("Order {} sent successfully to ingestion service", dto.externalId)
    }

    fun sendVendedor(vendedor: Vendedor) {
        val url = "$baseUrl$vendedorEndpoint"
        val dto = vendedor.toIngestionDto(companyProvider.getCompanyCnpj())

        logger.info("Sending vendedor {} to ingestion service at {}", dto.externalId, url)
        sendToIngestion("vendedor", dto.externalId) {
            restClient.post()
                .uri(url)
                .body(dto)
                .retrieve()
                .toBodilessEntity()
        }
        logger.info("Vendedor {} sent successfully to ingestion service", dto.externalId)
    }
}
