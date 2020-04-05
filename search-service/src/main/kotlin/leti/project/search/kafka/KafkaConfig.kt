package leti.project.search.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import java.util.*
import kotlin.collections.HashMap


@EnableKafka
@Configuration
class KafkaConfig(val kafkaProperties: KafkaProperties) {

    @Bean
    fun singleConsumerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        val settings = HashMap<String, Any>()
        settings[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        settings[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        settings[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        settings[ConsumerConfig.GROUP_ID_CONFIG] = "search-one-by-one-consumer"
        settings[ConsumerConfig.CLIENT_ID_CONFIG] = UUID.randomUUID().toString()
        settings[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"

        factory.consumerFactory = DefaultKafkaConsumerFactory<String, String>(settings)
        return factory
    }

}