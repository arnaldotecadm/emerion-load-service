package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.model.CustomerQuotation
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.CustomerQuotationService
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
import java.time.LocalDate

@WebMvcTest(CustomerQuotationController::class)
class CustomerQuotationControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    val customerQuotationServiceMock: CustomerQuotationService = mock()

    @MockitoBean
    val companyProviderMock: CompanyProvider = mock()

    @BeforeEach
    fun setup() {
        whenever(companyProviderMock.getCompanyCnpj()).thenReturn("12345678901234")
        whenever(customerQuotationServiceMock.getAllQuotations(any())).thenReturn(Page.empty())
    }

    @Test
    fun `should expose retailer and customer identifiers`() {
        val quotation = mock<CustomerQuotation>()
        whenever(quotation.codigoEmpresa).thenReturn(1)
        whenever(quotation.codigoCliente).thenReturn(42L)
        whenever(quotation.numeroPedido).thenReturn("QUOTE-42")
        whenever(quotation.dataPedido).thenReturn(LocalDate.of(2026, 1, 2))
        whenever(quotation.totalPedidoComImpostos).thenReturn(100.0)
        whenever(quotation.totalPedidoSemImpostos).thenReturn(90.0)
        whenever(customerQuotationServiceMock.getAllQuotations(any())).thenReturn(PageImpl(listOf(quotation)))

        mockMvc
            .perform(get("/customer-quotation/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].customerExternalId").value(42))
            .andExpect(jsonPath("$.content[0].cnpjEmpresa").value("12345678901234"))
    }

    @Test
    fun `should send a quotation to ingestion`() {
        mockMvc
            .perform(post("/customer-quotation/QUOTE-42/send"))
            .andExpect(status().isOk)

        verify(customerQuotationServiceMock).sendQuotationToIngestion("QUOTE-42")
    }
}
