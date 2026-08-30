package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.Product
import br.com.vercel.emerionloadservice.repository.projection.ProductProjectionImpl
import org.springframework.data.domain.Page

object ProductMapper {
    fun Page<ProductProjectionImpl>.toModel(): Page<Product> = this.map { it.toModel() }

    fun ProductProjectionImpl.toModel(): Product =
        Product(
            codGru = this.codGru.trim(),
            codSub = this.codSub.trim(),
            codPro = this.codPro.trim(),
            nome = this.nome.trim(),
            descricaoReduzida = this.descricaoReduzida?.trim(),
            referenciaInterna = this.referenciaInterna?.trim(),
            ncm = this.ncm?.trim(),
            cest = this.cest?.trim(),
            origemProduto = this.origemProduto?.trim(),
            categoria = this.categoria?.trim(),
            tipo = this.tipo?.trim(),
            marca = this.marca?.trim(),
            unidadeSaida = this.unidadeSaida?.trim(),
            unidadeEntrada = this.unidadeEntrada?.trim(),
            pesoLiquido = this.pesoLiquido,
            pesoBruto = this.pesoBruto,
            descontinuado = this.descontinuado == 1,
            codigoBarras = this.codigoBarras?.trim(),
            codigoBarrasProprio = this.codigoBarrasProprio?.trim(),
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
            similar = this.similar,
            quantidadeVolumes = this.quantidadeVolumes,
            quantidadeEmbalagem = this.quantidadeEmbalagem,
            localizacao = this.localizacao,
            cubagem = this.cubagem,
            codigoBarrasEmbalagem = this.codigoBarrasEmbalagem,
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
            observacao = this.observacao,
        )
}
