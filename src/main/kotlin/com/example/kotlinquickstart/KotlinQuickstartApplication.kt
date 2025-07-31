package com.example.kotlinquickstart

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinQuickstartApplication

fun main(args: Array<String>) {
    runApplication<KotlinQuickstartApplication>(*args)
} 