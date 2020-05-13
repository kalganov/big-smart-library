package leti.project.storage.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable


@Document(collection = "author")
data class Author(
    @Id var id: String?,
    @Field("firstName") val firstName: String,
    @Field("lastName") val lastName: String
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}
