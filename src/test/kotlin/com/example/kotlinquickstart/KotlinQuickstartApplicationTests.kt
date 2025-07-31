package com.example.kotlinquickstart

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class KotlinQuickstartApplicationTests : FunSpec({

    test("Spring context should load successfully") {
        // This test will pass if the Spring context loads without errors
        // The @SpringBootTest annotation ensures the full application context is loaded
        true shouldNotBe false
    }
}) 