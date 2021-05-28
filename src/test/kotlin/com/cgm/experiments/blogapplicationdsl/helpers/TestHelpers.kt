package com.cgm.experiments.blogapplicationdsl.helpers

import com.cgm.experiments.blogapplicationdsl.connectToDb
import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import org.springframework.context.support.BeanDefinitionDsl
import org.testcontainers.containers.PostgreSQLContainer

object TestHelpers{
    val articles = listOf(
        Article(1,"article x", "body article x"),
        Article(2,"article y", "body article y")
    )

    fun BeanDefinitionDsl.connectToPostgres(postgreSQLContainer: PostgreSQLContainer<Nothing>) {
        connectToDb(postgreSQLContainer.jdbcUrl, "org.postgresql.Driver", postgreSQLContainer.username, postgreSQLContainer.password)
    }
}

