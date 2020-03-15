package leti.project.storage.controller

import leti.project.storage.model.Author
import leti.project.storage.model.Book
import leti.project.storage.repository.AuthorRepository
import leti.project.storage.repository.BookRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.UUID.randomUUID


@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val log: Logger = LoggerFactory.getLogger(BookController::class.java)
) {

    @RequestMapping(path = ["/save"], consumes = [APPLICATION_JSON_VALUE], method = [RequestMethod.POST])
    fun save(@RequestBody @Validated book: Book): Book {
        log.debug("Request to save Book : $book")
        book.id = randomUUID().toString()
        savePassedAuthors(book.authors)
        return bookRepository.save(book)
    }

    @PostMapping(path = ["/update"], consumes = [APPLICATION_JSON_VALUE])
    fun update(@RequestBody @Validated book: Book): Book {
        log.debug("Request to update Book : $book")
        if (book.id != null) {
            return bookRepository.save(book)
        } else {
            throw IllegalArgumentException("Field id is required")
        }
    }

    @GetMapping
    fun findAll(@RequestParam page: Int, @RequestParam size: Int): Page<Book> {
        log.debug("Request to get all Books")
        return bookRepository.findAll(PageRequest.of(page, size))
    }

    @GetMapping("/{id}")
    fun findOne(@PathVariable id: String): Optional<Book> {
        log.debug("Request to get Book : $id")
        return bookRepository.findById(id)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) {
        log.debug("Request to delete Book : $id")
        bookRepository.deleteById(id)
    }

    private fun savePassedAuthors(authors: Collection<Author>) {
        authors.stream()
            .filter { (id) -> id == null }
            .forEach { author ->
                author.id = randomUUID().toString()
            }
        authorRepository.saveAll(authors)
    }
}