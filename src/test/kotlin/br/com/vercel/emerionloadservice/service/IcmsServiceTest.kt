package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Icms
import br.com.vercel.emerionloadservice.repository.IcmsQueryRepository
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
class IcmsServiceTest {
    @MockitoBean
    val icmsQueryRepositoryMock: IcmsQueryRepository = mock()

    @MockitoBean
    val ingestionServiceClientMock: IngestionServiceClient = mock()

    @InjectMocks
    lateinit var icmsService: IcmsService

    @Test
    fun `should return the icms page provided by the repository`() {
        val page = PageImpl(listOf(mock<Icms>()))
        whenever(icmsQueryRepositoryMock.findAllPaged(any())).thenReturn(page)

        assertSame(page, icmsService.getAllIcms(Pageable.ofSize(10)))
    }

    @Test
    fun `should return icms by its composite key`() {
        val icms = mock<Icms>()
        whenever(icmsQueryRepositoryMock.findByKey("001", "N")).thenReturn(icms)

        assertSame(icms, icmsService.getIcmsByKey("001", "N"))
    }

    @Test
    fun `should throw not found without sending when icms does not exist`() {
        whenever(icmsQueryRepositoryMock.findByKey("missing", "N")).thenReturn(null)

        val exception = assertThrows<ResponseStatusException> { icmsService.sendIcmsToIngestion("missing", "N") }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        verifyNoInteractions(ingestionServiceClientMock)
    }

    @Test
    fun `should send the resolved icms to ingestion`() {
        val icms = mock<Icms>()
        whenever(icmsQueryRepositoryMock.findByKey("001", "N")).thenReturn(icms)

        icmsService.sendIcmsToIngestion("001", "N")

        verify(ingestionServiceClientMock).sendIcms(icms)
    }
}
