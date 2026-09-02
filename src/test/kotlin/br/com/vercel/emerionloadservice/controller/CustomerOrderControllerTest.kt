package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.model.CustomerOrder
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.CustomerOrderService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@WebMvcTest(CustomerOrderController::class)
class CustomerOrderControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    val customerOrderServiceMock: CustomerOrderService = mock()

    @MockitoBean
    val companyProviderMock: CompanyProvider = mock()

    @BeforeEach
    fun setup() {
        whenever(companyProviderMock.getCompanyCnpj()).thenReturn("12345678901234")
        whenever(customerOrderServiceMock.getAllOrders(any())).thenReturn(Page.empty())
    }

    @Test
    fun `should return an empty customer order page`() {
        mockMvc
            .perform(get("/customer-order/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isEmpty)

        verify(customerOrderServiceMock).getAllOrders(any())
    }

    @Test
    fun `should return a populated customer order page with customer and retailer identities`() {
        val order = mock<CustomerOrder>()
        whenever(order.codigoEmpresa).thenReturn(1)
        whenever(order.codigoCliente).thenReturn(42L)
        whenever(order.numeroPedido).thenReturn("ORDER-42")
        whenever(order.dataPedido).thenReturn(LocalDateTime.of(2026, 1, 2, 0, 0))
        whenever(order.totalPedidoComImpostos).thenReturn(100.0)
        whenever(order.totalPedidoSemImpostos).thenReturn(90.0)
        whenever(order.itens).thenReturn(emptyList())
        whenever(customerOrderServiceMock.getAllOrders(any())).thenReturn(PageImpl(listOf(order)))

        mockMvc
            .perform(get("/customer-order/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].customerExternalId").value(42))
            .andExpect(jsonPath("$.content[0].cnpjEmpresa").value("12345678901234"))
    }

    @Test
    fun `should send an order to ingestion`() {
        mockMvc
            .perform(post("/customer-order/ORDER-42/send"))
            .andExpect(status().isOk)

        verify(customerOrderServiceMock).sendOrderToIngestion("ORDER-42")
    }

    @Test
    fun `should return not found when the order does not exist`() {
        whenever(customerOrderServiceMock.getOrderByNumres("missing")).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mockMvc
            .perform(get("/customer-order/missing"))
            .andExpect(status().isNotFound)
    }
}
