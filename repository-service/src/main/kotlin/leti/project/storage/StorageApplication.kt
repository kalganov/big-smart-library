package leti.project.storage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class StorageApplication

fun main(args: Array<String>) {
    runApplication<StorageApplication>(*args)
}
