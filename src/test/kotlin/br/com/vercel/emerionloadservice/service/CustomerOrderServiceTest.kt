package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.CustomerOrder
import br.com.vercel.emerionloadservice.repository.CustomerOrderQueryRepository
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
class CustomerOrderServiceTest {
    @MockitoBean
    val customerOrderQueryRepositoryMock: CustomerOrderQueryRepository = mock()

    @MockitoBean
    val ingestionServiceClientMock: IngestionServiceClient = mock()

    @InjectMocks
    lateinit var customerOrderService: CustomerOrderService

    @Test
    fun `should return the order page provided by the repository`() {
        val page = PageImpl(listOf(mock<CustomerOrder>()))
        whenever(customerOrderQueryRepositoryMock.findAllPaged(any())).thenReturn(page)

        assertSame(page, customerOrderService.getAllOrders(Pageable.ofSize(10)))
    }

    @Test
    fun `should return an order by its number`() {
        val order = mock<CustomerOrder>()
        whenever(customerOrderQueryRepositoryMock.findByKey("ORDER-42")).thenReturn(order)

        assertSame(order, customerOrderService.getOrderByNumres("ORDER-42"))
    }

    @Test
    fun `should throw not found without sending when order does not exist`() {
        whenever(customerOrderQueryRepositoryMock.findByKey("missing")).thenReturn(null)

        val exception = assertThrows<ResponseStatusException> { customerOrderService.sendOrderToIngestion("missing") }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        verifyNoInteractions(ingestionServiceClientMock)
    }

    @Test
    fun `should send the resolved order to ingestion`() {
        val order = mock<CustomerOrder>()
        whenever(customerOrderQueryRepositoryMock.findByKey("ORDER-42")).thenReturn(order)

        customerOrderService.sendOrderToIngestion("ORDER-42")

        verify(ingestionServiceClientMock).sendCustomerOrder(order)
    }
}
