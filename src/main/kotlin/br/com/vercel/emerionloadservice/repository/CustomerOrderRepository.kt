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
                p.codemp            as codEmp,
                p.codcli            as codCli,
                fc.cgccli           as cpfCnpj,
                p.numres            as numres,
                fat.nronfs          as nronfe,
                fat.dtafat          as dataFaturamento,
                fat.totfat          as totalFaturado,
                p.dteres            as dteres,
                p.sitres            as sitres,
                p.totger            as totger,
                p.totres            as totres,
                p.totipi            as totipi,
                p.totsub            as totsub,
                p.totdescinc        as totdescinc,
                p.totfrt            as totfrt,
                p.totseg            as totseg,
                p.totoutdesp        as totoutdesp,
                p.codven            as vendedorExternalId,
                p.codatd            as atendenteCod,
                p.dtfres            as dataEntregaPrevista,
                p.dsccom            as descontoComercial,
                p.dscreg            as descontoRegional,
                p.codtra            as codigoTransportadora,
                p.linres            as linhaReserva,
                p.pedant            as pedidoAnterior,
                p.regtrb            as regimeTributario,
                reg.nomregtrib      as nomeRegimeTributario,
                p.dtecom            as dataProcessamentoComercial,
                p.dtefin            as dataProcessamentoFinanceiro,
                p.dterej            as dataRejeicao,
                p.obsrej            as observacaoRejeicao,
                p.dtedel            as dataEntrega,
                p.dtefpe            as dataFinalizacao,
                p.codpfa            as codigoPagamento,
                pfa.despfa          as descricaoPagamento
            from pedres p
            left join fatped fat
                on fat.codemp = p.codemp
                and fat.dteres = p.dteres
                and fat.numres = p.numres
            left join fincli fc
                on fc.codcli = p.codcli
            left join finregtrib reg
                on reg.numregtrib = p.regtrb
            left join estpfa pfa
                on pfa.codpfa = p.codpfa
                and pfa.tippfa = p.tippfa
            where p.codemp = :codEmp
                and p.dteres = :dteres
                and p.numres = :numres
        """
    )
    fun getHeaderByBusinessKey(codEmp: Int, dteres: java.time.LocalDate, numres: String): CustomerOrderHeaderProjection?

    @Query(
        nativeQuery = true,
        value = """
            select
                re2.codemp as codEmp,
                re2.dteres as dteres,
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
                re2.codcfo as codCfo,
                re2.codcor as codcor,
                re2.codtam as codtam,
                re2.dspre2 as descricaoNFe,
                re2.liqre2 as pesoLiquido,
                re2.brtre2 as pesoBruto,
                re2.refre2 as referencia,
                re2.qtfre2 as quantidadeFaturada,
                re2.qtsre2 as quantidadeSeparada,
                re2.totcst as custoTotal,
                re2.lucrol as lucroValor,
                re2.lucrop as lucroPorcentagem,
                re2.sldre2 as saldoQuantidade,
                re2.vdsre2 as descontoItemValor,
                re2.totdsc as descontoItemTotal
            from pedre2 re2
            where re2.numres = :numres
            order by re2.seqre2
        """
    )
    fun getItemsByNumres(numres: String): List<CustomerOrderItemProjection>

    @Query(
        nativeQuery = true,
        value = """
            select
                re2.codemp as codEmp,
                re2.dteres as dteres,
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
                re2.codcfo as codCfo,
                re2.codcor as codcor,
                re2.codtam as codtam,
                re2.dspre2 as descricaoNFe,
                re2.liqre2 as pesoLiquido,
                re2.brtre2 as pesoBruto,
                re2.refre2 as referencia,
                re2.qtfre2 as quantidadeFaturada,
                re2.qtsre2 as quantidadeSeparada,
                re2.totcst as custoTotal,
                re2.lucrol as lucroValor,
                re2.lucrop as lucroPorcentagem,
                re2.sldre2 as saldoQuantidade,
                re2.vdsre2 as descontoItemValor,
                re2.totdsc as descontoItemTotal
            from pedre2 re2
            where re2.codemp = :codEmp
                and re2.dteres = :dteres
                and re2.numres = :numres
            order by re2.seqre2
        """
    )
    fun getItemsByBusinessKey(codEmp: Int, dteres: java.time.LocalDate, numres: String): List<CustomerOrderItemProjection>
}
