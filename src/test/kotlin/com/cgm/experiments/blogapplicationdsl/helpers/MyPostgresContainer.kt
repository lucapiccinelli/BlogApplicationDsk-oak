package com.cgm.experiments.blogapplicationdsl.helpers

import org.testcontainers.containers.PostgreSQLContainer

object MyPostgresContainer{
    val container by lazy {
        PostgreSQLContainer<Nothing>("postgres:latest").apply {
            withDatabaseName("test")
            withUsername("sa")
            withPassword("sa")
            withExposedPorts(5432)
        }
    }
}