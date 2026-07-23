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
