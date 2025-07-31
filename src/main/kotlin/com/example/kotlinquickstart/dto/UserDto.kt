package com.example.kotlinquickstart.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime
import java.util.UUID

data class CreateUserRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    
    @field:Email(message = "Email should be valid")
    @field:NotBlank(message = "Email is required")
    val email: String
)

data class UpdateUserRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    
    @field:Email(message = "Email should be valid")
    @field:NotBlank(message = "Email is required")
    val email: String
)

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
