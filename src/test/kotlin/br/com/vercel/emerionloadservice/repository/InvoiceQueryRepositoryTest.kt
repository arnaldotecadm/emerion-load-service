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

@Testcontainers
@SpringBootTest
class InvoiceQueryRepositoryTest : FirebirdIntegrationTest() {
    @Autowired
    private lateinit var invoiceQueryRepository: InvoiceQueryRepository

    @Autowired
    private lateinit var testData: TestDataBuilder

    @BeforeEach
    fun clearData() {
        testData.clearRepositoryData()
    }

    @Test
    fun `findAllPaged returns invoices in stable company order and includes matching customer`() {
        testData.insertCustomer(codCli = 1)
        testData.insertOrder(1, LocalDate.of(2024, 1, 2), 1002, 1)
        testData.insertOrder(1, LocalDate.of(2024, 1, 1), 1001, 1)
        testData.insertInvoice(1, LocalDate.of(2024, 1, 2), 1002, "NF-2")
        testData.insertInvoice(1, LocalDate.of(2024, 1, 1), 1001, "NF-1")

        val result = invoiceQueryRepository.findAllPaged(PageRequest.of(0, 1))

        assertEquals(2, result.totalElements)
        assertEquals("1001", result.content.single().numres)
        assertEquals(1, result.content.single().codCli)
    }

    @Test
    fun `findByOrder filters the order and maps invoice date and total values`() {
        val orderDate = LocalDate.of(2024, 4, 5)
        testData.insertCustomer(codCli = 1)
        testData.insertOrder(1, orderDate, 1001, 1)
        testData.insertInvoice(1, orderDate, 1001, "NF-2")
        testData.insertInvoice(1, orderDate, 1001, "NF-1")
        testData.insertOrder(1, orderDate, 1002, 1)
        testData.insertInvoice(1, orderDate, 1002, "NF-OTHER")

        val invoices = invoiceQueryRepository.findByOrder(1, orderDate, "1001")

        assertEquals(listOf("NF-1", "NF-2"), invoices.map { it.nronfs })
        invoices.first().let { invoice ->
            assertEquals(orderDate, invoice.dteres.toLocalDate())
            assertEquals(orderDate.plusDays(1), invoice.dataFaturamento?.toLocalDate())
            assertEquals(120.5, invoice.totalFaturado)
        }
    }

    @Test
    fun `findByOrder returns no invoices for an unknown order`() {
        assertEquals(emptyList(), invoiceQueryRepository.findByOrder(1, LocalDate.of(2024, 1, 1), "999"))
    }
}
