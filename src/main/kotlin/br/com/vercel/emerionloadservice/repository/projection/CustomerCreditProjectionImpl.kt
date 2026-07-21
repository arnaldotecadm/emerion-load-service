package br.com.vercel.emerionloadservice.repository.projection

import java.time.Instant
import java.time.LocalDateTime

data class CustomerCreditProjectionImpl(
    override val codCli: Long,
    override val sequencia: String?,
    override val data: Instant,
    override val dataPedido: LocalDateTime?,
    override val valorUtilizado: Double,
    override val valorTotal: Double,
    override val saldo: Double,
    override val situacao: String?
) : CustomerCreditProjection
