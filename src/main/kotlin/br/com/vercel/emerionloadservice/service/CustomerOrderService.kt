package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.CustomerOrder
import br.com.vercel.emerionloadservice.repository.CustomerOrderQueryRepository
import br.com.vercel.emerionloadservice.repository.CustomerOrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

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
        return customerOrderQueryRepository.findByKey(numres)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    fun sendOrderToIngestion(numres: String) {
        val order = getOrderByNumres(numres)
        ingestionServiceClient.sendCustomerOrder(order)
    }

}
