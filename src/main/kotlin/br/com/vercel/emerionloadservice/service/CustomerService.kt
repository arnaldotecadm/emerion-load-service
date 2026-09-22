package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.repository.CustomerQueryRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CustomerService(
    private val customerQueryRepository: CustomerQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient,
    private val chunkService: ChunkService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllCustomers(pageable: Pageable): Page<Customer> = customerQueryRepository.findAllPaged(pageable)

    fun processRecordsToWebservice(pageable: Pageable?) {
        val customers = customerQueryRepository.findAllToWebService(pageable ?: Pageable.ofSize(500))
        val batches = chunkService.chunkByJsonSize(customers.content)
        val listOfUpdated =
            batches
                .map { batch ->
                    try {
                        ingestionServiceClient.sendCustomer(batch)
                        batch.map { it.id } to true
                    } catch (e: Exception) {
                        logger.error("Error while sending customer ${batch.map { it.id }}", e)
                        batch.map { it.id } to false
                    }
                }.filter { it.second }
                .flatMap { it.first }
                .toList()
                .joinToString(separator = ", ") { it.toString() }
        if (listOfUpdated.isNotEmpty()) {
            customerQueryRepository.updateSentToWebService(listOfUpdated)
        }
    }

    fun getCustomerByCodCli(codCli: Long): Customer =
        customerQueryRepository.getCustomerByCodCli(codCli)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    fun sendCustomerToIngestion(codCli: Long): Boolean {
        val customer = getCustomerByCodCli(codCli)
        return ingestionServiceClient.sendCustomer(listOf(customer))
    }
}
