package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.repository.projection.CustomerProjectionImpl
import org.springframework.data.domain.Page

object CustomerMapper {

    fun Page<CustomerProjectionImpl>.toModel(): Page<Customer> {
        return this.map { it.toModel() }
    }

    fun CustomerProjectionImpl.toModel(): Customer {
        return Customer(
            id = this.id,
            nomeFantasia = this.nomeFantasia,
            razaoSocial = this.razaoSocial,
            cpfCnpj = this.cpfCnpj,
            inscricaoEstadual = this.inscricaoEstadual,
            regimeTributario = this.regimeTributario,
            bloqueado = this.bloqueado == 1,
            dataNascimento = this.dataNascimento?.toLocalDate(),
            dataCadastro = this.dataCadastro?.toLocalDate(),
            dataUltimaAtualizacao = this.dataUltimaAtualizacao?.toLocalDate(),
            email1 = this.email1,
            email2 = this.email2,
            website = this.website,
            limiteCredito = this.limiteCredito,
            observacoes = this.observacoes,
            cnae = this.cnae,
            vendedorExternalId = this.vendedorExternalId,
            nomeVendedor = this.nomeVendedor,
            codigoTipoCliente = this.codigoTipoCliente,
            codigoGrupoCliente = this.codigoGrupoCliente,
            codigoCategoriaCliente = this.codigoCategoriaCliente,
            uf = this.uf?.trim(),
            macroRegiao = this.macroRegiao?.trim(),
            microRegiao = this.microRegiao?.trim(),
            setor = this.setor?.trim(),

            faturamentoCep = this.faturamentoCep,
            faturamentoTipoEndereco = this.faturamentoTipoEndereco,
            faturamentoEndereco = this.faturamentoEndereco,
            faturamentoNumero = this.faturamentoNumero,
            faturamentoComplemento = this.faturamentoComplemento,
            faturamentoBairro = this.faturamentoBairro,
            faturamentoCidade = this.faturamentoCidade,
            faturamentoUf = this.faturamentoUf,
            faturamentoDDDTelefone = this.faturamentoDDDTelefone,
            faturamentoTelefone = this.faturamentoTelefone,
            faturamentoDDDFax = this.faturamentoDDDFax,
            faturamentoFax = this.faturamentoFax,
            faturamentoContato = this.faturamentoContato,
            faturamentoDDDCelular = this.faturamentoDDDCelular,
            faturamentoCelular = this.faturamentoCelular,

            cobrancaCep = this.cobrancaCep,
            cobrancaTipoEndereco = this.cobrancaTipoEndereco,
            cobrancaEndereco = this.cobrancaEndereco,
            cobrancaNumero = this.cobrancaNumero,
            cobrancaComplemento = this.cobrancaComplemento,
            cobrancaBairro = this.cobrancaBairro,
            cobrancaCidade = this.cobrancaCidade,
            cobrancaUf = this.cobrancaUf,
            cobrancaDDDTelefone = this.cobrancaDDDTelefone,
            cobrancaTelefone = this.cobrancaTelefone,
            cobrancaDDDFax = this.cobrancaDDDFax,
            cobrancaFax = this.cobrancaFax,
            cobrancaContato = this.cobrancaContato,
            cobrancaDDDCelular = this.cobrancaDDDCelular,
            cobrancaCelular = this.cobrancaCelular,

            entregaCep = this.entregaCep,
            entregaTipoEndereco = this.entregaTipoEndereco,
            entregaEndereco = this.entregaEndereco,
            entregaNumero = this.entregaNumero,
            entregaComplemento = this.entregaComplemento,
            entregaBairro = this.entregaBairro,
            entregaCidade = this.entregaCidade,
            entregaUf = this.entregaUf,
            entregaDDDTelefone = this.entregaDDDTelefone,
            entregaTelefone = this.entregaTelefone,
            entregaDDDFax = this.entregaDDDFax,
            entregaFax = this.entregaFax,
            entregaContato = this.entregaContato,
            entregaDDDCelular = this.entregaDDDCelular,
            entregaCelular = this.entregaCelular,
        )
    }
}