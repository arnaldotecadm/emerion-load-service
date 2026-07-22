package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.Vendedor
import br.com.vercel.emerionloadservice.repository.VendedorQueryRepository
import br.com.vercel.emerionloadservice.repository.VendedorRepository
import br.com.vercel.emerionloadservice.repository.mapper.VendedorMapper.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

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
        return vendedorRepository.getVendedorByCodVen(codVen).toModel()
    }

    fun sendVendedorToIngestion(codVen: Long) {
        val vendedor = getVendedorByCodVen(codVen)
        ingestionServiceClient.sendVendedor(vendedor)
    }
}
