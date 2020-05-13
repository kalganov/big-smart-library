package leti.project.storage.feign

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    url = FeignConfig.SERVICE_URL,
    name = FeignConfig.SERVICE_FEIGN_NAME,
    contextId = "searchClient",
    configuration = [FeignConfig::class]
)
interface SearchClient {

    @DeleteMapping("/api/search/{id}")
    fun delete(@PathVariable id: String)
}