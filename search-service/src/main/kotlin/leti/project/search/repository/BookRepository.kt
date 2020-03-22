package leti.project.search.repository

import leti.project.search.dto.BookElastic
import leti.project.search.model.Book
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Repository

@Repository
class BookRepository(
    @Value("classpath:book_mapping.json") mapping: Resource,
    @Value("classpath:book_settings.json") settings: Resource
) : ElasticRepository<Book, BookElastic>(mapping, settings) {

    override fun getIndexName(): String = Book::class.java.simpleName.toLowerCase()

    override fun convertObjToString(obj: Book): String = objectMapper.writeValueAsString(BookElastic(obj))

    override fun convertMapToObj(map: MutableMap<String, Any>): BookElastic =
        objectMapper.convertValue(map, BookElastic::class.java)
}