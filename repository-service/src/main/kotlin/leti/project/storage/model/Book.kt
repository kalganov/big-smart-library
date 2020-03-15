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
    @DBRef @Field("authors") val authors: HashSet<Author>
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Book

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}