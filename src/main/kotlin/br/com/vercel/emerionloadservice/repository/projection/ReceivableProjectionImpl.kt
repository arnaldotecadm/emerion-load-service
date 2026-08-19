package br.com.vercel.emerionloadservice.repository.projection

import java.time.Instant
import java.time.LocalDateTime

data class ReceivableProjectionImpl(
    override val codCli: Long,
    override val sequencia: String?,
    override val dataLancamento: Instant,
    override val dataReferenciaPedido: LocalDateTime?,
    override val valorOriginal: Double,
    override val valorUtilizado: Double,
    override val saldoAberto: Double,
    override val situacao: String?
) : ReceivableProjection

