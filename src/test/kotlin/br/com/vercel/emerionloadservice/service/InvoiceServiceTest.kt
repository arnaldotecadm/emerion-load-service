package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.repository.InvoiceQueryRepository
import br.com.vercel.emerionloadservice.repository.projection.InvoiceProjectionImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class InvoiceServiceTest {
    @MockitoBean
    val invoiceQueryRepositoryMock: InvoiceQueryRepository = mock()

    @MockitoBean
    val ingestionServiceClientMock: IngestionServiceClient = mock()

    @InjectMocks
    lateinit var invoiceService: InvoiceService

    @Test
    fun `should map paged invoice projections to invoices`() {
        whenever(invoiceQueryRepositoryMock.findAllPaged(any())).thenReturn(PageImpl(listOf(invoiceProjection())))

        val result = invoiceService.getAllInvoices(Pageable.ofSize(10))

        assertEquals(1, result.totalElements)
        assertEquals(LocalDate.of(2026, 1, 2), result.content.single().dteres)
    }

    @Test
    fun `should map invoices returned for an order`() {
        whenever(invoiceQueryRepositoryMock.findByOrder(1, LocalDate.of(2026, 1, 2), "ORDER-42")).thenReturn(
            listOf(invoiceProjection()),
        )

        val result = invoiceService.getInvoicesByOrder(1, LocalDate.of(2026, 1, 2), "ORDER-42")

        assertEquals("ORDER-42", result.single().numres)
    }

    @Test
    fun `should send an empty invoice list so the ingestion client owns the no-op decision`() {
        whenever(invoiceQueryRepositoryMock.findByOrder(1, LocalDate.of(2026, 1, 2), "missing")).thenReturn(emptyList())

        invoiceService.sendInvoicesToIngestion(1, LocalDate.of(2026, 1, 2), "missing")

        verify(ingestionServiceClientMock).sendInvoices(emptyList())
    }

    @Test
    fun `should send mapped invoices to ingestion`() {
        whenever(invoiceQueryRepositoryMock.findByOrder(1, LocalDate.of(2026, 1, 2), "ORDER-42")).thenReturn(
            listOf(invoiceProjection()),
        )

        invoiceService.sendInvoicesToIngestion(1, LocalDate.of(2026, 1, 2), "ORDER-42")

        verify(ingestionServiceClientMock).sendInvoices(
            org.mockito.kotlin.check {
                assertEquals("ORDER-42", it.single().numres)
            },
        )
    }

    private fun invoiceProjection() =
        InvoiceProjectionImpl(
            codEmp = 1,
            codCli = 42L,
            numres = "ORDER-42",
            dteres = LocalDateTime.of(2026, 1, 2, 0, 0),
            nronfs = "NF-1",
            dataFaturamento = LocalDateTime.of(2026, 1, 3, 0, 0),
            totalFaturado = 99.5,
        )
}
