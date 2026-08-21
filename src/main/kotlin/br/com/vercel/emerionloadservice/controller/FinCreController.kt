package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.client.dto.FinCreIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.FinCreIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.FinCreService
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
@RequestMapping("fincre")
class FinCreController(
    private val finCreService: FinCreService,
    private val companyProvider: CompanyProvider
) {

    @GetMapping("all")
    fun getAllFinCre(@PageableDefault(size = 40) pageable: Pageable): Page<FinCreIngestionDto> {
        return finCreService.getAllFinCre(pageable)
            .map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }
    }

    @GetMapping("{documento}")
    fun getFinCreByKey(
        @PathVariable documento: String
    ): FinCreIngestionDto {
        return finCreService.getFinCreByKey(documento)
            .toIngestionDto(companyProvider.getCompanyCnpj())
    }

    @PostMapping("{documento}/send")
    fun sendFinCreToIngestion(
        @PathVariable documento: String
    ): ResponseEntity<Void> {
        finCreService.sendFinCreToIngestion(documento)
        return ResponseEntity.ok().build()
    }
}
