package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.CustomerQuotation
import br.com.vercel.emerionloadservice.repository.CustomerQuotationRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.server.ResponseStatusException

@ExtendWith(MockitoExtension::class)
class CustomerQuotationServiceTest {
    @MockitoBean
    val chunkServiceMock: ChunkService = mock()

    @MockitoBean
    val customerQuotationRepositoryMock: CustomerQuotationRepository = mock()

    @MockitoBean
    val ingestionServiceClientMock: IngestionServiceClient = mock()

    @InjectMocks
    lateinit var customerQuotationService: CustomerQuotationService

    @Test
    fun `should return the quotation page provided by the repository`() {
        val page = PageImpl(listOf(mock<CustomerQuotation>()))
        whenever(customerQuotationRepositoryMock.findAllPaged(any())).thenReturn(page)

        assertSame(page, customerQuotationService.getAllQuotations(Pageable.ofSize(10)))
    }

    @Test
    fun `should send the resolved quotation to ingestion`() {
        val quotation = mock<CustomerQuotation>()
        whenever(customerQuotationRepositoryMock.findByKey("QUOTE-42")).thenReturn(quotation)

        customerQuotationService.sendQuotationToIngestion("QUOTE-42")

        verify(ingestionServiceClientMock).sendCustomerQuotation(listOf(quotation))
    }

    @Test
    fun `should throw not found without sending when quotation does not exist`() {
        whenever(customerQuotationRepositoryMock.findByKey("missing")).thenReturn(null)

        val exception = assertThrows<ResponseStatusException> { customerQuotationService.sendQuotationToIngestion("missing") }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        verifyNoInteractions(ingestionServiceClientMock)
    }
}
