package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.utils.FirebirdIntegrationTest
import br.com.vercel.emerionloadservice.utils.TestDataBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals

@Testcontainers
@SpringBootTest
class CustomerCreditRepositoryTest : FirebirdIntegrationTest() {
    @Autowired
    private lateinit var customerCreditRepository: CustomerCreditRepository

    @Autowired
    private lateinit var testData: TestDataBuilder

    @BeforeEach
    fun clearData() {
        testData.clearRepositoryData()
    }

    @Test
    fun `getCreditsByCodCli filters by customer and maps native query fields in sequence order`() {
        testData.insertCreditMovement(codCli = 2, sequencia = "01")
        testData.insertCreditMovement(codCli = 1, sequencia = "02")
        testData.insertCreditMovement(codCli = 1, sequencia = "01")

        val credits = customerCreditRepository.getCreditsByCodCli(1)

        assertEquals(listOf("01", "02"), credits.map { it.sequencia })
        assertEquals(listOf(1L, 1L), credits.map { it.codCli })
        assertEquals(100.0, credits.first().valorTotal)
        assertEquals(25.0, credits.first().valorUtilizado)
        assertEquals("ABERTO", credits.first().situacao)
    }

    @Test
    fun `getCreditsByCodCli returns an empty list when the customer has no movements`() {
        testData.insertCreditMovement(codCli = 1, sequencia = "01")

        assertEquals(emptyList(), customerCreditRepository.getCreditsByCodCli(99))
    }
}
