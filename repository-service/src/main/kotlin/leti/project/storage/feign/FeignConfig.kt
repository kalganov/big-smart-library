package leti.project.storage.feign

import feign.Feign
import feign.Logger
import feign.Logger.ErrorLogger
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.openfeign.support.SpringMvcContract
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class FeignConfig {

    @Bean
    fun searchClient(@Value("\${searchHost}") host: String): SearchClient {
        return Feign.builder()
            .contract(SpringMvcContract())
            .encoder(JacksonEncoder())
            .decoder(JacksonDecoder())
            .logger(ErrorLogger())
            .logLevel(Logger.Level.FULL)
            .target(SearchClient::class.java, "http://$host:10002")
    }
}