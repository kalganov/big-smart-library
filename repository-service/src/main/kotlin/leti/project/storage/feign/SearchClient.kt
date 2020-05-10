package leti.project.storage.feign

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable

interface SearchClient {

    @DeleteMapping("/api/search/{id}")
    fun delete(@PathVariable id: String)
}