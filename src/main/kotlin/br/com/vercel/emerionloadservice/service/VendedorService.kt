package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.SendAllResult
import br.com.vercel.emerionloadservice.model.Vendedor
import br.com.vercel.emerionloadservice.repository.VendedorQueryRepository
import br.com.vercel.emerionloadservice.repository.VendedorRepository
import br.com.vercel.emerionloadservice.repository.mapper.VendedorMapper.toModel
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class VendedorService(
    private val vendedorRepository: VendedorRepository,
    private val vendedorQueryRepository: VendedorQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient
) {
    fun getAllVendedores(pageable: Pageable): Page<Vendedor> {
        return vendedorQueryRepository.findAllPaged(pageable).toModel()
    }

    fun getVendedorByCodVen(codVen: Long): Vendedor {
        return vendedorRepository.getVendedorByCodVen(codVen)?.toModel()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    fun sendVendedorToIngestion(codVen: Long) {
        val vendedor = getVendedorByCodVen(codVen)
        ingestionServiceClient.sendVendedor(vendedor)
    }

    fun sendAllVendedoresToIngestion(pageable: Pageable): SendAllResult {
        var page = pageable
        var totalSent = 0
        var totalErrors = 0
        var totalPages = 0

        do {
            val currentPage = getAllVendedores(page)
            totalPages = currentPage.totalPages

            currentPage.content.forEach { vendedor ->
                try {
                    ingestionServiceClient.sendVendedor(vendedor)
                    totalSent++
                } catch (e: Exception) {
                    logger.error("Failed to send vendedor {}: {}", vendedor.id, e.message)
                    totalErrors++
                }
            }

            page = page.next()
        } while (currentPage.hasNext())

        return SendAllResult(totalSent = totalSent, totalErrors = totalErrors, totalPages = totalPages)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(VendedorService::class.java)
    }
}
