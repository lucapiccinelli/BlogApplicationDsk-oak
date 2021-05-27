package com.cgm.experiments.blogapplicationdsl.doors.inbound.routes

import com.cgm.experiments.blogapplicationdsl.domain.Repository
import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.InMemoryArticlesRepository
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import java.net.URI

class ArticlesHandler(private val repository: Repository<Article>) {

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
        ServerResponse.created(URI("${request.uri()}/${newArticle.id}"))
            .body(newArticle)
    }
}