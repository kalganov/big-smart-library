package leti.project.storage.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import java.util.*

@Configuration
@Profile("!test")
class KafkaConfig(val kafkaProperties: KafkaProperties) {

    fun producerFactory(): ProducerFactory<String, String> {
        val settings = HashMap<String, Any>()
        settings[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        settings[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = kafkaProperties.producer.keySerializer
        settings[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = kafkaProperties.producer.valueSerializer
        return DefaultKafkaProducerFactory(settings)
    }

    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }

}