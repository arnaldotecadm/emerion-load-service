package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Pedlib
import br.com.vercel.emerionloadservice.repository.PedlibQueryRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PedlibService(
    private val pedlibQueryRepository: PedlibQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient,
    private val chunkService: ChunkService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllPedlib(pageable: Pageable): Page<Pedlib> = pedlibQueryRepository.findAllPaged(pageable)

    fun processRecordsToWebservice(pageable: Pageable?) {
        val pedlibs = pedlibQueryRepository.findAllToWebService(pageable ?: Pageable.ofSize(500))

        chunkService.chunkByJsonSizePerNumber(pedlibs.content, 25).forEach { batch ->
            try {
                ingestionServiceClient.sendPedlib(batch)
                batch.forEach(pedlibQueryRepository::updateSentToWebService)
            } catch (e: Exception) {
                logger.error(
                    "Error while processing pedlib batch {}: {}",
                    batch.map { "${it.codigoEmpresa}/${it.dataPedido}/${it.numeroPedido}/${it.numeroLiberacao}" },
                    e.message,
                )
            }
        }
    }

    fun getPedlibByKey(numres: String): Pedlib =
        pedlibQueryRepository.findByKey(numres)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    fun sendPedlibToIngestion(numres: String) {
        ingestionServiceClient.sendPedlib(listOf(getPedlibByKey(numres)))
    }
}
