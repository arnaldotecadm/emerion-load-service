package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.Receivable
import br.com.vercel.emerionloadservice.repository.projection.ReceivableProjection
import org.springframework.data.domain.Page

object ReceivableMapper {

    fun Page<ReceivableProjection>.toModel(): Page<Receivable> {
        return this.map { it.toModel() }
    }

    fun ReceivableProjection.toModel(): Receivable {
        return Receivable(
            codCli = this.codCli,
            sequencia = this.sequencia,
            dataLancamento = this.dataLancamento,
            dataReferenciaPedido = this.dataReferenciaPedido,
            valorOriginal = this.valorOriginal,
            valorUtilizado = this.valorUtilizado,
            saldoAberto = this.saldoAberto,
            situacao = this.situacao
        )
    }
}

