package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.FinCre
import br.com.vercel.emerionloadservice.model.FinCrp
import br.com.vercel.emerionloadservice.repository.projection.FinCreProjectionImpl
import br.com.vercel.emerionloadservice.repository.projection.FinCrpProjectionImpl

object FinCreMapper {
    fun FinCreProjectionImpl.toModel(parcelas: List<FinCrpProjectionImpl>): FinCre =
        FinCre(
            codigoEmpresa = this.codigoEmpresa,
            dataEmissao = this.dataEmissao?.toLocalDate(),
            documento = this.documento,
            codigoCondicaoRecebimento = this.codigoCondicaoRecebimento,
            nomeCondicaoRecebimento = this.nomeCondicaoRecebimento,
            nomeEmpresa = this.nomeEmpresa,
            codigoComissao = this.codigoComissao,
            percentualComissao = this.percentualComissao,
            codigoCliente = this.codigoCliente,
            nomeCliente = this.nomeCliente,
            codigoVendedor = this.codigoVendedor,
            nomeVendedor = this.nomeVendedor,
            codigoTipoDocumento = this.codigoTipoDocumento,
            nomeTipoDocumento = this.nomeTipoDocumento,
            parcelas = parcelas.map { it.toModel() },
        )

    fun FinCrpProjectionImpl.toModel(): FinCrp =
        FinCrp(
            numeroParcela = this.numeroParcela,
            flagIncobravel = this.flagIncobravel,
            dataIncobravel = this.dataIncobravel?.toLocalDate(),
            dataVencimento = this.dataVencimento?.toLocalDate(),
            prazoEmDias = this.prazoEmDias,
            valorParcela = this.valorParcela,
            numeroBancario = this.numeroBancario,
            codigoBanco = this.codigoBanco,
            nomeBanco = this.nomeBanco,
            observacoes = this.observacoes,
            flagCartaAnuencia = this.flagCartaAnuencia,
            dataCartaAnuencia = this.dataCartaAnuencia?.toLocalDate(),
            flagPago = this.flagPago,
        )
}
