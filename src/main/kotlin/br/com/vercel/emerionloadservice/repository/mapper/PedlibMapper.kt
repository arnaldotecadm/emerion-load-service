package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.Pedlb2
import br.com.vercel.emerionloadservice.model.Pedlib
import br.com.vercel.emerionloadservice.repository.projection.Pedlb2ProjectionImpl
import br.com.vercel.emerionloadservice.repository.projection.PedlibProjectionImpl

object PedlibMapper {

    fun PedlibProjectionImpl.toModel(detalhes: List<Pedlb2ProjectionImpl>): Pedlib = Pedlib(
        codigoEmpresa = codigoEmpresa,
        dataPedido = dataPedido?.toLocalDate(),
        numeroPedido = numeroPedido,
        numeroLiberacao = numeroLiberacao,
        dataLiberacao = dataLiberacao?.toLocalDate(),
        horaLiberacao = horaLiberacao,
        codigoCliente = codigoCliente,
        quantidadeSeparada = quantidadeSeparada,
        totalLiberadoSemImpostos = totalLiberadoSemImpostos,
        totalLiberadoComImpostos = totalLiberadoComImpostos,
        situacaoLiberacao = situacaoLiberacao,
        codigoVendedor = codigoVendedor,
        comissaoLiberacao = comissaoLiberacao,
        totalCusto = totalCusto,
        detalhes = detalhes.map { it.toModel() }
    )

    private fun Pedlb2ProjectionImpl.toModel() = Pedlb2(
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
        custoPraticado = custoPraticado
    )
}
