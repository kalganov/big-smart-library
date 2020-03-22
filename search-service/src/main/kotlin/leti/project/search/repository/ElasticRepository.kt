package leti.project.search.repository

import com.fasterxml.jackson.databind.ObjectMapper
import leti.project.search.dto.SearchResult
import leti.project.search.model.Identifiable
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilders.matchAllQuery
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.stream.Collectors
import javax.annotation.PostConstruct


abstract class ElasticRepository<E : Identifiable, T>(
    private val mapping: Resource,
    private val settings: Resource
) {

    @Autowired
    lateinit var client: RestHighLevelClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private val log: Logger = LoggerFactory.getLogger(ElasticRepository::class.java)

    abstract fun getIndexName(): String

    abstract fun convertObjToString(obj: E): String

    abstract fun convertMapToObj(map: MutableMap<String, Any>): T

    @PostConstruct
    fun initIndex() {
        val indexRequest = GetIndexRequest(getIndexName())

        if (!client.indices().exists(indexRequest, RequestOptions.DEFAULT)) {
            val request = CreateIndexRequest(getIndexName())
                .mapping(getJson(mapping), XContentType.JSON)
                .settings(getJson(settings), XContentType.JSON)
            client.indices().create(request, RequestOptions.DEFAULT)
        }
    }

    open fun saveOrUpdate(obj: E): E {
        val indexRequest: IndexRequest = IndexRequest(getIndexName())
            .id(java.lang.String.valueOf(obj.id))
            .source(convertObjToString(obj), XContentType.JSON)
        val index = client.index(indexRequest, RequestOptions.DEFAULT)
        log.info(index.toString())
        return obj
    }

    fun findAll(): SearchResult<T> {
        val searchRequest = SearchRequest()
            .indices(getIndexName())
            .source(SearchSourceBuilder().query(matchAllQuery()))

        val response = client.search(searchRequest, RequestOptions.DEFAULT)
        return SearchResult(response.hits.totalHits, getSearchResult(response))
    }

    private fun getSearchResult(response: SearchResponse): List<T> = response.hits.hits
        .map(SearchHit::getSourceAsMap)
        .map(::convertMapToObj)
        .toList()

    private fun getJson(resource: Resource): String =
        Files.lines(resource.file.toPath(), StandardCharsets.UTF_8).collect(Collectors.joining())
}