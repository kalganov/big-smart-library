package leti.project.storage.controller

import com.fasterxml.jackson.module.kotlin.readValue
import leti.project.storage.IntegrationTest
import leti.project.storage.model.Author
import leti.project.storage.model.Book
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


class BookControllerTest : IntegrationTest() {

    @Test
    @Throws(Exception::class)
    fun testRead() {
        mockMvc.perform(get("/api/books").param("page", "0").param("size", "10"))
            .andDo(print())
            .andExpect(status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun testSave() {
        val book = createBook()
        mockMvc.perform(
            post("/api/books/save")
                .content(asJsonString(book))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk)
    }

    @Test
    fun testSaveAndDelete() {
        var book = createBook()
        mockMvc.perform(
            post("/api/books/save")
                .content(asJsonString(book))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo { result ->
                book = objectMapper.readValue(result.response.contentAsString)
            }

        mockMvc.perform(delete("/api/books/${book.id}"))
            .andDo(print())
            .andExpect(status().isOk)
    }

    @Test
    fun testSaveAndUpdate() {
        var book = createBook()
        mockMvc.perform(
            post("/api/books/save")
                .content(asJsonString(book))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo { result ->
                book = objectMapper.readValue(result.response.contentAsString)
            }

        val updatedBook = Book(book.id, "newCustomerId", book.genre, book.title, book.language, book.authors)

        mockMvc.perform(
            post("/api/books/update")
                .content(asJsonString(updatedBook))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk)
    }

    @Test
    fun testSaveAndFindOne() {
        var book = createBook()
        mockMvc.perform(
            post("/api/books/save")
                .content(asJsonString(book))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo { result ->
                book = objectMapper.readValue(result.response.contentAsString)
            }

        mockMvc.perform(get("/api/books/${book.id}"))
            .andDo(print())
            .andExpect(status().isOk)
    }

    private fun createBook(): Book {
        val authors = HashSet<Author>()
        authors.add(Author(null, "George", "R. R. Martin"))
        return Book(null, "customerId", "fantasy", "A Game of Thrones", "eng", authors)
    }

    fun asJsonString(book: Book): String {
        return try {
            objectMapper.writeValueAsString(book)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}