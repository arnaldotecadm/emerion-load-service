package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.CustomerOrder
import br.com.vercel.emerionloadservice.repository.CustomerOrderQueryRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CustomerOrderService(
    private val customerOrderQueryRepository: CustomerOrderQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient,
    private val chunkService: ChunkService,
) {
    val ordersWithProblem = mutableListOf<String>()
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllOrders(pageable: Pageable): Page<CustomerOrder> = customerOrderQueryRepository.findAllPaged(pageable)

    fun processRecordsToWebservice(pageable: Pageable?) {
        val customers = customerOrderQueryRepository.findAllToWebService(pageable ?: Pageable.ofSize(500))

        val fromDatabase = customers.content
        logger.info("Orders with problem: $ordersWithProblem")
        logger.info("Retrieved from database ${fromDatabase.size} customer orders to send to ingestion service")
        val fromDatabaseFilteredOut = fromDatabase.filter { it.numeroPedido !in ordersWithProblem }
        logger.info("Filtered out ${fromDatabase.size - fromDatabaseFilteredOut.size} customer orders that had problems in previous attempts")
        val batches = chunkService.chunkByJsonSizePerNumber(fromDatabaseFilteredOut, 25)
        val listOfUpdated =
            batches
                .asSequence()
                .map {
                    try {
                        ingestionServiceClient.sendCustomerOrder(it)
                        it.map { customer -> customer.numeroPedido } to true
                    } catch (e: Exception) {
                        logger.error(
                            "Error while sending customer order ${it.map { customer -> customer.numeroPedido }}: {}",
                            e.message
                        )
                        it.map { customer -> customer.numeroPedido } to false
                    }
                }.filter { it.second }
                .flatMap { it.first }
                .toList()
                .joinToString(separator = ", ") { it }
        if (listOfUpdated.isNotEmpty()) {
            try {
                customerOrderQueryRepository.updateSentToWebService(listOfUpdated)
            } catch (e: Exception) {
                logger.error(
                    "Error while updating sent_to_web_service for customer orders $listOfUpdated: {}",
                    e.message
                )
                logger.info("Updating item by item so we can isolate the problem")
                listOfUpdated.split(", ").forEach { numres ->
                    try {
                        customerOrderQueryRepository.updateSentToWebService(numres)
                    } catch (e: Exception) {
                        logger.error(
                            "Error while updating sent_to_web_service for customer order $numres: {}",
                            e.message
                        )
                        ordersWithProblem.add(numres)
                    }
                }
            }
        }
    }

    fun getOrderByNumres(numres: String): CustomerOrder =
        customerOrderQueryRepository.findByKey(numres)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    fun sendOrderToIngestion(numres: String) {
        val order = getOrderByNumres(numres)
        ingestionServiceClient.sendCustomerOrder(listOf(order))
    }
}
