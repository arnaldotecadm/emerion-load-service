package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.utils.FirebirdIntegrationTest
import br.com.vercel.emerionloadservice.utils.TestDataBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Testcontainers
@SpringBootTest
class ProductQueryRepositoryTest : FirebirdIntegrationTest() {
    @Autowired
    private lateinit var productQueryRepository: ProductQueryRepository

    @Autowired
    private lateinit var testData: TestDataBuilder

    @BeforeEach
    fun clearData() {
        testData.clearRepositoryData()
    }

    @Test
    fun `findAllPaged orders products by composite identifier`() {
        testData.insertCompany()
        testData.insertProduct(1, 2, 1, 1)
        testData.insertProduct(1, 1, 2, 1)
        testData.insertProduct(1, 1, 1, 2)

        val result = productQueryRepository.findAllPaged(PageRequest.of(0, 2))

        assertEquals(3, result.totalElements)
        assertEquals(listOf("1.1.2", "1.2.1"), result.content.map { it.id })
    }

    @Test
    fun `getProductByCodGruCodSubCodPro maps price stock and fiscal fields`() {
        testData.insertCompany()
        testData.insertProduct(1, 10, 20, 30, nome = "Produto Mapeado")

        val product = productQueryRepository.getProductByCodGruCodSubCodPro("10", "20", "30")

        requireNotNull(product)
        assertEquals("Produto Mapeado", product.nome)
        assertEquals("REF-30", product.referenciaInterna)
        assertEquals(true, product.descontinuado)
        assertEquals(BigDecimal("10.0000"), product.preco)
        assertEquals(BigDecimal("8.0000"), product.estoqueDisponivel)
        assertEquals(BigDecimal("2.0000"), product.estoqueRMA)
        assertEquals("000001", product.ibsCClassTrib)
        assertEquals("Produto de teste", product.observacao)
    }

    @Test
    fun `getProductByCodGruCodSubCodPro returns null when the product does not exist`() {
        testData.insertCompany()

        assertNull(productQueryRepository.getProductByCodGruCodSubCodPro("1", "2", "3"))
    }
}
