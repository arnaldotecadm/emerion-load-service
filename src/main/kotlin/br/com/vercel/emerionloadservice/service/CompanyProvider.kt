package br.com.vercel.emerionloadservice.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

/**
 * Resolves the CNPJ (geremp.cgcemp) that uniquely identifies the retailer/tenant whose
 * Firebird database this running instance is pointed at.
 *
 * emerion-load-service is deployed once per retailer, so this value never changes for the
 * lifetime of a running instance: it is resolved once and cached. The resulting CNPJ is
 * stamped onto every DTO sent to the ingestion service as `cnpjEmpresa`, distinct from each
 * customer's own `cpfCnpj` (fincli.cgccli), so emerion-dashboard can segregate data by
 * retailer while still knowing which customer (fincli.cgccli) each record belongs to.
 */
@Component
class CompanyProvider(
    private val jdbcTemplate: JdbcTemplate,
    @Value("\${company.codemp:1}") private val codEmp: Int
) {
    @Volatile
    private var cachedCnpj: String? = null

    fun getCompanyCnpj(): String {
        return cachedCnpj ?: synchronized(this) {
            cachedCnpj ?: fetchCompanyCnpj().also { cachedCnpj = it }
        }
    }

    private fun fetchCompanyCnpj(): String {
        val cnpj = jdbcTemplate.queryForObject(
            "select cgcemp from geremp where codemp = ?",
            String::class.java,
            codEmp
        )

        return requireNotNull(cnpj?.trim()?.takeIf { it.isNotEmpty() }) {
            "Could not resolve company CNPJ from geremp.cgcemp for codemp=$codEmp"
        }
    }
}
