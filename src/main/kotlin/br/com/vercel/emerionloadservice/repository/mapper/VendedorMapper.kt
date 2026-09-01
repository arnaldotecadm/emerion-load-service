package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.Vendedor
import br.com.vercel.emerionloadservice.repository.projection.VendedorProjectionImpl
import org.springframework.data.domain.Page

object VendedorMapper {
    fun Page<VendedorProjectionImpl>.toModel(): Page<Vendedor> = this.map { it.toModel() }

    fun VendedorProjectionImpl.toModel(): Vendedor =
        Vendedor(
            id = this.id,
            nome = this.nome.trim(),
            apelido = this.apelido?.trim(),
            cpfCnpj = this.cpfCnpj?.trim(),
            telefone = this.telefone?.trim(),
            celular = this.celular?.trim(),
            email = this.email?.trim(),
            cidade = this.cidade?.trim(),
            uf = this.uf?.trim(),
            situacao = this.situacao?.trim(),
            saldo = this.saldo,
            dataCadastro = this.dataCadastro?.toLocalDate(),
            metaRepresentacao = this.metaRepresentacao,
        )
}
