package com.example.kotlinquickstart.controller

import com.example.kotlinquickstart.dto.CreateUserRequest
import com.example.kotlinquickstart.dto.UpdateUserRequest
import com.example.kotlinquickstart.dto.UserResponse
import com.example.kotlinquickstart.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    
    @PostMapping
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        val user = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }
    
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<UserResponse> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(user)
    }
    
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserResponse>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }
    
    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val user = userService.updateUser(id, request)
        return ResponseEntity.ok(user)
    }
    
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Unit> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}
