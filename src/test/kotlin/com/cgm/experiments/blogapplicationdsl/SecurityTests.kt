package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.utils.RandomServerPort
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.beans
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityTests {

    private lateinit var app: ConfigurableApplicationContext
    private lateinit var client: TestRestTemplate
    private val port = FixedServerPort(RandomServerPort.value())

    @BeforeAll
    internal fun setUp() {

        app = start(port){
            beans {
                bean{
                    router{
                        GET("/api/test"){ ServerResponse.ok().body("test") }
                        GET("/public/test"){ ServerResponse.ok().body("test") }
                    }
                }
                enableSecurity()
            }
        }
        client = RestTemplateBuilder()
            .rootUri("http://localhost:${port.value()}")
            .run(::TestRestTemplate)
    }

    @Test
    fun `can secure endpoints under api route`(){
        val response = client.getForEntity("/api/test", String::class.java)

        response.statusCode shouldBe HttpStatus.UNAUTHORIZED
    }

    @Test
    fun `endpoint under public route are not secured`(){
        val response = client.getForEntity("/public/test", String::class.java)

        response.statusCode shouldBe HttpStatus.OK
    }
}