package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.Invoice
import br.com.vercel.emerionloadservice.repository.projection.InvoiceProjection
import org.springframework.data.domain.Page

object InvoiceMapper {

    fun Page<InvoiceProjection>.toModel(): Page<Invoice> {
        return this.map { it.toModel() }
    }

    fun InvoiceProjection.toModel(): Invoice {
        return Invoice(
            codEmp = this.codEmp,
            codCli = this.codCli,
            numres = this.numres,
            dteres = this.dteres.toLocalDate(),
            nronfs = this.nronfs,
            dataFaturamento = this.dataFaturamento?.toLocalDate(),
            totalFaturado = this.totalFaturado
        )
    }
}

