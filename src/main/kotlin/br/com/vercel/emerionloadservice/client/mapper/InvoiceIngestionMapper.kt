package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.client.dto.InvoiceIngestionDto
import br.com.vercel.emerionloadservice.model.Invoice

object InvoiceIngestionMapper {
    fun Invoice.toIngestionDto(cnpjEmpresa: String): InvoiceIngestionDto =
        InvoiceIngestionDto(
            externalId = "${this.codEmp}-${this.dteres}-${this.numres}-${this.nronfs ?: "SEMNF"}",
            cnpjEmpresa = cnpjEmpresa,
            codEmp = this.codEmp,
            customerExternalId = this.codCli,
            numres = this.numres,
            dteres = this.dteres,
            nronfs = this.nronfs,
            dataFaturamento = this.dataFaturamento,
            totalFaturado = this.totalFaturado,
        )
}
