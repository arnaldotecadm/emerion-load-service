package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.api.model.IcmsIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.IcmsIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.IcmsService
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
@RequestMapping("icms")
class IcmsController(
    private val icmsService: IcmsService,
    private val companyProvider: CompanyProvider,
) {
    @GetMapping("all")
    fun getAllIcms(
        @PageableDefault(size = 40) pageable: Pageable,
    ): Page<IcmsIngestionDto> = icmsService.getAllIcms(pageable).map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }

    @GetMapping("{codicm}/{tipicm}")
    fun getIcmsByKey(
        @PathVariable codicm: String,
        @PathVariable tipicm: String,
    ): IcmsIngestionDto = icmsService.getIcmsByKey(codicm, tipicm).toIngestionDto(companyProvider.getCompanyCnpj())

    @PostMapping("{codicm}/{tipicm}/send")
    fun sendIcmsToIngestion(
        @PathVariable codicm: String,
        @PathVariable tipicm: String,
    ): ResponseEntity<Void> {
        this.icmsService.sendIcmsToIngestion(codicm, tipicm)
        return ResponseEntity.ok().build()
    }
}
