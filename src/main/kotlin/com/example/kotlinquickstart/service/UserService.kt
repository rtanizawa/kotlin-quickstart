package com.example.kotlinquickstart.service

import com.example.kotlinquickstart.dto.CreateUserRequest
import com.example.kotlinquickstart.dto.UpdateUserRequest
import com.example.kotlinquickstart.dto.UserResponse
import com.example.kotlinquickstart.entity.User
import com.example.kotlinquickstart.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository
) {
    
    fun createUser(request: CreateUserRequest): UserResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("User with email ${request.email} already exists")
        }
        
        val user = User(
            name = request.name,
            email = request.email
        )
        
        val savedUser = userRepository.save(user)
        return savedUser.toUserResponse()
    }
    
    @Transactional(readOnly = true)
    fun getUserById(id: UUID): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User with id $id not found") }
        return user.toUserResponse()
    }
    
    @Transactional(readOnly = true)
    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { it.toUserResponse() }
    }
    
    fun updateUser(id: UUID, request: UpdateUserRequest): UserResponse {
        val existingUser = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User with id $id not found") }
        
        // Check if email is being changed and if it already exists
        if (request.email != existingUser.email && userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("User with email ${request.email} already exists")
        }
        
        val updatedUser = existingUser.copy(
            name = request.name,
            email = request.email,
            updatedAt = LocalDateTime.now()
        )
        
        val savedUser = userRepository.save(updatedUser)
        return savedUser.toUserResponse()
    }
    
    fun deleteUser(id: UUID) {
        if (!userRepository.existsById(id)) {
            throw IllegalArgumentException("User with id $id not found")
        }
        userRepository.deleteById(id)
    }
    
    private fun User.toUserResponse(): UserResponse {
        return UserResponse(
            id = this.id!!,
            name = this.name,
            email = this.email,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
} 