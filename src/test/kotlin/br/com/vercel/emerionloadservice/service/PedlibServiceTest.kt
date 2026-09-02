package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Pedlib
import br.com.vercel.emerionloadservice.repository.PedlibQueryRepository
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
class PedlibServiceTest {
    @MockitoBean
    val pedlibQueryRepositoryMock: PedlibQueryRepository = mock()

    @MockitoBean
    val ingestionServiceClientMock: IngestionServiceClient = mock()

    @InjectMocks
    lateinit var pedlibService: PedlibService

    @Test
    fun `should return the pedlib page provided by the repository`() {
        val page = PageImpl(listOf(mock<Pedlib>()))
        whenever(pedlibQueryRepositoryMock.findAllPaged(any())).thenReturn(page)

        assertSame(page, pedlibService.getAllPedlib(Pageable.ofSize(10)))
    }

    @Test
    fun `should return pedlib by its number`() {
        val pedlib = mock<Pedlib>()
        whenever(pedlibQueryRepositoryMock.findByKey("123")).thenReturn(pedlib)

        assertSame(pedlib, pedlibService.getPedlibByKey("123"))
    }

    @Test
    fun `should throw not found without sending when pedlib does not exist`() {
        whenever(pedlibQueryRepositoryMock.findByKey("missing")).thenReturn(null)

        val exception = assertThrows<ResponseStatusException> { pedlibService.sendPedlibToIngestion("missing") }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        verifyNoInteractions(ingestionServiceClientMock)
    }

    @Test
    fun `should send the resolved pedlib to ingestion`() {
        val pedlib = mock<Pedlib>()
        whenever(pedlibQueryRepositoryMock.findByKey("123")).thenReturn(pedlib)

        pedlibService.sendPedlibToIngestion("123")

        verify(ingestionServiceClientMock).sendPedlib(pedlib)
    }
}
