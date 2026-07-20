package br.com.vercel.emerionloadservice.repository.support

import org.springframework.data.domain.Pageable

/**
 * Firebird 1.5 does not support the ANSI `OFFSET ... FETCH` clause (nor parameterized
 * `FIRST`/`SKIP`), so Hibernate's default limit handling cannot be used for pagination.
 * This helper rewrites a native query to use Firebird's `FIRST <n> SKIP <m>` syntax with
 * literal values injected directly after the `SELECT` keyword.
 */
object FirebirdPagination {

    private val SELECT_KEYWORD = Regex("(?i)\\bselect\\b")

    fun applyFirstSkip(query: String, pageable: Pageable): String {
        val size = pageable.pageSize
        val offset = pageable.offset
        val match = SELECT_KEYWORD.find(query)
            ?: error("Query must contain a SELECT clause to apply pagination")

        val insertionPoint = match.range.last + 1
        return query.substring(0, insertionPoint) + " first $size skip $offset" + query.substring(insertionPoint)
    }
}
