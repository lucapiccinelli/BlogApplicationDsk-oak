package com.cgm.experiments.blogapplicationdsl.integration

import com.cgm.experiments.blogapplicationdsl.connectToH2FromEnv
import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.exposed.ArticleDao
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.exposed.ArticleEntity
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.exposed.ExposedArticlesRepository
import com.cgm.experiments.blogapplicationdsl.helpers.TestHelpers
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExposedArticlesRepositoryTests {

    private lateinit var app: ConfigurableApplicationContext
    private val initialArticles = TestHelpers.articles

    @BeforeAll
    internal fun setUp() {
        app = start(RandomServerPort)

        transaction {
            SchemaUtils.create(ArticleEntity)
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

