package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.CustomerOrder
import br.com.vercel.emerionloadservice.model.CustomerOrderItem
import br.com.vercel.emerionloadservice.repository.projection.CustomerOrderHeaderProjection
import br.com.vercel.emerionloadservice.repository.projection.CustomerOrderItemProjection

object CustomerOrderMapper {

    fun CustomerOrderHeaderProjection.toModel(items: List<CustomerOrderItemProjection>): CustomerOrder {
        return CustomerOrder(
            codCli = this.codCli,
            cnpjEmpresa = this.cnpjEmpresa,
            numres = this.numres,
            nronfe = this.nronfe,
            dteres = this.dteres,
            sitres = this.sitres,
            totger = this.totger,
            totres = this.totres,
            totipi = this.totipi,
            totsub = this.totsub,
            totdescinc = this.totdescinc,
            itens = items.toModel()
        )
    }

    fun List<CustomerOrderItemProjection>.toModel(): List<CustomerOrderItem> {
        return this.map { it.toModel() }
    }

    fun CustomerOrderItemProjection.toModel(): CustomerOrderItem {
        return CustomerOrderItem(
            codGru = this.codGru,
            codSub = this.codSub,
            codPro = this.codPro,
            descricao = this.descricao,
            quantidade = this.quantidade,
            valorUnitario = this.valorUnitario,
            valorTotal = this.valorTotal
        )
    }
}
