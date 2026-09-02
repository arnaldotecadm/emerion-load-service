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
class FinCreQueryRepositoryTest : FirebirdIntegrationTest() {
    @Autowired
    private lateinit var finCreQueryRepository: FinCreQueryRepository

    @Autowired
    private lateinit var testData: TestDataBuilder

    @BeforeEach
    fun clearData() {
        testData.clearRepositoryData()
    }

    @Test
    fun `findAllPaged sorts credit headers descending and attaches only page installments`() {
        testData.insertCustomer(codCli = 1)
        testData.insertVendedor(codVen = 7, nome = "Vendedor")
        testData.insertFinCreReferences(1)
        testData.insertFinCre(1, LocalDate.of(2024, 1, 1), 100, 1, 7)
        testData.insertFinCre(1, LocalDate.of(2024, 1, 2), 200, 1, 7)
        testData.insertFinCreInstallment(1, LocalDate.of(2024, 1, 1), 100, 1)
        testData.insertFinCreInstallment(1, LocalDate.of(2024, 1, 2), 200, 1)

        val result = finCreQueryRepository.findAllPaged(PageRequest.of(0, 1))
        val credit = result.content.single()

        assertEquals(2, result.totalElements)
        assertEquals("200", credit.documento)
        assertEquals(listOf(1), credit.parcelas.map { it.numeroParcela })
    }

    @Test
    fun `findByKey maps credit header references and installment dates`() {
        val emissionDate = LocalDate.of(2024, 2, 3)
        testData.insertCustomer(codCli = 1, nome = "Cliente Financeiro")
        testData.insertVendedor(codVen = 7, nome = "Vendedor Financeiro")
        testData.insertFinCreReferences(1)
        testData.insertFinCre(1, emissionDate, 123, 1, 7)
        testData.insertFinCreInstallment(1, emissionDate, 123, 2)

        val credit = finCreQueryRepository.findByKey("123")

        requireNotNull(credit)
        assertEquals(1, credit.codigoEmpresa)
        assertEquals(emissionDate, credit.dataEmissao)
        assertEquals("30 dias", credit.nomeCondicaoRecebimento)
        assertEquals("Empresa 1", credit.nomeEmpresa?.trim())
        assertEquals("Cliente Financeiro", credit.nomeCliente)
        assertEquals("Vendedor Financeiro", credit.nomeVendedor)
        credit.parcelas.single().let { installment ->
            assertEquals(2, installment.numeroParcela)
            assertEquals(emissionDate.plusDays(30), installment.dataVencimento)
            assertEquals(150.0, installment.valorParcela)
            assertEquals("Banco Um", installment.nomeBanco)
        }
    }

    @Test
    fun `findByKey returns null and an out of range page is empty when no credit exists`() {
        assertNull(finCreQueryRepository.findByKey("999"))
        assertEquals(emptyList(), finCreQueryRepository.findAllPaged(PageRequest.of(1, 10)).content)
    }
}
