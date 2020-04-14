package leti.project.search.controller

import leti.project.search.dto.BookElastic
import leti.project.search.dto.SearchResult
import leti.project.search.service.BookService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/search")
@CrossOrigin(origins = ["*"])
class BookController(
    private val log: Logger = LoggerFactory.getLogger(BookController::class.java),
    private val bookService: BookService
) {

    @GetMapping("/all")
    @ResponseBody
    fun findAll(): SearchResult<BookElastic> {
        log.debug("Request to search all Books")
        return bookService.findAll()
    }

    @GetMapping("/byTitle")
    @ResponseBody
    fun findByTitle(@RequestParam title: String): SearchResult<BookElastic> {
        log.debug("Request to search a book with title: ")
        return bookService.findByTitle(title)
    }
}
