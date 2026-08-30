package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.client.dto.IpiIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.IpiIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.IpiService
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
@RequestMapping("ipi")
class IpiController(
    private val ipiService: IpiService,
    private val companyProvider: CompanyProvider,
) {
    @GetMapping("all")
    fun getAllIpi(
        @PageableDefault(size = 40) pageable: Pageable,
    ): Page<IpiIngestionDto> = ipiService.getAllIpi(pageable).map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }

    @GetMapping("{codipi}/{tipipi}")
    fun getIpiByKey(
        @PathVariable codipi: String,
        @PathVariable tipipi: String,
    ): IpiIngestionDto = ipiService.getIpiByKey(codipi, tipipi).toIngestionDto(companyProvider.getCompanyCnpj())

    @PostMapping("{codipi}/{tipipi}/send")
    fun sendIpiToIngestion(
        @PathVariable codipi: String,
        @PathVariable tipipi: String,
    ): ResponseEntity<Void> {
        ipiService.sendIpiToIngestion(codipi, tipipi)
        return ResponseEntity.ok().build()
    }
}
