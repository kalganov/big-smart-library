package leti.project.search.service

import leti.project.search.model.Book
import leti.project.search.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(private val bookRepository: BookRepository) {

    fun saveOrUpdate(book: Book) = bookRepository.saveOrUpdate(book)

    fun findAll() = bookRepository.findAll()
}