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
class IcmsQueryRepositoryTest : FirebirdIntegrationTest() {
    @Autowired
    private lateinit var icmsQueryRepository: IcmsQueryRepository

    @Autowired
    private lateinit var testData: TestDataBuilder

    @BeforeEach
    fun clearData() {
        testData.clearRepositoryData()
    }

    @Test
    fun `findAllPaged uses the ICMS composite key for deterministic pagination`() {
        testData.insertIcms("020", "B")
        testData.insertIcms("010", "B")
        testData.insertIcms("010", "A")

        val result = icmsQueryRepository.findAllPaged(PageRequest.of(0, 2))

        assertEquals(3, result.totalElements)
        assertEquals(listOf("010" to "A", "010" to "B"), result.content.map { it.codigoIcms to it.tipoIcms })
    }

    @Test
    fun `findByKey maps ICMS rates and tax classification`() {
        testData.insertIcms("010", "A")

        val icms = icmsQueryRepository.findByKey("010", "A")

        requireNotNull(icms)
        assertEquals("ICMS 010", icms.nomeIcms)
        assertEquals("SP", icms.ufEmitente)
        assertEquals(18.0, icms.aliquotaIcms)
        assertEquals(10.0, icms.percentualReducaoValorImposto)
        assertEquals(90.0, icms.percentualBaseCalculoIcms)
        assertEquals("102", icms.situacaoTributariaIcms)
    }

    @Test
    fun `findByKey returns null when the composite key is absent`() {
        assertNull(icmsQueryRepository.findByKey("missing", "A"))
    }
}
