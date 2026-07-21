package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.client.IngestionServiceClient
import br.com.vercel.emerionloadservice.model.CustomerCredit
import br.com.vercel.emerionloadservice.repository.CustomerCreditQueryRepository
import br.com.vercel.emerionloadservice.repository.CustomerCreditRepository
import br.com.vercel.emerionloadservice.repository.mapper.CustomerCreditMapper.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CustomerCreditService(
    private val customerCreditRepository: CustomerCreditRepository,
    private val customerCreditQueryRepository: CustomerCreditQueryRepository,
    private val ingestionServiceClient: IngestionServiceClient
) {
    fun getAllCredits(pageable: Pageable): Page<CustomerCredit> {
        return customerCreditQueryRepository.findAllPaged(pageable).toModel()
    }

    fun getCreditsByCodCli(codCli: Long): List<CustomerCredit> {
        return customerCreditRepository.getCreditsByCodCli(codCli).toModel()
    }

    fun sendCreditsToIngestion(codCli: Long) {
        val credits = getCreditsByCodCli(codCli)
        ingestionServiceClient.sendCustomerCredits(credits)
    }
}
