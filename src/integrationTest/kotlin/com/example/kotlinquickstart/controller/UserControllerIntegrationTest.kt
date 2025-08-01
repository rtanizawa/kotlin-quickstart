package com.example.kotlinquickstart.controller

import com.example.kotlinquickstart.IntegrationTest
import com.example.kotlinquickstart.domain.CreateUserRequest
import com.example.kotlinquickstart.domain.UpdateUserRequest
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@IntegrationTest
class UserControllerIntegrationTest(
    private val webApplicationContext: WebApplicationContext,
    private val objectMapper: ObjectMapper
) : FunSpec({

    lateinit var mockMvc: MockMvc

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
            name = "John Smith",
            email = "john.smith@example.com"
        )

        val createResult = mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest))
        ).andReturn()

        val userId = objectMapper.readTree(createResult.response.contentAsString).get("id").asText()

        // Then update the user
        val updateUserRequest = UpdateUserRequest(
            name = "Joe Smart",
            email = "joe.smart@example.com"
        )

        val result = mockMvc.perform(
            put("/api/users/$userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Joe Smart"))
            .andExpect(jsonPath("$.email").value("joe.smart@example.com"))
            .andReturn()

        result.response.status shouldBe 200
    }
})