package leti.project.search.feign

import leti.project.search.model.Book
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

interface StorageClient {

    @GetMapping("/api/books/{id}")
    fun findOne(@PathVariable id: String?): Book
}