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

@Testcontainers
@SpringBootTest
class CustomerCreditQueryRepositoryTest : FirebirdIntegrationTest() {
    @Autowired
    private lateinit var customerCreditQueryRepository: CustomerCreditQueryRepository

    @Autowired
    private lateinit var testData: TestDataBuilder

    @BeforeEach
    fun clearData() {
        testData.clearRepositoryData()
    }

    @Test
    fun `findAllPaged orders movements by customer and sequence and maps outgoing credits`() {
        testData.insertCreditMovement(codCli = 2, sequencia = "02")
        testData.insertCreditMovement(codCli = 1, sequencia = "02", valorUtilizado = BigDecimal.ZERO)
        testData.insertCreditMovement(codCli = 1, sequencia = "01")

        val result = customerCreditQueryRepository.findAllPaged(PageRequest.of(0, 2))

        assertEquals(3, result.totalElements)
        assertEquals(listOf(1L to "01", 1L to "02"), result.content.map { it.codCli to it.sequencia })
        result.content.first().let { credit ->
            assertEquals(100.0, credit.valorTotal)
            assertEquals(25.0, credit.valorUtilizado)
            assertEquals(75.0, credit.saldo)
            assertEquals("ABERTO", credit.situacao)
            assertEquals("SAIDA", credit.tipo)
        }
        assertEquals("ENTRADA", result.content.last().tipo)
    }

    @Test
    fun `findAllPaged returns an empty page when there are no credit movements`() {
        val result = customerCreditQueryRepository.findAllPaged(PageRequest.of(0, 10))

        assertEquals(0, result.totalElements)
        assertEquals(emptyList(), result.content)
    }
}
