package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.model.Receivable
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.ReceivableService
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

@WebMvcTest(ReceivableController::class)
class ReceivableControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    val receivableServiceMock: ReceivableService = mock()

    @MockitoBean
    val companyProviderMock: CompanyProvider = mock()

    @BeforeEach
    fun setup() {
        whenever(companyProviderMock.getCompanyCnpj()).thenReturn("12345678901234")
        whenever(receivableServiceMock.getAllReceivables(any())).thenReturn(Page.empty())
        whenever(receivableServiceMock.getReceivablesByCodCli(404L)).thenReturn(emptyList())
    }

    @Test
    fun `should return an empty receivable page`() {
        mockMvc.perform(get("/receivable/all")).andExpect(status().isOk).andExpect(jsonPath("$.content").isEmpty)
        verify(receivableServiceMock).getAllReceivables(any())
    }

    @Test
    fun `should return a populated receivable page with customer and retailer identities`() {
        val receivable = mock<Receivable>()
        whenever(receivable.codCli).thenReturn(42L)
        whenever(receivable.dataLancamento).thenReturn(Instant.parse("2026-01-02T00:00:00Z"))
        whenever(receivable.valorOriginal).thenReturn(100.0)
        whenever(receivable.valorUtilizado).thenReturn(20.0)
        whenever(receivable.saldoAberto).thenReturn(80.0)
        whenever(receivableServiceMock.getAllReceivables(any())).thenReturn(PageImpl(listOf(receivable)))

        mockMvc
            .perform(get("/receivable/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].customerExternalId").value(42))
            .andExpect(jsonPath("$.content[0].cnpjEmpresa").value("12345678901234"))
    }

    @Test
    fun `should send customer receivables to ingestion`() {
        mockMvc.perform(post("/receivable/42/send")).andExpect(status().isOk)
        verify(receivableServiceMock).sendReceivablesToIngestion(42L)
    }

    @Test
    fun `should return an empty list when a customer has no receivables`() {
        mockMvc.perform(get("/receivable/404")).andExpect(status().isOk).andExpect(jsonPath("$").isEmpty)
        verify(receivableServiceMock).getReceivablesByCodCli(404L)
    }
}
