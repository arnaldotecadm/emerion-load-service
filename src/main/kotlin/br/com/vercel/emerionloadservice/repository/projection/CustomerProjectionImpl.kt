package br.com.vercel.emerionloadservice.repository.projection

import java.math.BigDecimal
import java.time.LocalDateTime

data class CustomerProjectionImpl(
    override val id: Long,
    override val nomeFantasia: String,
    override val razaoSocial: String,
    override val cpfCnpj: String,
    override val inscricaoEstadual: String?,
    override val regimeTributario: String?,
    override val bloqueado: Int,
    override val dataNascimento: LocalDateTime?,
    override val dataCadastro: LocalDateTime?,
    override val dataUltimaAtualizacao: LocalDateTime?,
    override val email1: String?,
    override val email2: String?,
    override val website: String?,
    override val limiteCredito: BigDecimal?,
    override val observacoes: String?,
    override val cnae: String?,
    override val vendedorExternalId: Long?,
    override val nomeVendedor: String?,
    override val codigoTipoCliente: String?,
    override val codigoGrupoCliente: String?,
    override val codigoCategoriaCliente: String?,
    override val uf: String?,
    override val macroRegiao: String?,
    override val microRegiao: String?,
    override val setor: String?
) : CustomerProjection
