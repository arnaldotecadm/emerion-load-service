package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Vendedor
import br.com.vercel.emerionloadservice.repository.VendedorQueryRepository
import br.com.vercel.emerionloadservice.repository.mapper.VendedorMapper.toModel
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class VendedorService(
    private val vendedorQueryRepository: VendedorQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllVendedores(pageable: Pageable): Page<Vendedor> = vendedorQueryRepository.findAllPaged(pageable).toModel()

    fun processRecordsToWebservice(pageable: Pageable?) {
        val vendedores = vendedorQueryRepository.findAllToWebService(pageable ?: Pageable.ofSize(500))
        val listOfUpdated =
            vendedores
                .map {
                    try {
                        ingestionServiceClient.sendVendedor(it)
                        it.id to true
                    } catch (e: Exception) {
                        logger.error("Error while sending vendedor ${it.id}", e)
                        it.id to false
                    }
                }.filter { it.second }
                .toList()
                .joinToString(separator = ", ") { it.first.toString() }
        if (listOfUpdated.isNotEmpty()) {
            vendedorQueryRepository.updateSentToWebService(listOfUpdated)
        }
    }

    fun getVendedorByCodVen(codVen: Long): Vendedor =
        vendedorQueryRepository.findByCodVen(codVen)?.toModel()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    fun sendVendedorToIngestion(codVen: Long) {
        val vendedor = getVendedorByCodVen(codVen)
        ingestionServiceClient.sendVendedor(vendedor)
    }
}
