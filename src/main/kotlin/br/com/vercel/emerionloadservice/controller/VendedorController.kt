package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.model.Vendedor
import br.com.vercel.emerionloadservice.service.VendedorService
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
@RequestMapping("vendedor")
class VendedorController(private val vendedorService: VendedorService) {

    @GetMapping("all")
    fun getAllVendedores(@PageableDefault(size = 40) pageable: Pageable): Page<Vendedor> {
        return this.vendedorService.getAllVendedores(pageable)
    }

    @GetMapping("{codVen}")
    fun getVendedorByCodVen(@PathVariable codVen: Long): Vendedor {
        return this.vendedorService.getVendedorByCodVen(codVen)
    }

    @PostMapping("{codVen}/send")
    fun sendVendedorToIngestion(@PathVariable codVen: Long): ResponseEntity<Void> {
        this.vendedorService.sendVendedorToIngestion(codVen)
        return ResponseEntity.ok().build()
    }
}
