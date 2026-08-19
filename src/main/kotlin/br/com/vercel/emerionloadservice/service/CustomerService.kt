package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.model.SendAllResult
import br.com.vercel.emerionloadservice.repository.CustomerQueryRepository
import br.com.vercel.emerionloadservice.repository.CustomerRepository
import br.com.vercel.emerionloadservice.repository.mapper.CustomerMapper.toModel
import br.com.vercel.emerionloadservice.repository.mapper.VendedorMapper.toModel
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CustomerService(
    private val customerRepository: CustomerRepository,
    private val customerQueryRepository: CustomerQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient
) {
    fun getAllCustomers(pageable: Pageable): Page<Customer> {
        return customerQueryRepository.findAllPaged(pageable).toModel()
    }

    fun getCustomerByCodCli(codCli: Long): Customer {
        return customerRepository.getCustomerByCodCli(codCli)?.toModel()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    fun sendCustomerToIngestion(codCli: Long) {
        val customer = getCustomerByCodCli(codCli)
        ingestionServiceClient.sendCustomer(customer)
    }

    fun sendAllCustomersToIngestion(pageable: Pageable): SendAllResult {
        var page = pageable
        var totalSent = 0
        var totalErrors = 0
        var totalPages = 0

        do {
            val currentPage = getAllCustomers(page)
            totalPages = currentPage.totalPages

            currentPage.content.forEach { customer ->
                try {
                    ingestionServiceClient.sendCustomer(customer)
                    totalSent++
                } catch (e: Exception) {
                    logger.error("Failed to send customer {}: {}", customer.id, e.message)
                    totalErrors++
                }
            }

            page = page.next()
        } while (currentPage.hasNext())

        return SendAllResult(totalSent = totalSent, totalErrors = totalErrors, totalPages = totalPages)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CustomerService::class.java)
    }
}