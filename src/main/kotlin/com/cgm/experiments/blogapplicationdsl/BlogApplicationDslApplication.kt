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
                        GET("/articles", ArticlesHandler::find)
                        GET("/articles/{id}", ArticlesHandler::find)
                    }
                }
            }
        })
    }

object ArticlesHandler{
    private val articles = listOf(
        Article(1,"article x", "body article x"),
        Article(2,"article y", "body article y"))

    fun find(request: ServerRequest): ServerResponse = (request.inPath("id")
        ?.run(::getOne)
        ?: okResponse(articles))

    private fun getOne(id: String) = (id.toIntOrNull()?.let { intId ->
        findOne(intId)
            ?.run(::okResponse)
            ?: ServerResponse.notFound().build()
        }
        ?: ServerResponse.badRequest().build())

    private fun findOne(id: Int): Article? = articles.firstOrNull { it.id == id }

    private fun okResponse(any: Any): ServerResponse = ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(any)

    private fun ServerRequest.inPath(name: String): String? = try {
        pathVariable(name)
    }catch (ex: IllegalArgumentException){
        null
    }
}

