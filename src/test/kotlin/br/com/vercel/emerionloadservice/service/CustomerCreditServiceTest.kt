package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.CustomerCredit
import br.com.vercel.emerionloadservice.repository.CustomerCreditQueryRepository
import br.com.vercel.emerionloadservice.repository.CustomerCreditRepository
import org.junit.jupiter.api.Assertions.assertSame
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

@ExtendWith(MockitoExtension::class)
class CustomerCreditServiceTest {
    @MockitoBean
    val customerCreditRepositoryMock: CustomerCreditRepository = mock()

    @MockitoBean
    val customerCreditQueryRepositoryMock: CustomerCreditQueryRepository = mock()

    @MockitoBean
    val ingestionServiceClientMock: IngestionServiceClient = mock()

    @InjectMocks
    lateinit var customerCreditService: CustomerCreditService

    @Test
    fun `should return the customer credit page provided by the query repository`() {
        val page = PageImpl(listOf(mock<CustomerCredit>()))
        whenever(customerCreditQueryRepositoryMock.findAllPaged(any())).thenReturn(page)

        assertSame(page, customerCreditService.getAllCredits(Pageable.ofSize(10)))
    }

    @Test
    fun `should return all credits for a customer`() {
        val credits = listOf(mock<CustomerCredit>(), mock<CustomerCredit>())
        whenever(customerCreditRepositoryMock.getCreditsByCodCli(42L)).thenReturn(credits)

        assertSame(credits, customerCreditService.getCreditsByCodCli(42L))
    }

    @Test
    fun `should send an empty credit list so the ingestion client owns the no-op decision`() {
        whenever(customerCreditRepositoryMock.getCreditsByCodCli(42L)).thenReturn(emptyList())

        customerCreditService.sendCreditsToIngestion(42L)

        verify(ingestionServiceClientMock).sendCustomerCredits(emptyList())
    }

    @Test
    fun `should send all resolved credits to ingestion`() {
        val credits = listOf(mock<CustomerCredit>())
        whenever(customerCreditRepositoryMock.getCreditsByCodCli(42L)).thenReturn(credits)

        customerCreditService.sendCreditsToIngestion(42L)

        verify(ingestionServiceClientMock).sendCustomerCredits(credits)
    }
}
