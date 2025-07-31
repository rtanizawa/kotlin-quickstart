package com.example.kotlinquickstart.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    
    @field:NotBlank(message = "Name is required")
    @Column(nullable = false)
    val name: String,
    
    @field:Email(message = "Email should be valid")
    @field:NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    val email: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun preUpdate() {
        // This would be handled by JPA lifecycle callbacks
        // For simplicity, we're setting it in the constructor
    }
} 