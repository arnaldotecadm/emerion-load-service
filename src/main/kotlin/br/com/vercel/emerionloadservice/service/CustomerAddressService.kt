package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.CustomerAddress
import br.com.vercel.emerionloadservice.repository.CustomerAddressQueryRepository
import br.com.vercel.emerionloadservice.repository.CustomerAddressRepository
import br.com.vercel.emerionloadservice.repository.mapper.CustomerAddressMapper.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CustomerAddressService(
    private val customerAddressRepository: CustomerAddressRepository,
    private val customerAddressQueryRepository: CustomerAddressQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient
) {
    fun getAllAddresses(pageable: Pageable): Page<CustomerAddress> {
        return customerAddressQueryRepository.findAllPaged(pageable)
    }

    fun getAddressByCodCli(codCli: Long): CustomerAddress {
        val header = customerAddressRepository.getHeaderByCodCli(codCli)
        val rows = customerAddressRepository.getAddressRowsByCodCli(codCli)
        return header.toModel(rows)
    }

    fun sendAddressToIngestion(codCli: Long) {
        val address = getAddressByCodCli(codCli)
        ingestionServiceClient.sendCustomerAddress(address)
    }
}
