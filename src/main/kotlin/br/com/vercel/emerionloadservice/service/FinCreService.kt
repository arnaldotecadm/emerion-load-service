package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.FinCre
import br.com.vercel.emerionloadservice.repository.FinCreQueryRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class FinCreService(
    private val finCreQueryRepository: FinCreQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient,
    private val chunkService: ChunkService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllFinCre(pageable: Pageable): Page<FinCre> = finCreQueryRepository.findAllPaged(pageable)

    fun processRecordsToWebservice(pageable: Pageable?) {
        val finCres = finCreQueryRepository.findAllToWebService(pageable ?: Pageable.ofSize(500))
        val batches = chunkService.chunkByJsonSize(finCres.content)
        val sentFinCres =
            batches
                .map { batch ->
                    try {
                        ingestionServiceClient.sendFinCre(batch)
                        batch to true
                    } catch (e: Exception) {
                        logger.error("Error while sending fincre ${batch.map { it.documento }}", e)
                        batch to false
                    }
                }.filter { it.second }
                .flatMap { it.first }
                .toList()

        if (sentFinCres.isNotEmpty()) {
            finCreQueryRepository.updateSentToWebService(sentFinCres)
        }
    }

    fun getFinCreByKey(documento: String): FinCre =
        finCreQueryRepository.findByKey(documento)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    fun sendFinCreToIngestion(documento: String) {
        val finCre = getFinCreByKey(documento)
        ingestionServiceClient.sendFinCre(finCre)
    }
}
