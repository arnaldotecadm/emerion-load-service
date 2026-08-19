package br.com.vercel.emerionloadservice.model

data class SendAllResult(
    val totalSent: Int,
    val totalErrors: Int,
    val totalPages: Int
)
