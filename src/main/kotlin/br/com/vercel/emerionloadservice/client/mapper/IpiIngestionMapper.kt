package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.api.model.IpiIngestionDto
import br.com.vercel.emerionloadservice.model.Ipi

object IpiIngestionMapper {
    fun Ipi.toIngestionDto(cnpjEmpresa: String): IpiIngestionDto =
        IpiIngestionDto(
            cnpjEmpresa = cnpjEmpresa,
            flgAtivo = this.flgAtivo,
            codigoIpi = this.codigoIpi,
            tipoIpi = this.tipoIpi,
            nomeIpi = this.nomeIpi,
            ncmIpi = this.ncmIpi,
            codigoEnquadramentoLegal = this.codigoEnquadramentoLegal,
            cstIpi = this.cstIpi,
            descricaoSituacaoTributariaIpi = this.descricaoSituacaoTributariaIpi,
            aliquotaIpi = this.aliquotaIpi,
            percentualBaseCalculoIpi = this.percentualBaseCalculoIpi,
            flgSineif20 = this.flgSineif20,
            codigoTextoFiscal = this.codigoTextoFiscal,
            cstPis = this.cstPis,
            descricaoSituacaoTributariaPis = this.descricaoSituacaoTributariaPis,
            aliquotaPis = this.aliquotaPis,
            incluiDescontoSuframaPis = this.incluiDescontoSuframaPis,
            cstCofins = this.cstCofins,
            descricaoSituacaoTributariaCofins = this.descricaoSituacaoTributariaCofins,
            aliquotaCofins = this.aliquotaCofins,
            incluiDescontoSuframaCofins = this.incluiDescontoSuframaCofins,
        )
}
