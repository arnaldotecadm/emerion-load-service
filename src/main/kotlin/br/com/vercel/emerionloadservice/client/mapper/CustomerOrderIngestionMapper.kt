package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.client.dto.CustomerOrderItemIngestionDto
import br.com.vercel.emerionloadservice.client.dto.CustomerOrderIngestionDto
import br.com.vercel.emerionloadservice.model.CustomerOrder
import br.com.vercel.emerionloadservice.model.CustomerOrderItem

object CustomerOrderIngestionMapper {

    // The receiving service generates its own internal id, so numres is sent as
    // externalId to allow it to be traced back to the source order record.
    fun CustomerOrder.toIngestionDto(): CustomerOrderIngestionDto {
        return CustomerOrderIngestionDto(
            externalId = this.numres,
            codCli = this.codCli,
            cnpjEmpresa = this.cnpjEmpresa,
            nronfe = this.nronfe,
            dteres = this.dteres,
            sitres = this.sitres,
            totger = this.totger,
            totres = this.totres,
            totipi = this.totipi,
            totsub = this.totsub,
            totdescinc = this.totdescinc,
            itens = this.itens.map { it.toIngestionDto() }
        )
    }

    private fun CustomerOrderItem.toIngestionDto(): CustomerOrderItemIngestionDto {
        return CustomerOrderItemIngestionDto(
            produto = this.produto,
            descricao = this.descricao,
            quantidade = this.quantidade,
            valorUnitario = this.valorUnitario,
            valorTotal = this.valorTotal
        )
    }
}
