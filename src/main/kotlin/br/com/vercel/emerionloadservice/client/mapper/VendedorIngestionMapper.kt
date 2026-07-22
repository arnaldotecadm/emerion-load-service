package br.com.vercel.emerionloadservice.client.mapper

import br.com.vercel.emerionloadservice.client.dto.VendedorIngestionDto
import br.com.vercel.emerionloadservice.model.Vendedor

object VendedorIngestionMapper {

    // The receiving service generates its own internal id, so the local id
    // is sent as externalId to allow it to be traced back to the source record.
    fun Vendedor.toIngestionDto(): VendedorIngestionDto {
        return VendedorIngestionDto(
            externalId = this.id,
            nome = this.nome,
            apelido = this.apelido,
            cpfCnpj = this.cpfCnpj,
            telefone = this.telefone,
            celular = this.celular,
            email = this.email,
            cidade = this.cidade,
            uf = this.uf,
            situacao = this.situacao,
            saldo = this.saldo,
            dataCadastro = this.dataCadastro
        )
    }
}
