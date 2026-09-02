package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.FinCreService
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

@WebMvcTest(FinCreController::class)
class FinCreControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    val finCreServiceMock: FinCreService = mock()

    @MockitoBean
    val companyProviderMock: CompanyProvider = mock()

    @BeforeEach
    fun setup() {
        whenever(companyProviderMock.getCompanyCnpj()).thenReturn("12345678901234")
        whenever(finCreServiceMock.getAllFinCre(any())).thenReturn(Page.empty())
    }

    @Test
    fun `should return an empty fincre page`() {
        mockMvc
            .perform(get("/fincre/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isEmpty)

        verify(finCreServiceMock).getAllFinCre(any())
    }

    @Test
    fun `should send fincre to ingestion`() {
        mockMvc
            .perform(post("/fincre/123/send"))
            .andExpect(status().isOk)

        verify(finCreServiceMock).sendFinCreToIngestion("123")
    }

    @Test
    fun `should return not found when fincre does not exist`() {
        whenever(finCreServiceMock.getFinCreByKey("missing")).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mockMvc
            .perform(get("/fincre/missing"))
            .andExpect(status().isNotFound)
    }
}
