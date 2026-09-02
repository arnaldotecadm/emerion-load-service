package br.com.vercel.emerionloadservice.controller

import br.com.vercel.emerionloadservice.model.Product
import br.com.vercel.emerionloadservice.service.CompanyProvider
import br.com.vercel.emerionloadservice.service.ProductService
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

@WebMvcTest(ProductController::class)
class ProductControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    val productServiceMock: ProductService = mock()

    @MockitoBean
    val companyProviderMock: CompanyProvider = mock()

    @BeforeEach
    fun setup() {
        whenever(companyProviderMock.getCompanyCnpj()).thenReturn("12345678901234")
        whenever(productServiceMock.getAllProducts(any())).thenReturn(Page.empty())
    }

    @Test
    fun `should return an empty product page`() {
        mockMvc.perform(get("/product/all")).andExpect(status().isOk).andExpect(jsonPath("$.content").isEmpty)
        verify(productServiceMock).getAllProducts(any())
    }

    @Test
    fun `should return a populated product page with the retailer identity`() {
        val product = mock<Product>()
        whenever(product.codGru).thenReturn("001")
        whenever(product.codSub).thenReturn("0002")
        whenever(product.codPro).thenReturn("00003")
        whenever(product.nome).thenReturn("Produto")
        whenever(productServiceMock.getAllProducts(any())).thenReturn(PageImpl(listOf(product)))

        mockMvc
            .perform(get("/product/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].externalId").value("001.0002.00003"))
            .andExpect(jsonPath("$.content[0].cnpjEmpresa").value("12345678901234"))
    }

    @Test
    fun `should send a product to ingestion`() {
        mockMvc.perform(post("/product/001.0002.00003/send")).andExpect(status().isOk)
        verify(productServiceMock).sendProductToIngestion("001.0002.00003")
    }

    @Test
    fun `should return bad request for an invalid product id`() {
        whenever(productServiceMock.getProductById("invalid")).thenThrow(ResponseStatusException(HttpStatus.BAD_REQUEST))
        mockMvc.perform(get("/product/invalid")).andExpect(status().isBadRequest)
    }
}
