package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.service.CustomerService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("customer")
class CustomerController(private val customerService: CustomerService) {

    @GetMapping("all")
    fun getAllCustomers(@PageableDefault(size = 40) pageable: Pageable): Page<Customer> {
        return this.customerService.getAllCustomers(pageable)
    }

    @GetMapping("{codCli}")
    fun getCustomerByCodCli(@PathVariable codCli: Long): Customer {
        return this.customerService.getCustomerByCodCli(codCli)
    }

    @PostMapping("{codCli}/send")
    fun sendCustomerToIngestion(@PathVariable codCli: Long): ResponseEntity<Void> {
        this.customerService.sendCustomerToIngestion(codCli)
        return ResponseEntity.ok().build()
    }
}