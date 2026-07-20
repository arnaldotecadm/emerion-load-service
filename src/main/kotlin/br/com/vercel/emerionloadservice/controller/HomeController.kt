package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.repository.ProductRepository
import br.com.vercel.emerionloadservice.service.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
class HomeController(
    private val service: ProductService
) {

    @GetMapping("ping")
    fun ping(): String {
        val products = this.service.getProducts()
        return "ping"
    }
}