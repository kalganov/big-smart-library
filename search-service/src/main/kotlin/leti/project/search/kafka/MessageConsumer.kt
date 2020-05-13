package leti.project.search.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import leti.project.search.model.Book
import leti.project.search.service.BookService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component


@Component
class MessageConsumer(
    private val objectMapper: ObjectMapper,
    private val bookService: BookService,
    private val log: Logger = LoggerFactory.getLogger(MessageConsumer::class.java)
) {

    @KafkaListener(
        topics = ["\${spring.kafka.topic}"],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun handleSingleMessage(record: ConsumerRecord<String, String?>) {
        val key: String = record.key()
        log.info(key)
        if (isBookRecord(key)) {
            val book = convertToBook(record.value())
            log.info("Indexing book: $book")
            bookService.saveOrUpdate(book)
        }
    }

    private fun isBookRecord(key: String): Boolean = key.split("|").toTypedArray()[0] == "Book"

    private fun convertToBook(json: String?): Book = objectMapper.readValue(json, Book::class.java)
}