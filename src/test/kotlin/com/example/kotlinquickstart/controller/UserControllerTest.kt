package com.example.kotlinquickstart.controller

import com.example.kotlinquickstart.domain.CreateUserRequest
import com.example.kotlinquickstart.domain.UpdateUserRequest
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserControllerTest(
    private val webApplicationContext: WebApplicationContext,
    private val objectMapper: ObjectMapper,
) : FunSpec({

    lateinit var mockMvc: MockMvc

//    companion object {
//        @Container
//        val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
//            withDatabaseName("testdb")
//            withUsername("test")
//            withPassword("test")
//        }
//
//        @JvmStatic
//        @DynamicPropertySource
//        fun properties(registry: DynamicPropertyRegistry) {
//            registry.add("spring.datasource.url", postgres::getJdbcUrl)
//            registry.add("spring.datasource.username", postgres::getUsername)
//            registry.add("spring.datasource.password", postgres::getPassword)
//        }
//    }

    beforeEach {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    test("should create user successfully") {
        val createUserRequest = CreateUserRequest(
            name = "John Doe",
            email = "john.doe@example.com"
        )

        val result = mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists())
            .andReturn()

        result.response.status shouldBe 201
    }

    test("should return 400 when creating user with invalid email") {
        val createUserRequest = CreateUserRequest(
            name = "John Doe",
            email = "invalid-email"
        )

        val result = mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest))
        )
            .andExpect(status().isBadRequest)
            .andReturn()

        result.response.status shouldBe 400
    }

    test("should return 400 when creating user with empty name") {
        val createUserRequest = CreateUserRequest(
            name = "",
            email = "john.doe@example.com"
        )

        val result = mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest))
        )
            .andExpect(status().isBadRequest)
            .andReturn()

        result.response.status shouldBe 400
    }

    test("should get all users") {
        val result = mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()

        result.response.status shouldBe 200
        result.response.contentType shouldNotBe null
    }

    test("should return 404 when getting non-existent user") {
        val nonExistentUuid = "550e8400-e29b-41d4-a716-446655440000"
        val result = mockMvc.perform(get("/api/users/$nonExistentUuid"))
            .andExpect(status().isNotFound)
            .andReturn()

        result.response.status shouldBe 404
    }

    test("should update user successfully") {
        // First create a user
        val createUserRequest = CreateUserRequest(
            name = "John Doe",
            email = "john.doe@example.com"
        )

        val createResult = mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest))
        ).andReturn()

        val userId = objectMapper.readTree(createResult.response.contentAsString).get("id").asText()

        // Then update the user
        val updateUserRequest = UpdateUserRequest(
            name = "John Smith",
            email = "john.smith@example.com"
        )

        val result = mockMvc.perform(
            put("/api/users/$userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("John Smith"))
            .andExpect(jsonPath("$.email").value("john.smith@example.com"))
            .andReturn()

        result.response.status shouldBe 200
    }
}) 