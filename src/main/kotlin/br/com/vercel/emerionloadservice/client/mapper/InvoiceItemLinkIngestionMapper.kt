package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.client.dto.InvoiceItemLinkIngestionDto
import br.com.vercel.emerionloadservice.model.InvoiceItemLink

object InvoiceItemLinkIngestionMapper {

    fun InvoiceItemLink.toIngestionDto(cnpjEmpresa: String): InvoiceItemLinkIngestionDto {
        return InvoiceItemLinkIngestionDto(
            externalId = "${this.codEmp}-${this.dteres}-${this.numres}-${this.seqRe2}-${this.nronfs ?: "SEMNF"}",
            cnpjEmpresa = cnpjEmpresa,
            codEmp = this.codEmp,
            numres = this.numres,
            dteres = this.dteres,
            seqRe2 = this.seqRe2,
            codClp = this.codClp,
            codGru = this.codGru,
            codSub = this.codSub,
            codPro = this.codPro,
            nronfs = this.nronfs,
            dataFaturamento = this.dataFaturamento,
            totalFaturado = this.totalFaturado
        )
    }
}

