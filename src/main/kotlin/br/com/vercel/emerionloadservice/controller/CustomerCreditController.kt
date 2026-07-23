package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.client.dto.CustomerCreditIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.CustomerCreditIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.CustomerCreditService
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
@RequestMapping("customer-credit")
class CustomerCreditController(
    private val customerCreditService: CustomerCreditService,
    private val companyProvider: CompanyProvider
) {

    @GetMapping("all")
    fun getAllCredits(@PageableDefault(size = 40) pageable: Pageable): Page<CustomerCreditIngestionDto> {
        return this.customerCreditService.getAllCredits(pageable).map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }
    }

    @GetMapping("{codCli}")
    fun getCreditsByCodCli(@PathVariable codCli: Long): List<CustomerCreditIngestionDto> {
        return this.customerCreditService.getCreditsByCodCli(codCli).toIngestionDto(companyProvider.getCompanyCnpj())
    }

    @PostMapping("{codCli}/send")
    fun sendCreditsToIngestion(@PathVariable codCli: Long): ResponseEntity<Void> {
        this.customerCreditService.sendCreditsToIngestion(codCli)
        return ResponseEntity.ok().build()
    }
}

