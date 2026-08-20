package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.client.dto.ProductIngestionDto
import br.com.vercel.emerionloadservice.model.Product

object ProductIngestionMapper {

    // The receiving service generates its own internal id, so the local id
    // is sent as externalId to allow it to be traced back to the source record.
    fun Product.toIngestionDto(cnpjEmpresa: String): ProductIngestionDto {
        return ProductIngestionDto(
            externalId = "${this.codGru.trim()}.${this.codSub.trim()}.${this.codPro.trim()}",
            cnpjEmpresa = cnpjEmpresa,
            nome = this.nome,
            descricaoReduzida = this.descricaoReduzida,
            referenciaInterna = this.referenciaInterna,
            ncm = this.ncm,
            cest = this.cest,
            origemProduto = this.origemProduto,
            categoria = this.categoria,
            tipo = this.tipo,
            marca = this.marca,
            unidadeEntrada = this.unidadeEntrada,
            unidadeSaida = this.unidadeSaida,
            pesoLiquido = this.pesoLiquido,
            pesoBruto = this.pesoBruto,
            descontinuado = this.descontinuado,
            codigoBarras = this.codigoBarras,
            codigoBarrasProprio = this.codigoBarrasProprio,
            preco = this.preco,
            preco2 = this.preco2,
            preco3 = this.preco3,
            preco4 = this.preco4,
            preco5 = this.preco5,
            descontoPadrao = this.descontoPadrao,
            estoqueDisponivel = this.estoqueDisponivel,
            estoqueMinimo = this.estoqueMinimo,
            estoqueMaximo = this.estoqueMaximo,
            estoqueReservado = this.estoqueReservado,
            estoqueAdquirido = this.estoqueAdquirido,

            estoqueAtual = this.estoqueAtual,
            estoqueRMA = this.estoqueRMA,
            simpro = this.simpro,
            quantidadeVolumes = this.quantidadeVolumes,
            quantidadeEmbalagem = this.quantidadeEmbalagem,
            locpro = this.locpro,
            pescub = this.pescub,
            cbaemb = this.cbaemb,
            ibsCClassTrib = this.ibsCClassTrib,
            ibsCst = this.ibsCst,
            fcpEntrada = this.fcpEntrada,
            fcpSaida = this.fcpSaida,
            ipiSaida = this.ipiSaida,
            ipiEntrada = this.ipiEntrada,
            icmSaida = this.icmSaida,
            icmEntrada = this.icmEntrada,
            icmStSaida = this.icmStSaida,
            icmStEntrada = this.icmStEntrada,
            saiicm = this.saiicm,
            enticm = this.enticm,
            codst2 = this.codst2,
            observacao = this.observacao,
        )
    }
}
