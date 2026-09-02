package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.CustomerService
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

@WebMvcTest(CustomerController::class)
class CustomerControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    val customerServiceMock: CustomerService = mock()

    @MockitoBean
    val companyProviderMock: CompanyProvider = mock()

    @BeforeEach
    fun setup() {
        whenever(companyProviderMock.getCompanyCnpj()).thenReturn("12345678901234")
        whenever(customerServiceMock.getAllCustomers(any())).thenReturn(Page.empty())
    }

    @Test
    fun `should return an empty customer page`() {
        mockMvc
            .perform(get("/customer/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isEmpty)

        verify(customerServiceMock).getAllCustomers(any())
    }

    @Test
    fun `should return a populated customer page with the retailer identity`() {
        val customer = mock<Customer>()
        whenever(customer.id).thenReturn(42L)
        whenever(customer.nomeFantasia).thenReturn("Cliente")
        whenever(customer.razaoSocial).thenReturn("Cliente Ltda")
        whenever(customer.cpfCnpj).thenReturn("12345678000199")
        whenever(customer.bloqueado).thenReturn(false)
        whenever(customerServiceMock.getAllCustomers(any())).thenReturn(PageImpl(listOf(customer)))

        mockMvc
            .perform(get("/customer/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].externalId").value(42))
            .andExpect(jsonPath("$.content[0].cnpjEmpresa").value("12345678901234"))
    }

    @Test
    fun `should send a customer to ingestion`() {
        mockMvc
            .perform(post("/customer/42/send"))
            .andExpect(status().isOk)

        verify(customerServiceMock).sendCustomerToIngestion(42L)
    }

    @Test
    fun `should return not found when the customer does not exist`() {
        whenever(customerServiceMock.getCustomerByCodCli(404L)).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mockMvc
            .perform(get("/customer/404"))
            .andExpect(status().isNotFound)
    }
}
