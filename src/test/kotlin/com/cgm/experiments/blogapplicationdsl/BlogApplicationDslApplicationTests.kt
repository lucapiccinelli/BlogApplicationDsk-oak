package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.InMemoryArticlesRepository
import com.cgm.experiments.blogapplicationdsl.utils.RandomServerPort
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.*
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.beans
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlogApplicationDslApplicationTests {

    private lateinit var app: ConfigurableApplicationContext
    private lateinit var client: MockMvc
    private val mapper = jacksonObjectMapper()

    private val expectedArticles = listOf(
        Article(1,"article x", "body article x"),
        Article(2,"article y", "body article y"))

    private val inMemoryArticlesRepository = InMemoryArticlesRepository()

    @BeforeAll
    internal fun setUp() {
        app = start(RandomServerPort){
            beans {
                bean { inMemoryArticlesRepository }
                articlesRoutes()
            }
        }
        client = MockMvcBuilders
            .webAppContextSetup(app as WebApplicationContext)
            .build()
    }

    @AfterAll
    internal fun tearDown() {
        app.close()
    }

    @BeforeEach
    internal fun beforeEach() {
        inMemoryArticlesRepository.reset(expectedArticles)
    }

    @Test
    fun `can read all articles`() {

        getOk("/api/articles", expectedArticles)
    }

    @Test
    fun `can read one article`() {
        val id = 2
        val expectedArticle = expectedArticles.first { it.id == id }

        getOk("/api/articles/${id}", expectedArticle)
    }

    @Test
    fun `if the article do not exist return not found`() {
        client.get("/api/articles/9999999")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `if the article id is not a number it returns bad request`() {
        client.get("/api/articles/badRequestId")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `can create a new article`() {
        val expectedArticle = Article(0, "article z", "body of article z")

        val response = client.post("/api/articles"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(expectedArticle)
        }
        .andExpect {
            status { isCreated() }
        }.andReturn().response

        val articleStr = response.contentAsString
        val actualArticle = mapper.readValue<Article>(articleStr)

        response.getHeaderValue("Location") shouldBe "http://localhost/api/articles/${actualArticle.id}"

        getOk("/api/articles/${actualArticle.id}", actualArticle)
    }

    private fun <T> getOk(url: String, expected: T) = client.get(url)
        .andExpect {
            status { isOk() }
            content { json(mapper.writeValueAsString(expected)) }
        }

}

