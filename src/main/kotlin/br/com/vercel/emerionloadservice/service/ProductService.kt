package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.repository.ProductRepository
import org.springframework.stereotype.Service
import java.util.Objects

@Service
class ProductService(
    private val repository: ProductRepository
) {

    fun getProducts(): List<Any>{
        return this.repository.getProducts()
    }
}