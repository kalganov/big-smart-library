package leti.project.storage.repository

import leti.project.storage.model.Book
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Repository
interface BookRepository : MongoRepository<Book, String>
