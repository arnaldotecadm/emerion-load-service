package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.utils.FirebirdIntegrationTest
import br.com.vercel.emerionloadservice.utils.TestDataBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Testcontainers
@SpringBootTest
class IpiQueryRepositoryTest : FirebirdIntegrationTest() {
    @Autowired
    private lateinit var ipiQueryRepository: IpiQueryRepository

    @Autowired
    private lateinit var testData: TestDataBuilder

    @BeforeEach
    fun clearData() {
        testData.clearRepositoryData()
    }

    @Test
    fun `findAllPaged uses the IPI composite key for deterministic pagination`() {
        testData.insertIpiReferences("A", "B")
        testData.insertIpi("020", "B")
        testData.insertIpi("010", "B")
        testData.insertIpi("010", "A")

        val result = ipiQueryRepository.findAllPaged(PageRequest.of(0, 2))

        assertEquals(3, result.totalElements)
        assertEquals(listOf("010" to "A", "010" to "B"), result.content.map { it.codigoIpi to it.tipoIpi })
    }

    @Test
    fun `findByKey maps IPI and joined PIS and COFINS descriptions`() {
        testData.insertIpiReferences("A")
        testData.insertIpi("010", "A")

        val ipi = ipiQueryRepository.findByKey("010", "A")

        requireNotNull(ipi)
        assertEquals("S", ipi.flgAtivo)
        assertEquals("IPI 010", ipi.nomeIpi)
        assertEquals("1234.56.78", ipi.ncmIpi)
        assertEquals("IPI tributado", ipi.descricaoSituacaoTributariaIpi)
        assertEquals(5.0, ipi.aliquotaIpi)
        assertEquals("PIS tributado", ipi.descricaoSituacaoTributariaPis)
        assertEquals(1.65, ipi.aliquotaPis)
        assertEquals("COFINS tributado", ipi.descricaoSituacaoTributariaCofins)
        assertEquals(7.6, ipi.aliquotaCofins)
    }

    @Test
    fun `findByKey returns null when the IPI composite key is absent`() {
        assertNull(ipiQueryRepository.findByKey("missing", "A"))
    }
}
