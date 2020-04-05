package leti.project.search.config

import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ElasticConfig {

    @Bean
    fun restHighLevelClient(@Value("\${elasticHost}") host: String): RestHighLevelClient {
        return RestHighLevelClient(
            RestClient.builder(
                HttpHost(host, 9200, "http")
            )
        )
    }
}
