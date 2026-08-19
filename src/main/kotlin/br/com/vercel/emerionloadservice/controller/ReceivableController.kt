package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.client.dto.ReceivableIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.ReceivableIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.ReceivableService
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
@RequestMapping("receivable")
class ReceivableController(
    private val receivableService: ReceivableService,
    private val companyProvider: CompanyProvider
) {

    @GetMapping("all")
    fun getAllReceivables(@PageableDefault(size = 40) pageable: Pageable): Page<ReceivableIngestionDto> {
        return receivableService.getAllReceivables(pageable).map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }
    }

    @GetMapping("{codCli}")
    fun getReceivablesByCodCli(@PathVariable codCli: Long): List<ReceivableIngestionDto> {
        return receivableService.getReceivablesByCodCli(codCli)
            .map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }
    }

    @PostMapping("{codCli}/send")
    fun sendReceivablesToIngestion(@PathVariable codCli: Long): ResponseEntity<Void> {
        receivableService.sendReceivablesToIngestion(codCli)
        return ResponseEntity.ok().build()
    }
}

