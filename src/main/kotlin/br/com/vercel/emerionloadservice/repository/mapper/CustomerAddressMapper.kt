package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.CustomerAddress
import br.com.vercel.emerionloadservice.model.CustomerAddressDetail
import br.com.vercel.emerionloadservice.repository.projection.CustomerAddressHeaderProjection
import br.com.vercel.emerionloadservice.repository.projection.CustomerAddressRowProjection

object CustomerAddressMapper {

    fun CustomerAddressHeaderProjection.toModel(rows: List<CustomerAddressRowProjection>): CustomerAddress {
        return CustomerAddress(
            codCli = this.codCli,
            cnpjEmpresa = this.cnpjEmpresa,
            cpfCnpj = this.cpfCnpj,
            enderecos = rows.toModel()
        )
    }

    fun List<CustomerAddressRowProjection>.toModel(): List<CustomerAddressDetail> {
        return this.map { it.toModel() }
    }

    fun CustomerAddressRowProjection.toModel(): CustomerAddressDetail {
        return CustomerAddressDetail(
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
