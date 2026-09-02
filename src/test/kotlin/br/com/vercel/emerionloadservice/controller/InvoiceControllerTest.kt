package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.model.Invoice
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.InvoiceService
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

@WebMvcTest(InvoiceController::class)
class InvoiceControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    val invoiceServiceMock: InvoiceService = mock()

    @MockitoBean
    val companyProviderMock: CompanyProvider = mock()

    @BeforeEach
    fun setup() {
        whenever(companyProviderMock.getCompanyCnpj()).thenReturn("12345678901234")
        whenever(invoiceServiceMock.getAllInvoices(any())).thenReturn(Page.empty())
        whenever(invoiceServiceMock.getInvoicesByOrder(1, LocalDate.of(2026, 1, 2), "missing")).thenReturn(emptyList())
    }

    @Test
    fun `should return an empty invoice page`() {
        mockMvc.perform(get("/invoice/all")).andExpect(status().isOk).andExpect(jsonPath("$.content").isEmpty)
        verify(invoiceServiceMock).getAllInvoices(any())
    }

    @Test
    fun `should return a populated invoice page with customer and retailer identities`() {
        val invoice = mock<Invoice>()
        whenever(invoice.codEmp).thenReturn(1)
        whenever(invoice.codCli).thenReturn(42L)
        whenever(invoice.numres).thenReturn("ORDER-42")
        whenever(invoice.dteres).thenReturn(LocalDate.of(2026, 1, 2))
        whenever(invoiceServiceMock.getAllInvoices(any())).thenReturn(PageImpl(listOf(invoice)))

        mockMvc
            .perform(get("/invoice/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].customerExternalId").value(42))
            .andExpect(jsonPath("$.content[0].cnpjEmpresa").value("12345678901234"))
    }

    @Test
    fun `should send invoices to ingestion`() {
        mockMvc.perform(post("/invoice/1/2026-01-02/ORDER-42/send")).andExpect(status().isOk)
        verify(invoiceServiceMock).sendInvoicesToIngestion(1, LocalDate.of(2026, 1, 2), "ORDER-42")
    }

    @Test
    fun `should return an empty list when an order has no invoices`() {
        mockMvc.perform(get("/invoice/1/2026-01-02/missing")).andExpect(status().isOk).andExpect(jsonPath("$").isEmpty)
        verify(invoiceServiceMock).getInvoicesByOrder(1, LocalDate.of(2026, 1, 2), "missing")
    }
}
