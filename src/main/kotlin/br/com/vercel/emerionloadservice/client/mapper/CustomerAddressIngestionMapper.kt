package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.client.dto.CustomerAddressDetailIngestionDto
import br.com.vercel.emerionloadservice.client.dto.CustomerAddressIngestionDto
import br.com.vercel.emerionloadservice.model.CustomerAddress
import br.com.vercel.emerionloadservice.model.CustomerAddressDetail

object CustomerAddressIngestionMapper {

    // The receiving service generates its own internal id, so codCli is sent as
    // externalId to allow it to be traced back to the source customer record.
    fun CustomerAddress.toIngestionDto(): CustomerAddressIngestionDto {
        return CustomerAddressIngestionDto(
            externalId = this.codCli,
            cnpjEmpresa = this.cnpjEmpresa,
            cpfCnpj = this.cpfCnpj,
            enderecos = this.enderecos.map { it.toIngestionDto() }
        )
    }

    private fun CustomerAddressDetail.toIngestionDto(): CustomerAddressDetailIngestionDto {
        return CustomerAddressDetailIngestionDto(
            tipo = this.tipo,
            cep = this.cep,
            endereco = this.endereco,
            numero = this.numero,
            referencia = this.referencia,
            bairro = this.bairro,
            cidade = this.cidade,
            uf = this.uf,
            telefone = this.telefone,
            telefoneContato = this.telefoneContato,
            complemento = this.complemento,
            fax = this.fax
        )
    }
}
