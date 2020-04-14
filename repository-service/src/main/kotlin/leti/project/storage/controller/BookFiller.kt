package leti.project.storage.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.annotation.PostConstruct

@Component
class BookFiller(
    private val objectMapper: ObjectMapper,
    @Value("classpath:books.jsonl") private val booksFile: Resource,
    private val bookController: BookController,
    private val log: Logger = LoggerFactory.getLogger(BookController::class.java)
) {

    @PostConstruct
    fun initBooks() {
        val findAll = bookController.findAll(0, 1)
        if (findAll.isEmpty) {
            val bufferReader = BufferedReader(InputStreamReader(booksFile.inputStream))
            bufferReader.use {
                while (it.ready()) {
                    val jsonBook = it.readLine()
                    log.info(jsonBook)
                    bookController.save(objectMapper.readValue(jsonBook))
                }
            }
        }
    }
}