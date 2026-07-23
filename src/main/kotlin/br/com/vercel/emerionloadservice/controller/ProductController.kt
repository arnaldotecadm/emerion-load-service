package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.client.dto.ProductIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.ProductIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.ProductService
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
@RequestMapping("product")
class ProductController(
    private val productService: ProductService,
    private val companyProvider: CompanyProvider
) {

    @GetMapping("all")
    fun getAllProducts(@PageableDefault(size = 40) pageable: Pageable): Page<ProductIngestionDto> {
        return this.productService.getAllProducts(pageable).map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }
    }

    @GetMapping("{id}")
    fun getProductById(@PathVariable id: String): ProductIngestionDto {
        return this.productService.getProductById(id).toIngestionDto(companyProvider.getCompanyCnpj())
    }

    @PostMapping("{id}/send")
    fun sendProductToIngestion(@PathVariable id: String): ResponseEntity<Void> {
        this.productService.sendProductToIngestion(id)
        return ResponseEntity.ok().build()
    }
}

