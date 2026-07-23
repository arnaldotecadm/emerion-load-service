package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.client.dto.CustomerCreditIngestionDto
import br.com.vercel.emerionloadservice.model.CustomerCredit

object CustomerCreditIngestionMapper {

    // The credit entry has no id of its own in the legacy schema, so codCli is sent as
    // customerExternalId to allow the receiving service to associate it with the customer.
    fun CustomerCredit.toIngestionDto(cnpjEmpresa: String): CustomerCreditIngestionDto {
        return CustomerCreditIngestionDto(
            customerExternalId = this.codCli,
            cnpjEmpresa = cnpjEmpresa,
            sequencia = this.sequencia,
            data = this.data,
            dataPedido = this.dataPedido,
            valorUtilizado = this.valorUtilizado,
            valorTotal = this.valorTotal,
            saldo = this.saldo,
            situacao = this.situacao,
            tipo = this.tipo
        )
    }

    fun List<CustomerCredit>.toIngestionDto(cnpjEmpresa: String): List<CustomerCreditIngestionDto> {
        return this.map { it.toIngestionDto(cnpjEmpresa) }
    }
}
