package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.repository.CustomerQueryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CustomerService(
    private val customerQueryRepository: CustomerQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient,
) {
    fun getAllCustomers(pageable: Pageable): Page<Customer> = customerQueryRepository.findAllPaged(pageable)

    fun getCustomerByCodCli(codCli: Long): Customer =
        customerQueryRepository.getCustomerByCodCli(codCli)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    fun sendCustomerToIngestion(codCli: Long) {
        val customer = getCustomerByCodCli(codCli)
        ingestionServiceClient.sendCustomer(customer)
    }
}
