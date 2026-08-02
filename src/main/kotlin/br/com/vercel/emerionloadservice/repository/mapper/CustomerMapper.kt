package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.repository.projection.CustomerProjection
import org.springframework.data.domain.Page

object CustomerMapper {

    fun Page<CustomerProjection>.toModel(): Page<Customer> {
        return this.map { it.toModel() }
    }

    fun CustomerProjection.toModel(): Customer {
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
        )
    }
}