package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.doors.inbound.routes.ArticlesHandler
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.InMemoryArticlesRepository
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.beans
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.router

fun initializeContext(): BeanDefinitionDsl = beans {
    useInmemoryRepository()
    articlesRoutes()
}

fun BeanDefinitionDsl.useInmemoryRepository() {
    bean { InMemoryArticlesRepository() }
}

fun BeanDefinitionDsl.articlesRoutes() {
    bean {
        router {
            "api".nest {
                val handler = ArticlesHandler(ref())

                GET("/articles", handler::find)
                GET("/articles/{id}", handler::find)

                accept(MediaType.APPLICATION_JSON).nest {
                    POST("/articles", handler::save)
                }
            }
        }
    }
}
