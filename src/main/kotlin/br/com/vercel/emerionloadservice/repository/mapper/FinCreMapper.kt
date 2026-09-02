package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.FinCrp
import br.com.vercel.emerionloadservice.repository.projection.FinCrpProjectionImpl

object FinCreMapper {
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
