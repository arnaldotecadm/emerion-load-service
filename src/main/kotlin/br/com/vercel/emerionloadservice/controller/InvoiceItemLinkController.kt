package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.client.dto.InvoiceItemLinkIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.InvoiceItemLinkIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.InvoiceItemLinkService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("invoice-item-link")
class InvoiceItemLinkController(
    private val invoiceItemLinkService: InvoiceItemLinkService,
    private val companyProvider: CompanyProvider
) {

    @GetMapping("all")
    fun getAllInvoiceItemLinks(@PageableDefault(size = 40) pageable: Pageable): Page<InvoiceItemLinkIngestionDto> {
        return invoiceItemLinkService.getAllInvoiceItemLinks(pageable).map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }
    }

    @GetMapping("{codEmp}/{dteres}/{numres}/{seqRe2}")
    fun getInvoiceItemLinksByOrderItem(
        @PathVariable codEmp: Int,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dteres: LocalDate,
        @PathVariable numres: String,
        @PathVariable seqRe2: Int
    ): List<InvoiceItemLinkIngestionDto> {
        return invoiceItemLinkService.getInvoiceItemLinksByOrderItem(codEmp, dteres, numres, seqRe2)
            .map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }
    }

    @PostMapping("{codEmp}/{dteres}/{numres}/{seqRe2}/send")
    fun sendInvoiceItemLinksToIngestion(
        @PathVariable codEmp: Int,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dteres: LocalDate,
        @PathVariable numres: String,
        @PathVariable seqRe2: Int
    ): ResponseEntity<Void> {
        invoiceItemLinkService.sendInvoiceItemLinksToIngestion(codEmp, dteres, numres, seqRe2)
        return ResponseEntity.ok().build()
    }
}

