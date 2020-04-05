package leti.project.search.dto

import leti.project.search.model.Book

data class BookElastic(
    var id: String?,
    val customerId: String,
    val genre: String,
    val title: String,
    val language: String,
    val authors: Set<String>
) {
    constructor(book: Book) : this(
        id = book.id,
        genre = book.genre,
        title = book.title,
        language = book.language,
        customerId = book.customerId,
        authors = book.authors.asSequence()
            .map { it.firstName + ' ' + it.lastName }
            .toSet()
    )
}
