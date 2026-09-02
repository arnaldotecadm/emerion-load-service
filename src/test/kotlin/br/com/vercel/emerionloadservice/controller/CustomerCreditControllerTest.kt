package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.model.CustomerCredit
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.CustomerCreditService
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
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@WebMvcTest(CustomerCreditController::class)
class CustomerCreditControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    val customerCreditServiceMock: CustomerCreditService = mock()

    @MockitoBean
    val companyProviderMock: CompanyProvider = mock()

    @BeforeEach
    fun setup() {
        whenever(companyProviderMock.getCompanyCnpj()).thenReturn("12345678901234")
        whenever(customerCreditServiceMock.getAllCredits(any())).thenReturn(Page.empty())
        whenever(customerCreditServiceMock.getCreditsByCodCli(404L)).thenReturn(emptyList())
    }

    @Test
    fun `should return an empty customer credit page`() {
        mockMvc.perform(get("/customer-credit/all")).andExpect(status().isOk).andExpect(jsonPath("$.content").isEmpty)
        verify(customerCreditServiceMock).getAllCredits(any())
    }

    @Test
    fun `should return a populated customer credit page with the retailer identity`() {
        val credit =
            CustomerCredit(
                codCli = 42L,
                sequencia = "1",
                data = Instant.parse("2026-01-02T00:00:00Z"),
                dataPedido = null,
                valorUtilizado = 0.0,
                valorTotal = 100.0,
                saldo = 100.0,
                situacao = "ABERTO",
            )
        whenever(customerCreditServiceMock.getAllCredits(any())).thenReturn(PageImpl(listOf(credit)))

        mockMvc
            .perform(get("/customer-credit/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].customerExternalId").value(42))
            .andExpect(jsonPath("$.content[0].cnpjEmpresa").value("12345678901234"))
    }

    @Test
    fun `should send customer credits to ingestion`() {
        mockMvc.perform(post("/customer-credit/42/send")).andExpect(status().isOk)
        verify(customerCreditServiceMock).sendCreditsToIngestion(42L)
    }

    @Test
    fun `should return an empty list when a customer has no credits`() {
        mockMvc.perform(get("/customer-credit/404")).andExpect(status().isOk).andExpect(jsonPath("$").isEmpty)
        verify(customerCreditServiceMock).getCreditsByCodCli(404L)
    }
}
