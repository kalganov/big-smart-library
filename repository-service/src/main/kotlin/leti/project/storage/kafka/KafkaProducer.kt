package leti.project.storage.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import leti.project.storage.model.Book
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaProducer(
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper
) {

    fun sendBook(book: Book) {
        val body = objectMapper.writeValueAsString(book)
        val key = book.javaClass.simpleName + "|" + book.id
        kafkaTemplate.send("storage.entity", key, body)
    }
}