package leti.project.search

import com.fasterxml.jackson.databind.ObjectMapper
import leti.project.search.feign.StorageClient
import org.elasticsearch.client.RestHighLevelClient
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
@EmbeddedKafka(topics = ["\${spring.kafka.topic}"], controlledShutdown = true)
@DirtiesContext
@AutoConfigureMockMvc
abstract class IntegrationTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc
    @Autowired
    protected lateinit var objectMapper: ObjectMapper
    @Autowired
    protected lateinit var kafkaTemplate: KafkaTemplate<String, String>
    @MockBean
    protected lateinit var client: StorageClient
}