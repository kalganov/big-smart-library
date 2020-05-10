package leti.project.storage.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import leti.project.storage.feign.SearchClient
import leti.project.storage.kafka.KafkaProducer
import leti.project.storage.model.Author
import leti.project.storage.model.Book
import leti.project.storage.repository.AuthorRepository
import leti.project.storage.repository.BookRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.UUID.randomUUID


@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = ["*"])
class BookController(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val objectMapper: ObjectMapper,
    @Value("classpath:books.jsonl") private val booksFile: Resource,
    private val kafkaProducer: KafkaProducer,
    private val searchClient: SearchClient,
    private val log: Logger = LoggerFactory.getLogger(BookController::class.java)
) {

    @RequestMapping(path = ["/save"], consumes = [APPLICATION_JSON_VALUE], method = [RequestMethod.POST])
    fun save(@RequestBody @Validated book: Book): Book {
        log.debug("Request to save Book : $book")
        book.id = randomUUID().toString()
        book.authors = book.authors.filter { author -> author.firstName.isNotBlank() || author.lastName.isNotBlank() }
        savePassedAuthors(book.authors)
        val savedBook = bookRepository.save(book)
        kafkaProducer.sendBook(savedBook)
        return savedBook
    }

    @PostMapping(path = ["/update"], consumes = [APPLICATION_JSON_VALUE])
    fun update(@RequestBody @Validated book: Book): Book {
        log.debug("Request to update Book : $book")
        if (book.id != null) {
            book.authors =
                book.authors.filter { author -> author.firstName.isNotBlank() || author.lastName.isNotBlank() }
            savePassedAuthors(book.authors)
            val updatedBook = bookRepository.save(book)
            kafkaProducer.sendBook(updatedBook)
            return updatedBook
        } else {
            throw IllegalArgumentException("Field id is required")
        }
    }

    @GetMapping
    fun findAll(@RequestParam page: Int, @RequestParam size: Int): Page<Book> {
        log.debug("Request to get all Books")
        return bookRepository.findAll(PageRequest.of(page, size))
    }

    @GetMapping(path = ["/fill"])
    fun fillBooks() {
        log.debug("Fill db")
        val findAll = findAll(0, 1)
        if (findAll.isEmpty) {
            val bufferReader = BufferedReader(InputStreamReader(booksFile.inputStream))
            bufferReader.use {
                while (it.ready()) {
                    val jsonBook = it.readLine()
                    log.info(jsonBook)
                    save(objectMapper.readValue(jsonBook))
                }
            }
        }
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
        searchClient.delete(id)
    }

    private fun savePassedAuthors(authors: Collection<Author>) {
        authors.stream()
            .filter { (id) -> id == null }
            .forEach { author ->
                val authorFromDB =
                    authorRepository.findByFirstNameAndLastName(author.firstName, author.lastName).stream().findAny()
                author.id = if (authorFromDB.isPresent) authorFromDB.get().id else randomUUID().toString()
            }

        authorRepository.saveAll(authors)
    }
}