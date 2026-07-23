package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.client.dto.CustomerIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.CustomerIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.service.CompanyProvider
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
class CustomerController(
    private val customerService: CustomerService,
    private val companyProvider: CompanyProvider
) {

    @GetMapping("all")
    fun getAllCustomers(@PageableDefault(size = 40) pageable: Pageable): Page<CustomerIngestionDto> {
        return this.customerService.getAllCustomers(pageable).map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }
    }

    @GetMapping("{codCli}")
    fun getCustomerByCodCli(@PathVariable codCli: Long): CustomerIngestionDto {
        return this.customerService.getCustomerByCodCli(codCli).toIngestionDto(companyProvider.getCompanyCnpj())
    }

    @PostMapping("{codCli}/send")
    fun sendCustomerToIngestion(@PathVariable codCli: Long): ResponseEntity<Void> {
        this.customerService.sendCustomerToIngestion(codCli)
        return ResponseEntity.ok().build()
    }
}
