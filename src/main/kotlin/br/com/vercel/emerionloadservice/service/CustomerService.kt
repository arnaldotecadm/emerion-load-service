package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.repository.CustomerRepository
import br.com.vercel.emerionloadservice.repository.mapper.CustomerMapper.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CustomerService(private val customerRepository: CustomerRepository) {
    fun getAllCustomers(pageable: Pageable): Page<Customer> {
        return customerRepository.getAllCustomers(pageable = pageable).toModel()
    }

    fun getCustomerByCodCli(codCli: Long): Customer {
        return customerRepository.getCustomerByCodCli(codCli).toModel()
    }
}