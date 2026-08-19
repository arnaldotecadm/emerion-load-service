package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.InvoiceItemLink
import br.com.vercel.emerionloadservice.repository.projection.InvoiceItemLinkProjection
import org.springframework.data.domain.Page

object InvoiceItemLinkMapper {

    fun Page<InvoiceItemLinkProjection>.toModel(): Page<InvoiceItemLink> {
        return this.map { it.toModel() }
    }

    fun InvoiceItemLinkProjection.toModel(): InvoiceItemLink {
        return InvoiceItemLink(
            codEmp = this.codEmp,
            numres = this.numres,
            dteres = this.dteres.toLocalDate(),
            seqRe2 = this.seqRe2,
            codClp = this.codClp?.trim(),
            codGru = this.codGru?.trim(),
            codSub = this.codSub?.trim(),
            codPro = this.codPro?.trim(),
            nronfs = this.nronfs?.trim(),
            dataFaturamento = this.dataFaturamento?.toLocalDate(),
            totalFaturado = this.totalFaturado
        )
    }
}

