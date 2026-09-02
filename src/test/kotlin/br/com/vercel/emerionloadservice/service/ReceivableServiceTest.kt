package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.repository.ReceivableQueryRepository
import br.com.vercel.emerionloadservice.repository.projection.ReceivableProjectionImpl
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
import java.time.Instant
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ReceivableServiceTest {
    @MockitoBean
    val receivableQueryRepositoryMock: ReceivableQueryRepository = mock()

    @MockitoBean
    val ingestionServiceClientMock: IngestionServiceClient = mock()

    @InjectMocks
    lateinit var receivableService: ReceivableService

    @Test
    fun `should map paged receivable projections to receivables`() {
        whenever(receivableQueryRepositoryMock.findAllPaged(any())).thenReturn(PageImpl(listOf(receivableProjection())))

        val result = receivableService.getAllReceivables(Pageable.ofSize(10))

        assertEquals(1, result.totalElements)
        assertEquals(42L, result.content.single().codCli)
    }

    @Test
    fun `should map receivables returned for a customer`() {
        whenever(receivableQueryRepositoryMock.findByCodCli(42L)).thenReturn(listOf(receivableProjection()))

        val result = receivableService.getReceivablesByCodCli(42L)

        assertEquals(20.0, result.single().saldoAberto)
    }

    @Test
    fun `should send an empty receivable list so the ingestion client owns the no-op decision`() {
        whenever(receivableQueryRepositoryMock.findByCodCli(42L)).thenReturn(emptyList())

        receivableService.sendReceivablesToIngestion(42L)

        verify(ingestionServiceClientMock).sendReceivables(emptyList())
    }

    @Test
    fun `should send mapped receivables to ingestion`() {
        whenever(receivableQueryRepositoryMock.findByCodCli(42L)).thenReturn(listOf(receivableProjection()))

        receivableService.sendReceivablesToIngestion(42L)

        verify(ingestionServiceClientMock).sendReceivables(
            org.mockito.kotlin.check {
                assertEquals(42L, it.single().codCli)
            },
        )
    }

    private fun receivableProjection() =
        ReceivableProjectionImpl(
            codCli = 42L,
            sequencia = "1",
            dataLancamento = Instant.parse("2026-01-02T00:00:00Z"),
            dataReferenciaPedido = LocalDateTime.of(2026, 1, 1, 0, 0),
            valorOriginal = 100.0,
            valorUtilizado = 80.0,
            saldoAberto = 20.0,
            situacao = "A",
        )
}
