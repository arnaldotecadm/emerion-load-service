package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.CustomerOrder
import br.com.vercel.emerionloadservice.model.SendAllResult
import br.com.vercel.emerionloadservice.repository.CustomerOrderQueryRepository
import br.com.vercel.emerionloadservice.repository.CustomerOrderRepository
import br.com.vercel.emerionloadservice.repository.mapper.CustomerOrderMapper.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class CustomerOrderService(
    private val customerOrderRepository: CustomerOrderRepository,
    private val customerOrderQueryRepository: CustomerOrderQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient
) {
    fun getAllOrders(pageable: Pageable): Page<CustomerOrder> {
        return customerOrderQueryRepository.findAllPaged(pageable)
    }

    fun getOrderByNumres(numres: String): CustomerOrder {
        val header =
            customerOrderRepository.getHeaderByNumres(numres) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        val items = customerOrderRepository.getItemsByNumres(numres)
        return header.toModel(items)
    }

    fun sendOrderToIngestion(numres: String) {
        val order = getOrderByNumres(numres)
        ingestionServiceClient.sendCustomerOrder(order)
    }

    fun getOrderByBusinessKey(codEmp: Int, dteres: LocalDate, numres: String): CustomerOrder {
        val header = customerOrderRepository.getHeaderByBusinessKey(codEmp, dteres, numres)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        val items = customerOrderRepository.getItemsByBusinessKey(codEmp, dteres, numres)
        return header.toModel(items)
    }

    fun sendOrderToIngestion(codEmp: Int, dteres: LocalDate, numres: String) {
        val order = getOrderByBusinessKey(codEmp, dteres, numres)
        ingestionServiceClient.sendCustomerOrder(order)
    }

    fun sendAllOrdersToIngestion(pageable: Pageable): SendAllResult {
        var page = pageable
        var totalSent = 0
        var totalErrors = 0
        var totalPages = 0

        do {
            val currentPage = getAllOrders(page)
            totalPages = currentPage.totalPages

            currentPage.content.forEach { order ->
                try {
                    ingestionServiceClient.sendCustomerOrder(order)
                    totalSent++
                } catch (e: Exception) {
                    logger.error("Failed to send order {}-{}-{}: {}", order.codEmp, order.dteres, order.numres, e.message)
                    totalErrors++
                }
            }

            page = page.next()
        } while (currentPage.hasNext())

        return SendAllResult(totalSent = totalSent, totalErrors = totalErrors, totalPages = totalPages)
    }

    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(CustomerOrderService::class.java)
    }
}
