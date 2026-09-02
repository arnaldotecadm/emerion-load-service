package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.model.Ipi
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.IpiService
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

@WebMvcTest(IpiController::class)
class IpiControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    val ipiServiceMock: IpiService = mock()

    @MockitoBean
    val companyProviderMock: CompanyProvider = mock()

    @BeforeEach
    fun setup() {
        whenever(companyProviderMock.getCompanyCnpj()).thenReturn("12345678901234")
        whenever(ipiServiceMock.getAllIpi(any())).thenReturn(Page.empty())
    }

    @Test
    fun `should return an empty ipi page`() {
        mockMvc.perform(get("/ipi/all")).andExpect(status().isOk).andExpect(jsonPath("$.content").isEmpty)
        verify(ipiServiceMock).getAllIpi(any())
    }

    @Test
    fun `should return a populated ipi page with the retailer identity`() {
        val ipi = mock<Ipi>()
        whenever(ipi.codigoIpi).thenReturn("001")
        whenever(ipi.tipoIpi).thenReturn("N")
        whenever(ipiServiceMock.getAllIpi(any())).thenReturn(PageImpl(listOf(ipi)))

        mockMvc
            .perform(get("/ipi/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].codigoIpi").value("001"))
            .andExpect(jsonPath("$.content[0].cnpjEmpresa").value("12345678901234"))
    }

    @Test
    fun `should send ipi to ingestion`() {
        mockMvc.perform(post("/ipi/001/N/send")).andExpect(status().isOk)
        verify(ipiServiceMock).sendIpiToIngestion("001", "N")
    }

    @Test
    fun `should return not found when ipi does not exist`() {
        whenever(ipiServiceMock.getIpiByKey("missing", "N")).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))
        mockMvc.perform(get("/ipi/missing/N")).andExpect(status().isNotFound)
    }
}
