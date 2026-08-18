package br.com.vercel.emerionloadservice.repository.projection

import java.math.BigDecimal
import java.time.LocalDateTime

interface CustomerProjection {
    val id: Long
    val nomeFantasia: String
    val razaoSocial: String
    val cpfCnpj: String
    val inscricaoEstadual: String?
    val regimeTributario: String?
    val bloqueado: Int
    val dataNascimento: LocalDateTime?
    val dataCadastro: LocalDateTime?
    val dataUltimaAtualizacao: LocalDateTime?
    val email1: String?
    val email2: String?
    val website: String?
    val limiteCredito: BigDecimal?
    val observacoes: String?
    val cnae: String?
    val vendedorExternalId: Long?
    val nomeVendedor: String?
    val codigoTipoCliente: String?
    val codigoGrupoCliente: String?
    val codigoCategoriaCliente: String?
    val uf: String?
    val macroRegiao: String?
    val microRegiao: String?
    val setor: String?
}