package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.http.MediaType
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
                    val expectedArticles = listOf(
                        Article("article x", "body article x"),
                        Article("article y", "body article y"))

                    GET("/articles"){
                        ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(expectedArticles)
                    }
                }
            }
        })
    }
