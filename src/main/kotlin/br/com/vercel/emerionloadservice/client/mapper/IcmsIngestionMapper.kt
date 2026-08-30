package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.client.dto.IcmsIngestionDto
import br.com.vercel.emerionloadservice.model.Icms

object IcmsIngestionMapper {
    fun Icms.toIngestionDto(cnpjEmpresa: String): IcmsIngestionDto =
        IcmsIngestionDto(
            cnpjEmpresa = cnpjEmpresa,
            codigoIcms = this.codigoIcms,
            tipoIcms = this.tipoIcms,
            nomeIcms = this.nomeIcms,
            ufEmitente = this.ufEmitente,
            codigoRegimeTributario = this.codigoRegimeTributario,
            aliquotaIcms = this.aliquotaIcms,
            percentualReducaoValorImposto = this.percentualReducaoValorImposto,
            percentualBaseCalculoIcms = this.percentualBaseCalculoIcms,
            situacaoTributariaIcms = this.situacaoTributariaIcms,
        )
}
