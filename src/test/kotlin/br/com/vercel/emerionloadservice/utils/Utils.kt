package br.com.vercel.emerionloadservice.utils

import br.com.vercel.emerionloadservice.repository.projection.VendedorProjectionImpl

object Utils {
    fun buildVendedorProjection(
        id: Long = 1L,
        nome: String = "Nome do Vendedor",
        apelido: String? = "Apelido do Vendedor",
        cpfCnpj: String? = "123.456.789-00",
        telefone: String? = "(11) 1234-5678",
        celular: String? = "(11) 91234-5678",
        email: String? = "vendedor@example.com",
    ): VendedorProjectionImpl =
        VendedorProjectionImpl(
            id = id,
            nome = nome,
            apelido = apelido,
            cpfCnpj = cpfCnpj,
            telefone = telefone,
            celular = celular,
            email = email,
            cidade = null,
            uf = null,
            situacao = null,
            saldo = null,
            dataCadastro = null,
            metaRepresentacao = null,
        )
}
