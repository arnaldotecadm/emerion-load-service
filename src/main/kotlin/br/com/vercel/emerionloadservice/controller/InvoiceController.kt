package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.client.dto.InvoiceIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.InvoiceIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.InvoiceService
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
@RequestMapping("invoice")
class InvoiceController(
    private val invoiceService: InvoiceService,
    private val companyProvider: CompanyProvider
) {

    @GetMapping("all")
    fun getAllInvoices(@PageableDefault(size = 40) pageable: Pageable): Page<InvoiceIngestionDto> {
        return invoiceService.getAllInvoices(pageable).map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }
    }

    @GetMapping("{codEmp}/{dteres}/{numres}")
    fun getInvoicesByOrder(
        @PathVariable codEmp: Int,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dteres: LocalDate,
        @PathVariable numres: String
    ): List<InvoiceIngestionDto> {
        return invoiceService.getInvoicesByOrder(codEmp, dteres, numres)
            .map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }
    }

    @PostMapping("{codEmp}/{dteres}/{numres}/send")
    fun sendInvoicesToIngestion(
        @PathVariable codEmp: Int,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dteres: LocalDate,
        @PathVariable numres: String
    ): ResponseEntity<Void> {
        invoiceService.sendInvoicesToIngestion(codEmp, dteres, numres)
        return ResponseEntity.ok().build()
    }
}

