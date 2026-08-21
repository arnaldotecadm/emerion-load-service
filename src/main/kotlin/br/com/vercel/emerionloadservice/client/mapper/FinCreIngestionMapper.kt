package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.client.dto.FinCreIngestionDto
import br.com.vercel.emerionloadservice.client.dto.FinCrpIngestionDto
import br.com.vercel.emerionloadservice.model.FinCre
import br.com.vercel.emerionloadservice.model.FinCrp

object FinCreIngestionMapper {

    fun FinCre.toIngestionDto(cnpjEmpresa: String): FinCreIngestionDto = FinCreIngestionDto(
        cnpjEmpresa = cnpjEmpresa,
        codigoEmpresa = this.codigoEmpresa,
        dataEmissao = this.dataEmissao,
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
        parcelas = this.parcelas.map { it.toIngestionDto() }
    )

    private fun FinCrp.toIngestionDto(): FinCrpIngestionDto = FinCrpIngestionDto(
        numeroParcela = this.numeroParcela,
        flagIncobravel = this.flagIncobravel,
        dataIncobravel = this.dataIncobravel,
        dataVencimento = this.dataVencimento,
        prazoEmDias = this.prazoEmDias,
        valorParcela = this.valorParcela,
        numeroBancario = this.numeroBancario,
        codigoBanco = this.codigoBanco,
        nomeBanco = this.nomeBanco,
        observacoes = this.observacoes,
        flagCartaAnuencia = this.flagCartaAnuencia,
        dataCartaAnuencia = this.dataCartaAnuencia,
        flagPago = this.flagPago
    )
}
