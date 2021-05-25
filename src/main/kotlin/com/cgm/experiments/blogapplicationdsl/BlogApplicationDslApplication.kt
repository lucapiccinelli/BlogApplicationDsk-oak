package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

@SpringBootApplication
class BlogApplicationDslApplication

fun main(args: Array<String>) {
    start(args)
}

fun start(args: Array<String> = emptyArray()) =
    runApplication<BlogApplicationDslApplication>(*args){
        addInitializers(beans {
            bean {
                router {
                    "api".nest {
                        GET("/articles", ArticlesHandler::findAll)
                        GET("/articles/{id}", ArticlesHandler::findOne)
                    }
                }
            }
        })
    }

object ArticlesHandler{
    private val articles = listOf(
        Article(1,"article x", "body article x"),
        Article(2,"article y", "body article y"))

    fun findAll(request: ServerRequest): ServerResponse =
        ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(articles)

    fun findOne(request: ServerRequest): ServerResponse = request.pathVariable("id").let { id ->
        ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(articles.find { it.id == id.toInt() }!!)
    }
}