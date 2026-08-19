package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.client.dto.ReceivableIngestionDto
import br.com.vercel.emerionloadservice.model.Receivable

object ReceivableIngestionMapper {

    fun Receivable.toIngestionDto(cnpjEmpresa: String): ReceivableIngestionDto {
        return ReceivableIngestionDto(
            externalId = "${this.codCli}-${this.sequencia ?: "SEMSEQ"}",
            cnpjEmpresa = cnpjEmpresa,
            customerExternalId = this.codCli,
            sequencia = this.sequencia,
            dataLancamento = this.dataLancamento,
            dataReferenciaPedido = this.dataReferenciaPedido,
            valorOriginal = this.valorOriginal,
            valorUtilizado = this.valorUtilizado,
            saldoAberto = this.saldoAberto,
            situacao = this.situacao
        )
    }

    fun List<Receivable>.toIngestionDto(cnpjEmpresa: String): List<ReceivableIngestionDto> {
        return this.map { it.toIngestionDto(cnpjEmpresa) }
    }
}

