package com.example.kotlinquickstart.persitence

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByEmail(email: String): Optional<UserEntity>
    fun existsByEmail(email: String): Boolean
}

@Entity
@Table(name = "users")
data class UserEntity(
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
