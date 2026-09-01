package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.api.model.PedlibIngestionDto
import br.com.vercel.emerionloadservice.client.mapper.PedlibIngestionMapper.toIngestionDto
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.PedlibService
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
@RequestMapping("pedlib")
class PedlibController(
    private val pedlibService: PedlibService,
    private val companyProvider: CompanyProvider,
) {
    @GetMapping("all")
    fun getAllPedlib(
        @PageableDefault(size = 40) pageable: Pageable,
    ): Page<PedlibIngestionDto> = pedlibService.getAllPedlib(pageable).map { it.toIngestionDto(companyProvider.getCompanyCnpj()) }

    @GetMapping("{numres}")
    fun getPedlibByKey(
        @PathVariable numres: String,
    ): PedlibIngestionDto = pedlibService.getPedlibByKey(numres).toIngestionDto(companyProvider.getCompanyCnpj())

    @PostMapping("{numres}/send")
    fun sendPedlibToIngestion(
        @PathVariable numres: String,
    ): ResponseEntity<Void> {
        pedlibService.sendPedlibToIngestion(numres)
        return ResponseEntity.ok().build()
    }
}
