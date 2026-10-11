package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.api.model.CustomerQuotationIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.CustomerQuotationIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.CustomerQuotationService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("customer-quotation")
class CustomerQuotationController(
    private val customerQuotationService: CustomerQuotationService,
    private val companyProvider: CompanyProvider,
) {
    @GetMapping("all")
    fun getAllQuotations(
        @PageableDefault(size = 40) pageable: Pageable,
    ): Page<CustomerQuotationIngestionDto> =
        customerQuotationService.getAllQuotations(pageable).map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }

    @GetMapping("{numres}")
    fun getQuotationByNumres(
        @PathVariable numres: String,
    ): CustomerQuotationIngestionDto =
        customerQuotationService.getQuotationByNumres(numres).toIngestionDto(companyProvider.getCompanyCnpj())

    @PostMapping("{numres}/send")
    fun sendQuotationToIngestion(
        @PathVariable numres: String,
    ): ResponseEntity<Void> {
        customerQuotationService.sendQuotationToIngestion(numres)
        return ResponseEntity.ok().build()
    }
}
