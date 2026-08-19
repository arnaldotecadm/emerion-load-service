package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.client.dto.CustomerOrderIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.CustomerOrderIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.model.SendAllResult
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.CustomerOrderService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController()
@RequestMapping("customer-order")
class CustomerOrderController(
    private val customerOrderService: CustomerOrderService,
    private val companyProvider: CompanyProvider
) {

    @GetMapping("all")
    fun getAllOrders(@PageableDefault(size = 40) pageable: Pageable): Page<CustomerOrderIngestionDto> {
        return this.customerOrderService.getAllOrders(pageable).map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }
    }

    @GetMapping("{numres}")
    fun getOrderByNumres(@PathVariable numres: String): CustomerOrderIngestionDto {
        return this.customerOrderService.getOrderByNumres(numres).toIngestionDto(companyProvider.getCompanyCnpj())
    }

    @PostMapping("{numres}/send")
    fun sendOrderToIngestion(@PathVariable numres: String): ResponseEntity<Void> {
        this.customerOrderService.sendOrderToIngestion(numres)
        return ResponseEntity.ok().build()
    }

    @GetMapping("key/{codEmp}/{dteres}/{numres}")
    fun getOrderByBusinessKey(
        @PathVariable codEmp: Int,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dteres: LocalDate,
        @PathVariable numres: String
    ): CustomerOrderIngestionDto {
        return this.customerOrderService.getOrderByBusinessKey(codEmp, dteres, numres)
            .toIngestionDto(companyProvider.getCompanyCnpj())
    }

    @PostMapping("key/{codEmp}/{dteres}/{numres}/send")
    fun sendOrderToIngestionByBusinessKey(
        @PathVariable codEmp: Int,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dteres: LocalDate,
        @PathVariable numres: String
    ): ResponseEntity<Void> {
        this.customerOrderService.sendOrderToIngestion(codEmp, dteres, numres)
        return ResponseEntity.ok().build()
    }

    @PostMapping("send-all")
    fun sendAllOrdersToIngestion(
        @RequestParam(defaultValue = "40") pageSize: Int
    ): ResponseEntity<SendAllResult> {
        val result = this.customerOrderService.sendAllOrdersToIngestion(PageRequest.of(0, pageSize))
        return ResponseEntity.ok(result)
    }
}
