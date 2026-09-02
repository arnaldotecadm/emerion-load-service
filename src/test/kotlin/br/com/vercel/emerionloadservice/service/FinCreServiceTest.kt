package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.FinCre
import br.com.vercel.emerionloadservice.repository.FinCreQueryRepository
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
class FinCreServiceTest {
    @MockitoBean
    val finCreQueryRepositoryMock: FinCreQueryRepository = mock()

    @MockitoBean
    val ingestionServiceClientMock: IngestionServiceClient = mock()

    @InjectMocks
    lateinit var finCreService: FinCreService

    @Test
    fun `should return the fincre page provided by the repository`() {
        val page = PageImpl(listOf(mock<FinCre>()))
        whenever(finCreQueryRepositoryMock.findAllPaged(any())).thenReturn(page)

        assertSame(page, finCreService.getAllFinCre(Pageable.ofSize(10)))
    }

    @Test
    fun `should return fincre by its document`() {
        val finCre = mock<FinCre>()
        whenever(finCreQueryRepositoryMock.findByKey("123")).thenReturn(finCre)

        assertSame(finCre, finCreService.getFinCreByKey("123"))
    }

    @Test
    fun `should throw not found without sending when fincre does not exist`() {
        whenever(finCreQueryRepositoryMock.findByKey("missing")).thenReturn(null)

        val exception = assertThrows<ResponseStatusException> { finCreService.sendFinCreToIngestion("missing") }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        verifyNoInteractions(ingestionServiceClientMock)
    }

    @Test
    fun `should send the resolved fincre to ingestion`() {
        val finCre = mock<FinCre>()
        whenever(finCreQueryRepositoryMock.findByKey("123")).thenReturn(finCre)

        finCreService.sendFinCreToIngestion("123")

        verify(ingestionServiceClientMock).sendFinCre(finCre)
    }
}
