package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.Icms
import br.com.vercel.emerionloadservice.repository.projection.IcmsProjectionImpl
import org.springframework.data.domain.Page

object IcmsMapper {
    fun Page<IcmsProjectionImpl>.toModel(): Page<Icms> = this.map { it.toModel() }

    fun IcmsProjectionImpl.toModel(): Icms =
        Icms(
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
