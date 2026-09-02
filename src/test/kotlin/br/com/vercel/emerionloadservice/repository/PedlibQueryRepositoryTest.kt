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
class PedlibQueryRepositoryTest : FirebirdIntegrationTest() {
    @Autowired
    private lateinit var pedlibQueryRepository: PedlibQueryRepository

    @Autowired
    private lateinit var testData: TestDataBuilder

    @BeforeEach
    fun clearData() {
        testData.clearRepositoryData()
    }

    @Test
    fun `findAllPaged sorts releases by order and joins details to their release key`() {
        testData.insertPedlib(1, LocalDate.of(2024, 1, 1), 1001, 1, 1)
        testData.insertPedlib(1, LocalDate.of(2024, 1, 1), 1002, 1, 1)
        testData.insertPedlibDetail(1, LocalDate.of(2024, 1, 1), 1002, 1, 1)

        val result = pedlibQueryRepository.findAllPaged(PageRequest.of(0, 1))
        val release = result.content.single()

        assertEquals(2, result.totalElements)
        assertEquals("1002", release.numeroPedido)
        assertEquals(listOf(1), release.detalhes.map { it.numeroSequenciaLiberacao })
    }

    @Test
    fun `findByKey maps release header and item monetary values`() {
        val orderDate = LocalDate.of(2024, 3, 4)
        testData.insertPedlib(1, orderDate, 1001, 2, 1)
        testData.insertPedlibDetail(1, orderDate, 1001, 2, 3)

        val release = pedlibQueryRepository.findByKey("1001")

        requireNotNull(release)
        assertEquals(orderDate, release.dataPedido)
        assertEquals(orderDate.plusDays(1), release.dataLiberacao)
        assertEquals("10:30:00", release.horaLiberacao)
        assertEquals(120.0, release.totalLiberadoComImpostos)
        assertEquals(4.5, release.comissaoLiberacao)
        release.detalhes.single().let { detail ->
            assertEquals(3, detail.numeroSequenciaLiberacao)
            assertEquals("Detalhe 3", detail.descricaoItemLiberacao)
            assertEquals(3.0, detail.quantidadeNoPedido)
            assertEquals(120.0, detail.totalValorBruto)
            assertEquals(35.0, detail.custoPraticado)
        }
    }

    @Test
    fun `findByKey returns null and an out of range page is empty when no release matches`() {
        assertNull(pedlibQueryRepository.findByKey("999"))
        assertEquals(emptyList(), pedlibQueryRepository.findAllPaged(PageRequest.of(1, 10)).content)
    }
}
