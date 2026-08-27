package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Pedlib
import br.com.vercel.emerionloadservice.repository.PedlibQueryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PedlibService(
    private val pedlibQueryRepository: PedlibQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient
) {

    fun getAllPedlib(pageable: Pageable): Page<Pedlib> = pedlibQueryRepository.findAllPaged(pageable)

    fun getPedlibByKey(numres: String): Pedlib = pedlibQueryRepository.findByKey(numres)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    fun sendPedlibToIngestion(numres: String) {
        ingestionServiceClient.sendPedlib(getPedlibByKey(numres))
    }
}
