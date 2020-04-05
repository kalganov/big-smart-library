package leti.project.search.dto

import java.io.Serializable

data class SearchResult<E>(
    val totalResults: Long,
    val results: List<E>
) : Serializable