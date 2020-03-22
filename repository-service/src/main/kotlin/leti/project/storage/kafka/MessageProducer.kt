package leti.project.storage.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import leti.project.storage.model.Book

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class MessageProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    fun sendMessage(book: Book) {
        val body = objectMapper.writeValueAsString(book)
        val messageKey = book.javaClass.simpleName + "|" + book.id
        kafkaTemplate.send("storage.entity", messageKey, body)
    }
}