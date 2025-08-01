package com.example.kotlinquickstart

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer

object PostgresTestContainer {
    val instance: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16").apply {
        withDatabaseName("testdb")
        withUsername("testuser")
        withPassword("testpass")
        start()
    }
}

class PostgresContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(context: ConfigurableApplicationContext) {
        val postgres = PostgresTestContainer.instance
        TestPropertyValues.of(
            "spring.datasource.url=${postgres.jdbcUrl}",
            "spring.datasource.username=${postgres.username}",
            "spring.datasource.password=${postgres.password}"
        ).applyTo(context.environment)
    }
}