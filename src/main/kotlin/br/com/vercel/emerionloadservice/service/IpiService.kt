package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Ipi
import br.com.vercel.emerionloadservice.repository.IpiQueryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class IpiService(
    private val ipiQueryRepository: IpiQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient,
) {
    fun getAllIpi(pageable: Pageable): Page<Ipi> = ipiQueryRepository.findAllPaged(pageable)

    fun getIpiByKey(
        codipi: String,
        tipipi: String,
    ): Ipi =
        ipiQueryRepository.findByKey(codipi, tipipi)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    fun sendIpiToIngestion(
        codipi: String,
        tipipi: String,
    ) {
        val ipi = getIpiByKey(codipi, tipipi)
        ingestionServiceClient.sendIpi(ipi)
    }
}
