package br.com.vercel.emerionloadservice.model

data class RequestBatch(
    val batchId: String,
    val items: List<Any>,
)
