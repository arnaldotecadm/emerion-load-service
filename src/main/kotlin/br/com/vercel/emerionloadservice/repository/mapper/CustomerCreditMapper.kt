package br.com.vercel.emerionloadservice.repository.mapper

import br.com.vercel.emerionloadservice.model.CustomerCredit
import br.com.vercel.emerionloadservice.repository.projection.CustomerCreditProjection
import org.springframework.data.domain.Page

object CustomerCreditMapper {

    fun Page<CustomerCreditProjection>.toModel(): Page<CustomerCredit> {
        return this.map { it.toModel() }
    }

    fun List<CustomerCreditProjection>.toModel(): List<CustomerCredit> {
        return this.map { it.toModel() }
    }

    fun CustomerCreditProjection.toModel(): CustomerCredit {
        return CustomerCredit(
            codCli = this.codCli,
            sequencia = this.sequencia,
            data = this.data,
            dataPedido = this.dataPedido,
            valorUtilizado = this.valorUtilizado,
            valorTotal = this.valorTotal,
            saldo = this.saldo,
            situacao = this.situacao
        )
    }
}
