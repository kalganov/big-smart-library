package leti.project.search.repository

import com.fasterxml.jackson.databind.ObjectMapper
import leti.project.search.dto.BookElastic
import leti.project.search.dto.SearchResult
import leti.project.search.model.Book
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.cluster.health.ClusterHealthStatus
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.index.query.QueryBuilders.boolQuery
import org.elasticsearch.index.query.QueryBuilders.matchQuery
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Repository
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ConnectException
import javax.annotation.PostConstruct

@Repository
class BookRepository(
    @Value("classpath:book_mapping.json") val mapping: Resource,
    @Value("classpath:book_settings.json") val settings: Resource,
    private val client: RestHighLevelClient,
    private val objectMapper: ObjectMapper,
    private val log: Logger = LoggerFactory.getLogger(BookRepository::class.java)
) {

    @PostConstruct
    fun init() {
        checkCluster()
        initIndex()
    }

    private fun initIndex() {
        val indexRequest = GetIndexRequest(getIndexName())
        if (!client.indices().exists(indexRequest, RequestOptions.DEFAULT)) {
            val request = CreateIndexRequest(getIndexName())
                .mapping(getJson(mapping), XContentType.JSON)
                .settings(getJson(settings), XContentType.JSON)
            client.indices().create(request, RequestOptions.DEFAULT)
        }
    }

    private fun checkCluster() {
        //While we are using only one partition we are waiting YELLOW status of the cluster
        log.info("Waiting for the elastic cluster")
        val waitForYellowStatus = ClusterHealthRequest().waitForYellowStatus()
        waitForYellowStatus.timeout("50s")
        var status: ClusterHealthStatus = ClusterHealthStatus.RED
        for (i in 0..3) {
            try {
                status = client.cluster().health(waitForYellowStatus, RequestOptions.DEFAULT).status
            } catch (e: ConnectException) {
                log.info("ElasticSearch didn't response ${i + 1} times")
                Thread.sleep(10000)
            }
        }
        if (status != ClusterHealthStatus.RED) {
            log.info("The cluster has respond with status: $status")
        } else {
            throw RuntimeException("ElasticSearch didn't response")
        }
    }

    fun saveOrUpdate(obj: Book): Book {
        val indexRequest: IndexRequest = IndexRequest(getIndexName())
            .type("_doc")
            .id(java.lang.String.valueOf(obj.id))
            .source(convertObjToString(obj), XContentType.JSON)
        val index = client.index(indexRequest, RequestOptions.DEFAULT)
        log.info(index.toString())
        return obj
    }

    fun findAll(): SearchResult<BookElastic> {
        return applySearch(QueryBuilders.matchAllQuery())
    }

    fun findByTitle(title: String): SearchResult<BookElastic> {
        return applySearch(boolQuery().should(matchQuery("title", title)))
    }

    private fun applySearch(query: QueryBuilder?): SearchResult<BookElastic> {
        val searchRequest = SearchRequest()
            .indices(getIndexName())
            .source(SearchSourceBuilder().query(query))

        val response = client.search(searchRequest, RequestOptions.DEFAULT)
        return SearchResult(response.hits.totalHits, getSearchResult(response))
    }

    private fun getSearchResult(response: SearchResponse): List<BookElastic> = response.hits.hits
        .map(SearchHit::getSourceAsMap)
        .map(::convertMapToObj)
        .toList()

    private fun getJson(resource: Resource): String {
        val stringBuilder = StringBuilder()
        BufferedReader(InputStreamReader(resource.inputStream)).use {
            while (it.ready()) {
                stringBuilder.append(it.readLine())
            }
        }
        return stringBuilder.toString()
    }

    fun getIndexName(): String = Book::class.java.simpleName.toLowerCase()

    fun convertObjToString(obj: Book): String = objectMapper.writeValueAsString(BookElastic(obj))

    fun convertMapToObj(map: MutableMap<String, Any>): BookElastic =
        objectMapper.convertValue(map, BookElastic::class.java)
}