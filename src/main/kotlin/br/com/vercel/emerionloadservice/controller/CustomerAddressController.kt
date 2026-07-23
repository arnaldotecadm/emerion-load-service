package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.client.dto.CustomerAddressIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.CustomerAddressIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.CustomerAddressService
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
@RequestMapping("customer-address")
class CustomerAddressController(
    private val customerAddressService: CustomerAddressService,
    private val companyProvider: CompanyProvider
) {

    @GetMapping("all")
    fun getAllAddresses(@PageableDefault(size = 40) pageable: Pageable): Page<CustomerAddressIngestionDto> {
        return this.customerAddressService.getAllAddresses(pageable).map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }
    }

    @GetMapping("{codCli}")
    fun getAddressByCodCli(@PathVariable codCli: Long): CustomerAddressIngestionDto {
        return this.customerAddressService.getAddressByCodCli(codCli).toIngestionDto(companyProvider.getCompanyCnpj())
    }

    @PostMapping("{codCli}/send")
    fun sendAddressToIngestion(@PathVariable codCli: Long): ResponseEntity<Void> {
        this.customerAddressService.sendAddressToIngestion(codCli)
        return ResponseEntity.ok().build()
    }
}

