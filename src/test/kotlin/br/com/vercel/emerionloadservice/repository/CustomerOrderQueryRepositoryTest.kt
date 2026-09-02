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
class CustomerOrderQueryRepositoryTest : FirebirdIntegrationTest() {
    @Autowired
    private lateinit var customerOrderQueryRepository: CustomerOrderQueryRepository

    @Autowired
    private lateinit var testData: TestDataBuilder

    @BeforeEach
    fun clearData() {
        testData.clearRepositoryData()
    }

    @Test
    fun `findAllPaged orders headers by business date and attaches only their items`() {
        testData.insertCustomer(codCli = 1, cpfCnpj = "11111111000111")
        testData.insertProduct(codClp = 1, codGru = 10, codSub = 20, codPro = 30)
        testData.insertOrder(1, LocalDate.of(2024, 1, 1), 1001, 1)
        testData.insertOrder(1, LocalDate.of(2024, 1, 2), 1002, 1)
        testData.insertOrder(1, LocalDate.of(2024, 1, 2), 1003, 1)
        testData.insertOrderItem(1, LocalDate.of(2024, 1, 2), 1002, 1)

        val result = customerOrderQueryRepository.findAllPaged(PageRequest.of(0, 1))
        val order = result.content.single()

        assertEquals(3, result.totalElements)
        assertEquals(listOf("1002"), result.content.map { it.numeroPedido })
        assertEquals(1, order.itens.size)
        assertEquals(1, order.itens.single().seqRe2)
    }

    @Test
    fun `findByKey maps retailer customer tax totals and detailed item values`() {
        val orderDate = LocalDate.of(2024, 2, 10)
        testData.insertCustomer(codCli = 1, cpfCnpj = "11111111000111")
        testData.insertRegimeTributario(codigo = 1, nome = "Lucro Presumido")
        testData.insertProduct(codClp = 1, codGru = 10, codSub = 20, codPro = 30)
        testData.insertOrder(1, orderDate, 1001, 1, vendedorExternalId = null)
        testData.insertOrderItem(1, orderDate, 1001, 2)

        val order = customerOrderQueryRepository.findByKey("1001")

        requireNotNull(order)
        assertEquals(1, order.codigoEmpresa)
        assertEquals(1, order.codigoCliente)
        assertEquals("11111111000111", order.cpfCnpj)
        assertEquals(orderDate, order.dataPedido.toLocalDate())
        assertEquals("ABERTO", order.statusPedido)
        assertEquals(120.0, order.totalPedidoComImpostos)
        assertEquals(5.0, order.totalIpi)
        assertEquals(null, order.vendedorExternalId)
        assertEquals("Lucro Presumido", order.nomeRegimeTributario)
        order.itens.single().let { item ->
            assertEquals("10.20.30", item.produto)
            assertEquals("Item 2", item.descricao)
            assertEquals(2.0, item.quantidade)
            assertEquals(50.0, item.valorUnitario)
            assertEquals(120.0, item.totalItemTributado)
            assertEquals("REF-1", item.referencia)
        }
    }

    @Test
    fun `findByKey returns null and an out of range page is empty when no order matches`() {
        assertNull(customerOrderQueryRepository.findByKey("999"))
        assertEquals(emptyList(), customerOrderQueryRepository.findAllPaged(PageRequest.of(1, 1)).content)
    }
}
