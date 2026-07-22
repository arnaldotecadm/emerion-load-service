package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.data.DummyEntity
import br.com.vercel.emerionloadservice.repository.projection.VendedorProjection
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface VendedorRepository : PagingAndSortingRepository<DummyEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            select
                ven.codven as id,
                ven.nomven as nome,
                ven.apeven as apelido,
                ven.cgcven as cpfCnpj,
                ven.fonven as telefone,
                ven.celven as celular,
                ven.emaven as email,
                ven.cidven as cidade,
                ven.sigufe as uf,
                ven.flgati as situacao,
                ven.sldven as saldo,
                ven.dcaven as dataCadastro
            from finven ven
            where ven.codven = :codVen
        """
    )
    fun getVendedorByCodVen(codVen: Long): VendedorProjection
}
