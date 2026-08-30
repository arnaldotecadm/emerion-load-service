package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.client.dto.VendedorIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.VendedorIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.VendedorService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("vendedor")
class VendedorController(
    private val vendedorService: VendedorService,
    private val companyProvider: CompanyProvider
) {

    @GetMapping("all")
    fun getAllVendedores(@PageableDefault(size = 40) pageable: Pageable): Page<VendedorIngestionDto> {
        return this.vendedorService.getAllVendedores(pageable)
            .map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }
    }

    @GetMapping("{codVen}")
    fun getVendedorByCodVen(@PathVariable codVen: Long): VendedorIngestionDto {
        return this.vendedorService.getVendedorByCodVen(codVen).toIngestionDto(companyProvider.getCompanyCnpj())
    }

    @PostMapping("{codVen}/send")
    fun sendVendedorToIngestion(@PathVariable codVen: Long): ResponseEntity<Void> {
        this.vendedorService.sendVendedorToIngestion(codVen)
        return ResponseEntity.ok().build()
    }
}

