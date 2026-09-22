package br.com.vercel.emerionloadservice.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.unit.DataSize
import tools.jackson.databind.ObjectMapper

@Service
class ChunkService(
    private val objectMapper: ObjectMapper,
    @Value($$"${ingestion-service.max-chunk-size}") private val maxChunkSize: DataSize,
) {
    fun <T> chunkByJsonSize(items: List<T>): List<List<T>> {
        val result = mutableListOf<List<T>>()
        var currentBatch = mutableListOf<T>()
        var currentSize = 0
        for (item in items) {
            val itemSize = objectMapper.writeValueAsBytes(item).size
            if (currentBatch.isNotEmpty() && currentSize + itemSize > maxChunkSize.toBytes()) {
                result += currentBatch
                currentBatch = mutableListOf()
                currentSize = 0
            }
            currentBatch += item
            currentSize += itemSize
        }
        if (currentBatch.isNotEmpty()) {
            result += currentBatch
        }
        return result
    }

    fun <T> chunkByJsonSizePerNumber(items: List<T>, chunkSize: Int): List<List<T>> {
        val result = mutableListOf<List<T>>()
        var currentBatch = mutableListOf<T>()
        var currentSize = 0
        for (item in items) {
            if (currentBatch.isNotEmpty() && currentSize + 1 > chunkSize) {
                result += currentBatch
                currentBatch = mutableListOf()
                currentSize = 0
            }
            currentBatch += item
            currentSize += 1
        }
        if (currentBatch.isNotEmpty()) {
            result += currentBatch
        }
        return result
    }
}
