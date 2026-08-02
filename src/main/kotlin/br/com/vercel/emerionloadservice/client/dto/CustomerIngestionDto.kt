package br.com.vercel.emerionloadservice.client.dto

import java.math.BigDecimal
import java.time.LocalDate

data class CustomerIngestionDto(
    val externalId: Long,
    val cnpjEmpresa: String,
    val nomeFantasia: String,
    val razaoSocial: String,
    val cpfCnpj: String,
    val inscricaoEstadual: String?,
    val regimeTributario: String?,
    val bloqueado: Boolean,
    val dataNascimento: LocalDate?,
    val dataCadastro: LocalDate?,
    val dataUltimaAtualizacao: LocalDate?,
    val email1: String?,
    val email2: String?,
    val website: String?,
    val limiteCredito: BigDecimal?,
    val observacoes: String?,
    val cnae: String?,
    val vendedorExternalId: Long?,
    val nomeVendedor: String?,
    val codigoTipoCliente: String?,
    val codigoGrupoCliente: String?,
    val codigoCategoriaCliente: String?
)
