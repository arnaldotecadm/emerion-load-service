package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.Receivable
import br.com.vercel.emerionloadservice.repository.projection.ReceivableProjectionImpl
import org.springframework.data.domain.Page

object ReceivableMapper {
    fun Page<ReceivableProjectionImpl>.toModel(): Page<Receivable> = this.map { it.toModel() }

    fun ReceivableProjectionImpl.toModel(): Receivable =
        Receivable(
            codCli = this.codCli,
            sequencia = this.sequencia,
            dataLancamento = this.dataLancamento,
            dataReferenciaPedido = this.dataReferenciaPedido,
            valorOriginal = this.valorOriginal,
            valorUtilizado = this.valorUtilizado,
            saldoAberto = this.saldoAberto,
            situacao = this.situacao,
        )
}
