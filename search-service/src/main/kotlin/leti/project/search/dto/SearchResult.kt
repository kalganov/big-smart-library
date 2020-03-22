package leti.project.search.dto

data class SearchResult<E>(
    private val totalResults: Long,
    private val results: List<E>
)