package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.data.DummyEntity
import br.com.vercel.emerionloadservice.repository.projection.CustomerOrderHeaderProjection
import br.com.vercel.emerionloadservice.repository.projection.CustomerOrderItemProjection
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerOrderRepository : PagingAndSortingRepository<DummyEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            select
                p.codcli as codCli,
                p.numres as numres,
                fat.nronfs as nronfe,
                p.dteres as dteres,
                p.sitres as sitres,
                p.totger as totger,
                p.totres as totres,
                p.totipi as totipi,
                p.totsub as totsub,
                p.totdescinc as totdescinc
            from pedres p
            left join fatped fat
                on fat.codemp = p.codemp
                and fat.dteres = p.dteres
                and fat.numres = p.numres
            where p.numres = :numres
        """
    )
    fun getHeaderByNumres(numres: String): CustomerOrderHeaderProjection

    @Query(
        nativeQuery = true,
        value = """
            select
                re2.numres as numres,
                re2.codgru as codGru,
                re2.codsub as codSub,
                re2.codpro as codPro,
                re2.desre2 as descricao,
                re2.qtpre2 as quantidade,
                re2.vlqre2 as valorUnitario,
                re2.totre2 as valorTotal,
                re2.seqre2 as seqRe2,
                re2.codclp as codClp,
                re2.codst1 as codSt1,
                re2.codund as codUnd,
                re2.vlure2 as vluRe2,
                re2.dscre2 as dscRe2,
                re2.dsrre2 as dsrRe2,
                re2.icmre2 as icmsAliquota,
                re2.basicm as icmsBase,
                re2.toticm as icmsValor,
                re2.redicm as icmsReducaoBase,
                re2.bassub as icmsSubstituicaoBase,
                re2.totsub as icmsSubstituicaoValor,
                re2.icmsub as icmsSubstituicaoAliquota,
                re2.mrgsub as icmsSubstituicaoMargem,
                re2.redsub as icmsSubstituicaoReducaoBase,
                re2.ipire2 as ipiAliquota,
                re2.basipi as ipiBase,
                re2.totipi as ipiValor,
                re2.clsipi as ipiClassificacao,
                re2.cstipi as ipiCst,
                re2.baspis as pisBase,
                re2.aliqpis as pisAliquota,
                re2.totpis as pisValor,
                re2.cstpis as pisCst,
                re2.bascof as cofinsBase,
                re2.aliqcof as cofinsAliquota,
                re2.totcof as cofinsValor,
                re2.cstcof as cofinsCst,
                re2.totdsr as descontoValor,
                re2.totfrt as freteValor,
                re2.totseg as seguroValor,
                re2.totoutdesp as outrasDespesasValor,
                re2.totitetrb as totalItemTributado,
                re2.totren as totRen,
                re2.totge2 as totGe2,
                re2.obsre2 as observacao,
                re2.numpedcompra as pedidoCompraCliente,
                re2.numitemcompra as itemPedidoCompraCliente,
                re2.nrore2 as nroRe2,
                re2.flgval as flgVal,
                re2.flgpac as flgPac,
                re2.flglib as flgLib,
                re2.codcfo as codCfo
            from pedre2 re2
            where re2.numres = :numres
            order by re2.seqre2
        """
    )
    fun getItemsByNumres(numres: String): List<CustomerOrderItemProjection>
}
