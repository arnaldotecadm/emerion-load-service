package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.model.Pedlib
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.PedlibService
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

@WebMvcTest(PedlibController::class)
class PedlibControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    val pedlibServiceMock: PedlibService = mock()

    @MockitoBean
    val companyProviderMock: CompanyProvider = mock()

    @BeforeEach
    fun setup() {
        whenever(companyProviderMock.getCompanyCnpj()).thenReturn("12345678901234")
        whenever(pedlibServiceMock.getAllPedlib(any())).thenReturn(Page.empty())
    }

    @Test
    fun `should return an empty pedlib page`() {
        mockMvc.perform(get("/pedlib/all")).andExpect(status().isOk).andExpect(jsonPath("$.content").isEmpty)
        verify(pedlibServiceMock).getAllPedlib(any())
    }

    @Test
    fun `should return a populated pedlib page with the retailer identity`() {
        val pedlib = mock<Pedlib>()
        whenever(pedlib.codigoEmpresa).thenReturn(1)
        whenever(pedlib.numeroPedido).thenReturn("123")
        whenever(pedlib.numeroLiberacao).thenReturn(1)
        whenever(pedlib.detalhes).thenReturn(listOf(mock()))
        whenever(pedlibServiceMock.getAllPedlib(any())).thenReturn(PageImpl(listOf(pedlib)))

        mockMvc
            .perform(get("/pedlib/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].numeroPedido").value("123"))
            .andExpect(jsonPath("$.content[0].cnpjEmpresa").value("12345678901234"))
    }

    @Test
    fun `should send pedlib to ingestion`() {
        mockMvc.perform(post("/pedlib/123/send")).andExpect(status().isOk)
        verify(pedlibServiceMock).sendPedlibToIngestion("123")
    }

    @Test
    fun `should return not found when pedlib does not exist`() {
        whenever(pedlibServiceMock.getPedlibByKey("missing")).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))
        mockMvc.perform(get("/pedlib/missing")).andExpect(status().isNotFound)
    }
}
