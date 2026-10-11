package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.CustomerQuotation
import br.com.vercel.emerionloadservice.repository.CustomerQuotationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CustomerQuotationService(
    private val customerQuotationRepository: CustomerQuotationRepository,
    private val ingestionServiceClient: IngestionServiceClient,
    private val chunkService: ChunkService,
) {
    fun getAllQuotations(pageable: Pageable): Page<CustomerQuotation> = customerQuotationRepository.findAllPaged(pageable)

    fun processRecordsToWebservice(pageable: Pageable?) {
        val quotations = customerQuotationRepository.findAllToWebService(pageable ?: Pageable.ofSize(500))
        chunkService.chunkByJsonSize(quotations.content).forEach { batch ->
            ingestionServiceClient.sendCustomerQuotation(batch)
            customerQuotationRepository.updateSentToWebService(batch.map { it.numeroPedido })
        }
    }

    fun getQuotationByNumres(numres: String): CustomerQuotation =
        customerQuotationRepository.findByKey(numres)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    fun sendQuotationToIngestion(numres: String) {
        ingestionServiceClient.sendCustomerQuotation(listOf(getQuotationByNumres(numres)))
    }
}
