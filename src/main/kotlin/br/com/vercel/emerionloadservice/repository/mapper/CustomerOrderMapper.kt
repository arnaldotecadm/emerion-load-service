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
            valorTotal = this.valorTotal,
            seqRe2 = this.seqRe2,
            codClp = this.codClp,
            codSt1 = this.codSt1,
            codUnd = this.codUnd,
            vluRe2 = this.vluRe2,
            dscRe2 = this.dscRe2,
            dsrRe2 = this.dsrRe2,
            icmsAliquota = this.icmsAliquota,
            icmsBase = this.icmsBase,
            icmsValor = this.icmsValor,
            icmsReducaoBase = this.icmsReducaoBase,
            icmsSubstituicaoBase = this.icmsSubstituicaoBase,
            icmsSubstituicaoValor = this.icmsSubstituicaoValor,
            icmsSubstituicaoAliquota = this.icmsSubstituicaoAliquota,
            icmsSubstituicaoMargem = this.icmsSubstituicaoMargem,
            icmsSubstituicaoReducaoBase = this.icmsSubstituicaoReducaoBase,
            ipiAliquota = this.ipiAliquota,
            ipiBase = this.ipiBase,
            ipiValor = this.ipiValor,
            ipiClassificacao = this.ipiClassificacao,
            ipiCst = this.ipiCst,
            pisBase = this.pisBase,
            pisAliquota = this.pisAliquota,
            pisValor = this.pisValor,
            pisCst = this.pisCst,
            cofinsBase = this.cofinsBase,
            cofinsAliquota = this.cofinsAliquota,
            cofinsValor = this.cofinsValor,
            cofinsCst = this.cofinsCst,
            descontoValor = this.descontoValor,
            freteValor = this.freteValor,
            seguroValor = this.seguroValor,
            outrasDespesasValor = this.outrasDespesasValor,
            totalItemTributado = this.totalItemTributado,
            totRen = this.totRen,
            totGe2 = this.totGe2,
            observacao = this.observacao,
            pedidoCompraCliente = this.pedidoCompraCliente,
            itemPedidoCompraCliente = this.itemPedidoCompraCliente,
            nroRe2 = this.nroRe2,
            flgVal = this.flgVal,
            flgPac = this.flgPac,
            flgLib = this.flgLib,
            codCfo = this.codCfo
        )
    }
}
