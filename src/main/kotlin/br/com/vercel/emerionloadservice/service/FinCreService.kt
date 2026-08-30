package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.FinCre
import br.com.vercel.emerionloadservice.repository.FinCreQueryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class FinCreService(
    private val finCreQueryRepository: FinCreQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient,
) {
    fun getAllFinCre(pageable: Pageable): Page<FinCre> = finCreQueryRepository.findAllPaged(pageable)

    fun getFinCreByKey(documento: String): FinCre =
        finCreQueryRepository.findByKey(documento)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    fun sendFinCreToIngestion(documento: String) {
        val finCre = getFinCreByKey(documento)
        ingestionServiceClient.sendFinCre(finCre)
    }
}
