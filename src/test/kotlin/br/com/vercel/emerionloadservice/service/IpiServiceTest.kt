package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Ipi
import br.com.vercel.emerionloadservice.repository.IpiQueryRepository
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
class IpiServiceTest {
    @MockitoBean
    val ipiQueryRepositoryMock: IpiQueryRepository = mock()

    @MockitoBean
    val ingestionServiceClientMock: IngestionServiceClient = mock()

    @InjectMocks
    lateinit var ipiService: IpiService

    @Test
    fun `should return the ipi page provided by the repository`() {
        val page = PageImpl(listOf(mock<Ipi>()))
        whenever(ipiQueryRepositoryMock.findAllPaged(any())).thenReturn(page)

        assertSame(page, ipiService.getAllIpi(Pageable.ofSize(10)))
    }

    @Test
    fun `should return ipi by its composite key`() {
        val ipi = mock<Ipi>()
        whenever(ipiQueryRepositoryMock.findByKey("001", "N")).thenReturn(ipi)

        assertSame(ipi, ipiService.getIpiByKey("001", "N"))
    }

    @Test
    fun `should throw not found without sending when ipi does not exist`() {
        whenever(ipiQueryRepositoryMock.findByKey("missing", "N")).thenReturn(null)

        val exception = assertThrows<ResponseStatusException> { ipiService.sendIpiToIngestion("missing", "N") }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        verifyNoInteractions(ingestionServiceClientMock)
    }

    @Test
    fun `should send the resolved ipi to ingestion`() {
        val ipi = mock<Ipi>()
        whenever(ipiQueryRepositoryMock.findByKey("001", "N")).thenReturn(ipi)

        ipiService.sendIpiToIngestion("001", "N")

        verify(ingestionServiceClientMock).sendIpi(ipi)
    }
}
