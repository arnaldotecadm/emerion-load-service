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
class ReceivableQueryRepositoryTest : FirebirdIntegrationTest() {
    @Autowired
    private lateinit var receivableQueryRepository: ReceivableQueryRepository

    @Autowired
    private lateinit var testData: TestDataBuilder

    @BeforeEach
    fun clearData() {
        testData.clearRepositoryData()
    }

    @Test
    fun `findAllPaged orders receivables by customer and sequence`() {
        testData.insertCreditMovement(codCli = 2, sequencia = "01")
        testData.insertCreditMovement(codCli = 1, sequencia = "02")
        testData.insertCreditMovement(codCli = 1, sequencia = "01")

        val result = receivableQueryRepository.findAllPaged(PageRequest.of(0, 2))

        assertEquals(3, result.totalElements)
        assertEquals(listOf(1L to "01", 1L to "02"), result.content.map { it.codCli to it.sequencia })
    }

    @Test
    fun `findByCodCli filters records and maps receivable values`() {
        testData.insertCreditMovement(codCli = 2, sequencia = "01")
        testData.insertCreditMovement(codCli = 1, sequencia = "02", valorUtilizado = BigDecimal.ZERO)
        testData.insertCreditMovement(codCli = 1, sequencia = "01")

        val receivables = receivableQueryRepository.findByCodCli(1)

        assertEquals(listOf("01", "02"), receivables.map { it.sequencia })
        receivables.first().let { receivable ->
            assertEquals(100.0, receivable.valorOriginal)
            assertEquals(25.0, receivable.valorUtilizado)
            assertEquals(75.0, receivable.saldoAberto)
            assertEquals("ABERTO", receivable.situacao)
            assertEquals(2024, receivable.dataLancamento.atZone(java.time.ZoneOffset.UTC).year)
        }
    }

    @Test
    fun `findByCodCli and an empty database page return no receivables`() {
        assertEquals(emptyList(), receivableQueryRepository.findByCodCli(99))
        assertEquals(emptyList(), receivableQueryRepository.findAllPaged(PageRequest.of(0, 10)).content)
    }
}
