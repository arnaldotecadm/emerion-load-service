package br.com.vercel.emerionloadservice.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.jdbc.core.JdbcTemplate

class CompanyProviderTest {
    private val jdbcTemplateMock: JdbcTemplate = mock()

    @Test
    fun `should trim and cache the company cnpj`() {
        whenever(jdbcTemplateMock.queryForObject(any<String>(), eq(String::class.java), eq(7))).thenReturn(" 12345678901234 ")
        val companyProvider = CompanyProvider(jdbcTemplateMock, 7)

        assertEquals("12345678901234", companyProvider.getCompanyCnpj())
        assertEquals("12345678901234", companyProvider.getCompanyCnpj())

        verify(jdbcTemplateMock, times(1)).queryForObject(any<String>(), eq(String::class.java), eq(7))
    }

    @Test
    fun `should reject a missing company cnpj`() {
        whenever(jdbcTemplateMock.queryForObject(any<String>(), eq(String::class.java), eq(7))).thenReturn("   ")
        val companyProvider = CompanyProvider(jdbcTemplateMock, 7)

        val exception = assertThrows<IllegalArgumentException> { companyProvider.getCompanyCnpj() }

        assertEquals("Could not resolve company CNPJ from geremp.cgcemp for codemp=7", exception.message)
    }
}
