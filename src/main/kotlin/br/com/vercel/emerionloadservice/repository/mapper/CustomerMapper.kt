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
        )
    }
}