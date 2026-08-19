package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Invoice
import br.com.vercel.emerionloadservice.repository.InvoiceQueryRepository
import br.com.vercel.emerionloadservice.repository.mapper.InvoiceMapper.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class InvoiceService(
    private val invoiceQueryRepository: InvoiceQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient
) {
    fun getAllInvoices(pageable: Pageable): Page<Invoice> {
        return invoiceQueryRepository.findAllPaged(pageable).toModel()
    }

    fun getInvoicesByOrder(codEmp: Int, dteres: LocalDate, numres: String): List<Invoice> {
        return invoiceQueryRepository.findByOrder(codEmp, dteres, numres).map { it.toModel() }
    }

    fun sendInvoicesToIngestion(codEmp: Int, dteres: LocalDate, numres: String) {
        val invoices = getInvoicesByOrder(codEmp, dteres, numres)
        ingestionServiceClient.sendInvoices(invoices)
    }
}

