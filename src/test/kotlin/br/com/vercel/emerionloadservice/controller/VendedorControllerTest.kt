package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.VendedorService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.data.domain.Page
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Disabled
@WebMvcTest(VendedorController::class)
class VendedorControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    val vendedorServiceMock: VendedorService = mock(VendedorService::class.java)

    @MockitoBean
    val companyProviderMock: CompanyProvider = mock(CompanyProvider::class.java)

    @BeforeEach
    fun setup() {
        whenever(companyProviderMock.getCompanyCnpj()).thenReturn("12345678901234")
        whenever(vendedorServiceMock.getAllVendedores(any())).thenReturn(Page.empty())
    }

    @Test
    fun getAllVendedores() {
        mockMvc
            .perform(get("/vendedor/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content").isEmpty)
        verify(vendedorServiceMock).getAllVendedores(any())
    }

    @Test
    fun getVendedorByCodVen() {
    }

    @Test
    fun sendVendedorToIngestion() {
    }
}
