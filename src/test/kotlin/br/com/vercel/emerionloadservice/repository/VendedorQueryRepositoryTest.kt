package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.service.VendedorService
import br.com.vercel.emerionloadservice.utils.FirebirdIntegrationTest
import br.com.vercel.emerionloadservice.utils.TestDataBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals

@Disabled
@Testcontainers
@SpringBootTest
class VendedorQueryRepositoryTest : FirebirdIntegrationTest() {
    @Autowired
    private lateinit var vendedorService: VendedorService

    @Autowired
    lateinit var testData: TestDataBuilder

    @BeforeEach
    fun tearDown() {
        testData.clearVendedores()
    }

    @Test
    fun findAllPaged() {
        for (i in 1..15) {
            testData.insertVendedor(codVen = 100L + i, nome = "Vendedor $i")
        }
        val pageable = PageRequest.of(0, 10)
        val result = vendedorService.getAllVendedores(pageable)

        assertEquals(15, result.totalElements)
        assertEquals(2, result.totalPages)
        assertEquals(10, result.numberOfElements)
    }

    @Test
    fun findByCodVen() {
        testData.insertVendedor(codVen = 1, nome = "Arnaldo")
        vendedorService.getVendedorByCodVen(1).let {
            assertEquals(1, it.id)
            assertEquals("Arnaldo", it.nome)
        }
    }
}
