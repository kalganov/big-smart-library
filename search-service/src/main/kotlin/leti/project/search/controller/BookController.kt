package leti.project.search.controller

import leti.project.search.dto.SearchResult
import leti.project.search.feign.StorageClient
import leti.project.search.model.Book
import leti.project.search.service.BookService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/search")
@CrossOrigin(origins = ["*"])
class BookController(
    private val log: Logger = LoggerFactory.getLogger(BookController::class.java),
    private val bookService: BookService,
    private val storageClient: StorageClient
) {

    @GetMapping("/all")
    @ResponseBody
    fun findAll(): SearchResult<Book> {
        log.debug("Request to search all Books")
        val findAll = bookService.findAll()
        val books = findAll.results
            .map { bookElastic -> storageClient.findOne(bookElastic.id) }
            .filter { book -> Objects.nonNull(book) }
        return SearchResult(findAll.totalResults, books)
    }

    @GetMapping("/byTitle")
    @ResponseBody
    fun findByTitle(@RequestParam title: String): SearchResult<Book> {
        log.debug("Request to search a book with title: ")
        val findByTitle = bookService.findByTitle(title)
        val books = findByTitle.results
            .map { bookElastic -> storageClient.findOne(bookElastic.id) }
            .filter { book -> Objects.nonNull(book) }
        return SearchResult(findByTitle.totalResults, books)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) {
        log.debug("Request to delete Book : $id")
        bookService.removeBook(id)
    }
}
