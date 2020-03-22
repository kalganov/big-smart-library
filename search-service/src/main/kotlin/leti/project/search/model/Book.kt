package leti.project.search.model

import java.io.Serializable

data class Book(
    override var id: String?,
    val customerId: String,
    val genre: String,
    val title: String,
    val language: String,
    val authors: MutableSet<Author> = mutableSetOf()
) : Serializable, Identifiable

