package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.router
import java.net.URI

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

                        accept(MediaType.APPLICATION_JSON).nest {
                            POST("/articles", ArticlesHandler::save)
                        }
                    }
                }
            }
        })
    }

object ArticlesHandler {

    private val repository = ArticlesRepository()

    fun find(request: ServerRequest): ServerResponse = (request.inPath("id")
        ?.run(::getOne)
        ?: okResponse(repository.getAll()))

    private fun getOne(id: String) = (id.toIntOrNull()?.let { intId ->
        repository.getOne(intId)
            ?.run(::okResponse)
            ?: ServerResponse.notFound().build()
        }
        ?: ServerResponse.badRequest().build())

    private fun okResponse(any: Any): ServerResponse = ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(any)

    private fun ServerRequest.inPath(name: String): String? = try {
        pathVariable(name)
    }catch (ex: IllegalArgumentException){
        null
    }

    fun save(request: ServerRequest) = request.body<Article>().let { article ->
        val newArticle = repository.save(article)
        ServerResponse
            .created(URI("${request.uri()}/${newArticle.id}"))
            .body(newArticle)
    }
}

class ArticlesRepository {
    private val articles = mutableListOf(
        Article(1,"article x", "body article x"),
        Article(2,"article y", "body article y"))

    fun getAll(): List<Article> = articles

    fun getOne(id: Int): Article? = articles.firstOrNull { it.id == id }
    fun save(article: Article): Article {
        val maxId: Int = articles.maxByOrNull { it.id }?.id ?: 0
        return article.copy(id = maxId + 1).apply {
            articles.add(this)
        }
    }

    fun reset() = articles.clear()
}

