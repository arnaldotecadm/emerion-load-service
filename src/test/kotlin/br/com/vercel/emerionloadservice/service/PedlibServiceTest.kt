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
import org.mockito.kotlin.eq
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

    @MockitoBean
    val chunkServiceMock: ChunkService = mock()

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
        whenever(pedlibQueryRepositoryMock.findByKey(123L)).thenReturn(pedlib)

        assertSame(pedlib, pedlibService.getPedlibByKey(123L))
    }

    @Test
    fun `should send pending pedlibs and mark them as sent`() {
        val pedlib = mock<Pedlib>()
        whenever(pedlibQueryRepositoryMock.findAllToWebService(any())).thenReturn(PageImpl(listOf(pedlib)))
        whenever(chunkServiceMock.chunkByJsonSizePerNumber<Pedlib>(any(), eq(25))).thenReturn(listOf(listOf(pedlib)))

        pedlibService.processRecordsToWebservice(Pageable.ofSize(10))

        verify(ingestionServiceClientMock).sendPedlib(listOf(pedlib))
        verify(pedlibQueryRepositoryMock).updateSentToWebService(pedlib)
    }

    @Test
    fun `should throw not found without sending when pedlib does not exist`() {
        whenever(pedlibQueryRepositoryMock.findByKey(999L)).thenReturn(null)

        val exception = assertThrows<ResponseStatusException> { pedlibService.sendPedlibToIngestion(999L) }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        verifyNoInteractions(ingestionServiceClientMock)
    }

    @Test
    fun `should send the resolved pedlib to ingestion`() {
        val pedlib = mock<Pedlib>()
        whenever(pedlibQueryRepositoryMock.findByKey(123L)).thenReturn(pedlib)

        pedlibService.sendPedlibToIngestion(123L)

        verify(ingestionServiceClientMock).sendPedlib(listOf(pedlib))
    }
}
