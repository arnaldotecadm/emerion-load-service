package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Ipi
import br.com.vercel.emerionloadservice.repository.IpiQueryRepository
import br.com.vercel.emerionloadservice.repository.mapper.IpiMapper.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class IpiService(
    private val ipiQueryRepository: IpiQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient
) {

    fun getAllIpi(pageable: Pageable): Page<Ipi> {
        return ipiQueryRepository.findAllPaged(pageable).toModel()
    }

    fun getIpiByKey(codipi: String, tipipi: String): Ipi {
        return ipiQueryRepository.findByKey(codipi, tipipi)?.toModel()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    fun sendIpiToIngestion(codipi: String, tipipi: String) {
        val ipi = getIpiByKey(codipi, tipipi)
        ingestionServiceClient.sendIpi(ipi)
    }
}
