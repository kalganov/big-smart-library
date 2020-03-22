package leti.project.search.model

import java.io.Serializable

data class Author(
    override var id: String?,
    val firstName: String,
    val lastName: String
) : Serializable, Identifiable