package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.client.dto.Pedlb2IngestionDto
import br.com.vercel.emerionloadservice.client.dto.PedlibIngestionDto
import br.com.vercel.emerionloadservice.model.Pedlb2
import br.com.vercel.emerionloadservice.model.Pedlib

object PedlibIngestionMapper {
    fun Pedlib.toIngestionDto(cnpjEmpresa: String) =
        PedlibIngestionDto(
            cnpjEmpresa = cnpjEmpresa,
            codigoEmpresa = codigoEmpresa,
            dataPedido = dataPedido,
            numeroPedido = numeroPedido,
            numeroLiberacao = numeroLiberacao,
            dataLiberacao = dataLiberacao,
            horaLiberacao = horaLiberacao,
            codigoCliente = codigoCliente,
            quantidadeSeparada = quantidadeSeparada,
            totalLiberadoSemImpostos = totalLiberadoSemImpostos,
            totalLiberadoComImpostos = totalLiberadoComImpostos,
            situacaoLiberacao = situacaoLiberacao,
            codigoVendedor = codigoVendedor,
            comissaoLiberacao = comissaoLiberacao,
            totalCusto = totalCusto,
            detalhes = detalhes.map { it.toIngestionDto() },
        )

    private fun Pedlb2.toIngestionDto() =
        Pedlb2IngestionDto(
            numeroSequenciaLiberacao = numeroSequenciaLiberacao,
            classificacaoItem = classificacaoItem,
            codigoGrupo = codigoGrupo,
            codigoSubGrupo = codigoSubGrupo,
            codigoProduto = codigoProduto,
            descricaoItemLiberacao = descricaoItemLiberacao,
            quantidadeNoPedido = quantidadeNoPedido,
            totalSeparado = totalSeparado,
            quantidadeRestante = quantidadeRestante,
            totalValorLiquido = totalValorLiquido,
            totalValorBruto = totalValorBruto,
            percentualDesconto = percentualDesconto,
            totalCusto = totalCusto,
            percentualDeAcrescimo = percentualDeAcrescimo,
            precoVendaItem = precoVendaItem,
            precoPraticado = precoPraticado,
            custoPraticado = custoPraticado,
        )
}
