package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.Vendedor
import br.com.vercel.emerionloadservice.repository.projection.VendedorProjection
import org.springframework.data.domain.Page

object VendedorMapper {

    fun Page<VendedorProjection>.toModel(): Page<Vendedor> {
        return this.map { it.toModel() }
    }

    fun VendedorProjection.toModel(): Vendedor {
        return Vendedor(
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
            dataCadastro = this.dataCadastro?.toLocalDate()
        )
    }
}
