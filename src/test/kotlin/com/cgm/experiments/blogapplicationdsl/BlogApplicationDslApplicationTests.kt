package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.*
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlogApplicationDslApplicationTests {

    private lateinit var app: ConfigurableApplicationContext
    private lateinit var client: MockMvc
    private val mapper = jacksonObjectMapper()

    @BeforeAll
    internal fun setUp() {
        app = start()
        client = MockMvcBuilders
            .webAppContextSetup(app as WebApplicationContext)
            .build()
    }

    @AfterAll
    internal fun tearDown() {
        app.close()
    }

    @Test
    fun `can read all articles`() {
        val expectedArticles = listOf(
            Article("article x", "body article x"),
            Article("article y", "body article y")
        )

        client.get("/articles")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json(mapper.writeValueAsString(expectedArticles)) }
            }
    }

}

