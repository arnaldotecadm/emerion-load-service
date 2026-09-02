package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.repository.VendedorQueryRepository
import br.com.vercel.emerionloadservice.utils.Utils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.server.ResponseStatusException

@ExtendWith(MockitoExtension::class)
class VendedorServiceTest {
    @MockitoBean
    val vendedorQueryRepositoryMock: VendedorQueryRepository = mock()

    @MockitoBean
    val ingestionServiceClientMock: IngestionServiceClient = mock()

    @InjectMocks
    lateinit var vendedorService: VendedorService

    @Test
    fun `should return mapped vendedores from the repository page`() {
        val pageImpl =
            PageImpl(
                List(10) { index -> Utils.buildVendedorProjection(id = 100L + index, nome = "Vendedor ${index + 1}") },
            )
        whenever { vendedorQueryRepositoryMock.findAllPaged(any()) }.thenReturn(pageImpl)
        val result = vendedorService.getAllVendedores(Pageable.ofSize(10))
        assertEquals(10, result.content.size)
        assertEquals(10, result.totalElements)
        assertEquals(1, result.totalPages)
        assertEquals(10, result.numberOfElements)
    }

    @Test
    fun `should return vendedor by its id`() {
        whenever { vendedorQueryRepositoryMock.findByCodVen(1L) }.thenReturn(
            Utils.buildVendedorProjection(
                id = 1L,
                nome = "Arnaldo",
            ),
        )
        val result = vendedorService.getVendedorByCodVen(1L)
        assertEquals(1L, result.id)
        assertEquals("Arnaldo", result.nome)
    }

    @Test
    fun `should throw exception when vendedor not found`() {
        whenever { vendedorQueryRepositoryMock.findByCodVen(999L) }.thenReturn(null)
        val exception = assertThrows<ResponseStatusException> { vendedorService.getVendedorByCodVen(999L) }
        assertEquals(404, exception.statusCode.value())
    }

    @Test
    fun `should send the resolved vendedor to ingestion`() {
        whenever { vendedorQueryRepositoryMock.findByCodVen(1L) }.thenReturn(
            Utils.buildVendedorProjection(
                id = 1L,
                nome = "Arnaldo",
            ),
        )
        vendedorService.sendVendedorToIngestion(1L)
        verify(ingestionServiceClientMock).sendVendedor(any())
    }

    @Test
    fun `should throw exception when sending vendedor to ingestion and vendedor not found`() {
        whenever { vendedorQueryRepositoryMock.findByCodVen(999L) }.thenReturn(null)
        val exception = assertThrows<ResponseStatusException> { vendedorService.sendVendedorToIngestion(999L) }
        assertEquals(404, exception.statusCode.value())
        verify(ingestionServiceClientMock, times(0)).sendVendedor(any())
    }
}
