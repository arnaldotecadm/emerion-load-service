package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.model.CustomerOrder
import br.com.vercel.emerionloadservice.service.CustomerOrderService
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
@RequestMapping("customer-order")
class CustomerOrderController(private val customerOrderService: CustomerOrderService) {

    @GetMapping("all")
    fun getAllOrders(@PageableDefault(size = 40) pageable: Pageable): Page<CustomerOrder> {
        return this.customerOrderService.getAllOrders(pageable)
    }

    @GetMapping("{numres}")
    fun getOrderByNumres(@PathVariable numres: String): CustomerOrder {
        return this.customerOrderService.getOrderByNumres(numres)
    }

    @PostMapping("{numres}/send")
    fun sendOrderToIngestion(@PathVariable numres: String): ResponseEntity<Void> {
        this.customerOrderService.sendOrderToIngestion(numres)
        return ResponseEntity.ok().build()
    }
}
