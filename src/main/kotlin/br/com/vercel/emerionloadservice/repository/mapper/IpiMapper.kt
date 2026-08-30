package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.Ipi
import br.com.vercel.emerionloadservice.repository.projection.IpiProjectionImpl
import org.springframework.data.domain.Page

object IpiMapper {
    fun Page<IpiProjectionImpl>.toModel(): Page<Ipi> = this.map { it.toModel() }

    fun IpiProjectionImpl.toModel(): Ipi =
        Ipi(
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
