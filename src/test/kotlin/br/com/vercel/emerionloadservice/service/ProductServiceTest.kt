package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Product
import br.com.vercel.emerionloadservice.repository.ProductQueryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.server.ResponseStatusException

@ExtendWith(MockitoExtension::class)
class ProductServiceTest {
    @MockitoBean
    val productQueryRepositoryMock: ProductQueryRepository = mock()

    @MockitoBean
    val ingestionServiceClientMock: IngestionServiceClient = mock()

    @InjectMocks
    lateinit var productService: ProductService

    @Test
    fun `should return the product page provided by the repository`() {
        val page = PageImpl(listOf(mock<Product>()))
        whenever(productQueryRepositoryMock.findAllPaged(any())).thenReturn(page)

        assertSame(page, productService.getAllProducts(Pageable.ofSize(10)))
    }

    @Test
    fun `should normalize product id segments before querying`() {
        val product = mock<Product>()
        whenever(productQueryRepositoryMock.getProductByCodGruCodSubCodPro("001", "0002", "00003")).thenReturn(product)

        assertSame(product, productService.getProductById("1.2.3"))
    }

    @Test
    fun `should reject a product id without exactly three segments`() {
        val exception = assertThrows<ResponseStatusException> { productService.getProductById("1.2") }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
        verifyNoInteractions(productQueryRepositoryMock, ingestionServiceClientMock)
    }

    @Test
    fun `should throw not found without sending when product does not exist`() {
        whenever(productQueryRepositoryMock.getProductByCodGruCodSubCodPro("001", "0002", "00003")).thenReturn(null)

        val exception = assertThrows<ResponseStatusException> { productService.sendProductToIngestion("1.2.3") }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        verifyNoInteractions(ingestionServiceClientMock)
    }

    @Test
    fun `should send the resolved product to ingestion`() {
        val product = mock<Product>()
        whenever(productQueryRepositoryMock.getProductByCodGruCodSubCodPro("001", "0002", "00003")).thenReturn(product)

        productService.sendProductToIngestion("1.2.3")

        verify(ingestionServiceClientMock).sendProduct(product)
    }
}
