package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.client.dto.CustomerIngestionDto
import br.com.vercel.emerionloadservice.model.Customer

object CustomerIngestionMapper {

    // The receiving service generates its own internal id, so the local id
    // is sent as externalId to allow it to be traced back to the source record.
    fun Customer.toIngestionDto(cnpjEmpresa: String): CustomerIngestionDto {
        return CustomerIngestionDto(
            externalId = this.id,
            cnpjEmpresa = cnpjEmpresa,
            nomeFantasia = this.nomeFantasia,
            razaoSocial = this.razaoSocial,
            cpfCnpj = this.cpfCnpj,
            inscricaoEstadual = this.inscricaoEstadual,
            regimeTributario = this.regimeTributario,
            bloqueado = this.bloqueado,
            dataNascimento = this.dataNascimento,
            dataCadastro = this.dataCadastro,
            dataUltimaAtualizacao = this.dataUltimaAtualizacao,
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
