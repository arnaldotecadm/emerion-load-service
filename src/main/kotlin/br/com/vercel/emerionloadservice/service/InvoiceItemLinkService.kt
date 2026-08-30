package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.InvoiceItemLink
import br.com.vercel.emerionloadservice.repository.InvoiceItemLinkQueryRepository
import br.com.vercel.emerionloadservice.repository.mapper.InvoiceItemLinkMapper.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class InvoiceItemLinkService(
    private val invoiceItemLinkQueryRepository: InvoiceItemLinkQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient,
) {
    fun getAllInvoiceItemLinks(pageable: Pageable): Page<InvoiceItemLink> = invoiceItemLinkQueryRepository.findAllPaged(pageable).toModel()

    fun getInvoiceItemLinksByOrderItem(
        codEmp: Int,
        dteres: LocalDate,
        numres: String,
        seqRe2: Int,
    ): List<InvoiceItemLink> =
        invoiceItemLinkQueryRepository.findByOrderItem(codEmp, dteres, numres, seqRe2).map {
            it.toModel()
        }

    fun sendInvoiceItemLinksToIngestion(
        codEmp: Int,
        dteres: LocalDate,
        numres: String,
        seqRe2: Int,
    ) {
        val links = getInvoiceItemLinksByOrderItem(codEmp, dteres, numres, seqRe2)
        ingestionServiceClient.sendInvoiceItemLinks(links)
    }
}
