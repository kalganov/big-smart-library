package leti.project.storage.feign

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
@Profile("!test")
@EnableFeignClients(basePackageClasses = [SearchClient::class])
class FeignConfig {

    companion object {
        const val SERVICE_URL = "http://\${searchHost}:10002"
        const val SERVICE_FEIGN_NAME = "search-service"
    }
}