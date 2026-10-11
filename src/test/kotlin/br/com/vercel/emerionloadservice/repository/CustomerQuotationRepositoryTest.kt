package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.utils.FirebirdIntegrationTest
import br.com.vercel.emerionloadservice.utils.TestDataBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Testcontainers
@SpringBootTest
class CustomerQuotationRepositoryTest : FirebirdIntegrationTest() {
    @Autowired
    private lateinit var customerQuotationRepository: CustomerQuotationRepository

    @Autowired
    private lateinit var testData: TestDataBuilder

    @BeforeEach
    fun clearData() {
        testData.clearRepositoryData()
    }

    @Test
    fun `findAllPaged orders quotations by business date and maps identities`() {
        testData.insertCustomer(codCli = 1, cpfCnpj = "11111111000111")
        testData.insertRegimeTributario(codigo = 1, nome = "Lucro Presumido")
        testData.insertQuotation(1, LocalDate.of(2024, 1, 1), 1001, 1)
        testData.insertQuotation(1, LocalDate.of(2024, 1, 2), 1002, 1, vendedorExternalId = null)

        val result = customerQuotationRepository.findAllPaged(PageRequest.of(0, 1))
        val quotation = result.content.single()

        assertEquals(2, result.totalElements)
        assertEquals("1002", quotation.numeroPedido)
        assertEquals(1, quotation.codigoCliente)
        assertEquals("11111111000111", quotation.cpfCnpj)
        assertEquals(LocalDate.of(2024, 1, 2), quotation.dataPedido)
        assertEquals("Lucro Presumido", quotation.nomeRegimeTributario)
        assertNull(quotation.vendedorExternalId)
    }

    @Test
    fun `findByKey returns null when no quotation matches`() {
        assertNull(customerQuotationRepository.findByKey("999"))
    }
}
