package com.cgm.experiments.blogapplicationdsl.integration

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.exposed.ArticleDao
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.exposed.ArticleEntity
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.exposed.ExposedArticlesRepository
import com.cgm.experiments.blogapplicationdsl.enableLiquibase
import com.cgm.experiments.blogapplicationdsl.helpers.MyPostgresContainer
import com.cgm.experiments.blogapplicationdsl.helpers.TestHelpers
import com.cgm.experiments.blogapplicationdsl.helpers.TestHelpers.connectToPostgres
import com.cgm.experiments.blogapplicationdsl.start
import com.cgm.experiments.blogapplicationdsl.utils.RandomServerPort
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.beans
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class ExposedArticlesRepositoryTests {

    private lateinit var app: ConfigurableApplicationContext
    private val initialArticles = TestHelpers.articles

    companion object{
        @Container
        val container = MyPostgresContainer.container
    }

    @BeforeAll
    internal fun setUp() {
        app = start(RandomServerPort){
            beans {
                connectToPostgres(container)
                enableLiquibase("classpath:/liquibase/db-changelog-master.xml")
            }
        }
    }

    @AfterAll
    internal fun tearDown() {
        app.close()
    }

    @Test
    fun `test getAll from repo`() {
        withExpected { expectedArticles ->
            ExposedArticlesRepository().getAll() shouldBe expectedArticles
        }
    }

    private fun withExpected(test: (articles: List<Article>) -> Unit): Unit{
        transaction {
            initialArticles.map { ArticleDao.new {
                title = it.title
                body = it.body
            } }
            .map { Article(it.id.value, it.title, it.body) }
            .run(test)
        }
    }

}

