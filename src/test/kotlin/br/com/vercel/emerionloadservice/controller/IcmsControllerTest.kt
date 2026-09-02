package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.IcmsService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.server.ResponseStatusException

@WebMvcTest(IcmsController::class)
class IcmsControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    val icmsServiceMock: IcmsService = mock()

    @MockitoBean
    val companyProviderMock: CompanyProvider = mock()

    @BeforeEach
    fun setup() {
        whenever(companyProviderMock.getCompanyCnpj()).thenReturn("12345678901234")
        whenever(icmsServiceMock.getAllIcms(any())).thenReturn(Page.empty())
    }

    @Test
    fun `should return an empty icms page`() {
        mockMvc
            .perform(get("/icms/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isEmpty)

        verify(icmsServiceMock).getAllIcms(any())
    }

    @Test
    fun `should send icms to ingestion`() {
        mockMvc
            .perform(post("/icms/001/N/send"))
            .andExpect(status().isOk)

        verify(icmsServiceMock).sendIcmsToIngestion("001", "N")
    }

    @Test
    fun `should return not found when icms does not exist`() {
        whenever(icmsServiceMock.getIcmsByKey("missing", "N")).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mockMvc
            .perform(get("/icms/missing/N"))
            .andExpect(status().isNotFound)
    }
}
