package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.repository.CustomerQueryRepository
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
class CustomerServiceTest {
    @MockitoBean
    val customerQueryRepositoryMock: CustomerQueryRepository = mock()

    @MockitoBean
    val ingestionServiceClientMock: IngestionServiceClient = mock()

    @InjectMocks
    lateinit var customerService: CustomerService

    @Test
    fun `should return the customer page provided by the repository`() {
        val page = PageImpl(listOf(mock<Customer>()))
        whenever(customerQueryRepositoryMock.findAllPaged(any())).thenReturn(page)

        assertSame(page, customerService.getAllCustomers(Pageable.ofSize(10)))
    }

    @Test
    fun `should return a customer by its external id`() {
        val customer = mock<Customer>()
        whenever(customerQueryRepositoryMock.getCustomerByCodCli(42L)).thenReturn(customer)

        assertSame(customer, customerService.getCustomerByCodCli(42L))
    }

    @Test
    fun `should throw not found without sending when customer does not exist`() {
        whenever(customerQueryRepositoryMock.getCustomerByCodCli(404L)).thenReturn(null)

        val exception = assertThrows<ResponseStatusException> { customerService.sendCustomerToIngestion(404L) }

        org.junit.jupiter.api.Assertions
            .assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        verifyNoInteractions(ingestionServiceClientMock)
    }

    @Test
    fun `should send the resolved customer to ingestion`() {
        val customer = mock<Customer>()
        whenever(customerQueryRepositoryMock.getCustomerByCodCli(42L)).thenReturn(customer)

        customerService.sendCustomerToIngestion(42L)

        verify(ingestionServiceClientMock).sendCustomer(customer)
    }
}
