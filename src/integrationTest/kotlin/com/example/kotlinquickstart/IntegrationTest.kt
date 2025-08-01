package com.example.kotlinquickstart

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@Target(CLASS)
@Retention(RUNTIME)
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@ContextConfiguration(initializers = [PostgresContainerInitializer::class])
annotation class IntegrationTest
