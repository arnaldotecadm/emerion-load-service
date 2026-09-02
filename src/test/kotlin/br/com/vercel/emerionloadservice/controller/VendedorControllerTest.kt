package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.model.Vendedor
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.VendedorService
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
    fun `should return an empty vendedor page`() {
        mockMvc
            .perform(get("/vendedor/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content").isEmpty)
        verify(vendedorServiceMock).getAllVendedores(any())
    }

    @Test
    fun `should return a populated vendedor page with the retailer identity`() {
        val vendedor = mock<Vendedor>()
        whenever(vendedor.id).thenReturn(42L)
        whenever(vendedor.nome).thenReturn("Vendedor")
        whenever(vendedorServiceMock.getAllVendedores(any())).thenReturn(PageImpl(listOf(vendedor)))

        mockMvc
            .perform(get("/vendedor/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].externalId").value(42))
            .andExpect(jsonPath("$.content[0].cnpjEmpresa").value("12345678901234"))
    }

    @Test
    fun `should send a vendedor to ingestion`() {
        mockMvc.perform(post("/vendedor/42/send")).andExpect(status().isOk)
        verify(vendedorServiceMock).sendVendedorToIngestion(42L)
    }

    @Test
    fun `should return not found when vendedor does not exist`() {
        whenever(vendedorServiceMock.getVendedorByCodVen(404L)).thenThrow(
            org.springframework.web.server
                .ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND),
        )

        mockMvc.perform(get("/vendedor/404")).andExpect(status().isNotFound)
    }
}
