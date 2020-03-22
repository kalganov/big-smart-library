package leti.project.storage.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import java.util.*


@EnableKafka
@Configuration
class KafkaConfiguration(private val kafkaProperties: KafkaProperties) {

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val settings = HashMap<String, Any>()
        settings[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        settings[ProducerConfig.ACKS_CONFIG] = "all"
        settings[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = "1"
        settings[ProducerConfig.COMPRESSION_TYPE_CONFIG] = "gzip"
        settings[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        settings[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        return DefaultKafkaProducerFactory(settings)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }
}