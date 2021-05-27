package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.utils.ServerPort
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.BeanDefinitionDsl

@SpringBootApplication
class BlogApplicationDslApplication

fun main(args: Array<String>) {
    start(FixedServerPort(8080), args)
}

fun start(port: ServerPort, args: Array<String> = emptyArray(), initializer: (() -> BeanDefinitionDsl)? = null) =
    runApplication<BlogApplicationDslApplication>(*args){
        mapOf("server.port" to port.value())
            .run(::setDefaultProperties)

        addInitializers(initializer
            ?.run { initializer() }
            ?: initializeContext())
    }
