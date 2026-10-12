package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.api.model.CustomerQuotationIngestionDto
import br.com.vercel.emerionloadservice.model.CustomerQuotation

object CustomerQuotationIngestionMapper {
    fun CustomerQuotation.toIngestionDto(cnpjEmpresa: String): CustomerQuotationIngestionDto =
        CustomerQuotationIngestionDto(
            externalId = numeroPedido,
            codigoEmpresa = codigoEmpresa,
            codigoCliente = codigoCliente,
            cpfCnpj = cpfCnpj,
            cnpjEmpresa = cnpjEmpresa,
            customerExternalId = codigoCliente,
            dteres = dataPedido,
            numeroCotacao = numeroCotacao,
            numeroPedido = numeroPedido,
            dataPedido = dataPedido,
            statusPedido = statusPedido,
            totalPedidoComImpostos = totalPedidoComImpostos,
            totalPedidoSemImpostos = totalPedidoSemImpostos,
            totalIpi = totalIpi,
            totalIcms = totalIcms,
            totalPis = totalPis,
            totalCofins = totalCofins,
            totalSubstituicaoTributaria = totalSubstituicaoTributaria,
            totalDescontoIncondicional = totalDescontoIncondicional,
            totalFrete = totalFrete,
            totalSeguro = totalSeguro,
            totalOutrasDespesas = totalOutrasDespesas,
            vendedorExternalId = vendedorExternalId,
            atendenteCod = atendenteCod,
            regimeTributario = regimeTributario,
            nomeRegimeTributario = nomeRegimeTributario,
            codigoPadraoFaturamento = codigoPadraoFaturamento,
        )
}
