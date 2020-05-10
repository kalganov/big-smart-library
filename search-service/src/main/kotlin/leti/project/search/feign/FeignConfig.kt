package leti.project.search.feign

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import feign.Feign
import feign.Logger
import feign.Logger.ErrorLogger
import feign.jackson.JacksonEncoder
import leti.project.search.model.Book
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.openfeign.support.SpringMvcContract
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class FeignConfig(val objectMapper: ObjectMapper) {

    @Bean
    fun storageClient(@Value("\${storageHost}") host: String): StorageClient {
        return Feign.builder()
            .contract(SpringMvcContract())
            .encoder(JacksonEncoder())
            .decoder { response, type ->
                objectMapper.readValue<Book>(response.body().toString())
            }
            .logger(ErrorLogger())
            .logLevel(Logger.Level.FULL)
            .target(StorageClient::class.java, "http://$host:10001")
    }
}