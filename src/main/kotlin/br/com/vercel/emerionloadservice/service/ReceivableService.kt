package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Receivable
import br.com.vercel.emerionloadservice.repository.ReceivableQueryRepository
import br.com.vercel.emerionloadservice.repository.mapper.ReceivableMapper.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ReceivableService(
    private val receivableQueryRepository: ReceivableQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient,
) {
    fun getAllReceivables(pageable: Pageable): Page<Receivable> = receivableQueryRepository.findAllPaged(pageable).toModel()

    fun getReceivablesByCodCli(codCli: Long): List<Receivable> = receivableQueryRepository.findByCodCli(codCli).map { it.toModel() }

    fun sendReceivablesToIngestion(codCli: Long) {
        val receivables = getReceivablesByCodCli(codCli)
        ingestionServiceClient.sendReceivables(receivables)
    }
}
