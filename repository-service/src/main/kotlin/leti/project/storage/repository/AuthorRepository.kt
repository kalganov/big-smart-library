package leti.project.storage.repository

import leti.project.storage.model.Author
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Repository
interface AuthorRepository : MongoRepository<Author, String> {
    fun findByFirstNameAndLastName(firstName: String, lastName: String): List<Author>
}
