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
class CustomerQueryRepositoryTest : FirebirdIntegrationTest() {
    @Autowired
    private lateinit var customerQueryRepository: CustomerQueryRepository

    @Autowired
    private lateinit var testData: TestDataBuilder

    @BeforeEach
    fun clearData() {
        testData.clearRepositoryData()
    }

    @Test
    fun `findAllPaged returns customers in codCli order with accurate page metadata`() {
        testData.insertCustomer(codCli = 30)
        testData.insertCustomer(codCli = 10)
        testData.insertCustomer(codCli = 20)

        val result = customerQueryRepository.findAllPaged(PageRequest.of(1, 2))

        assertEquals(3, result.totalElements)
        assertEquals(2, result.totalPages)
        assertEquals(listOf(30L), result.content.map { it.id })
    }

    @Test
    fun `getCustomerByCodCli maps customer identity relationships dates and addresses`() {
        testData.insertVendedor(codVen = 7, nome = "Vendedora")
        testData.insertRegimeTributario(codigo = 1, nome = "Simples Nacional")
        testData.insertCustomer(
            codCli = 10,
            cpfCnpj = "12345678000199",
            codVen = 7,
            regimeTributario = 1,
            bloqueado = true,
        )

        val customer = customerQueryRepository.getCustomerByCodCli(10)

        requireNotNull(customer)
        assertEquals("Cliente 10", customer.razaoSocial)
        assertEquals("Fantasia 10", customer.nomeFantasia)
        assertEquals("12345678000199", customer.cpfCnpj)
        assertEquals("Simples Nacional", customer.regimeTributario)
        assertEquals(7, customer.vendedorExternalId)
        assertEquals("Vendedora", customer.nomeVendedor)
        assertEquals(LocalDate.of(1980, 1, 2), customer.dataNascimento)
        assertEquals(LocalDate.of(2020, 3, 4), customer.dataCadastro)
        assertEquals(LocalDate.of(2024, 5, 6), customer.dataUltimaAtualizacao)
        assertEquals("Faturamento 10", customer.faturamentoEndereco)
        assertEquals("Cobrança 10", customer.cobrancaEndereco)
        assertEquals("Entrega 10", customer.entregaEndereco)
        assertEquals(true, customer.bloqueado)
    }

    @Test
    fun `getCustomerByCodCli returns null when the customer does not exist`() {
        assertNull(customerQueryRepository.getCustomerByCodCli(999))
    }
}
