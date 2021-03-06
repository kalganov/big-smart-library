package leti.project.storage.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

data class Book(
    @Id var id: String?,
    @Field("customerId") val customerId: String,
    @Field("genre") val genre: String,
    @Field("title") val title: String,
    @Field("language") val language: String,
    @DBRef @Field("authors") var authors: Collection<Author>
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}