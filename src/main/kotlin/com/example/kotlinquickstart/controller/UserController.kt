package com.example.kotlinquickstart.controller

import com.example.kotlinquickstart.domain.CreateUserRequest
import com.example.kotlinquickstart.domain.UpdateUserRequest
import com.example.kotlinquickstart.domain.UserResponse
import com.example.kotlinquickstart.domain.UserService
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
    fun findUserById(@PathVariable id: UUID): ResponseEntity<*> {
        val user = userService.findUserById(id)
        return if (user == null) {
            ResponseEntity.notFound().build<UserResponse>()
        } else {
            ResponseEntity.ok(user)
        }
    }
    
    @GetMapping
    fun findAllUsers(): ResponseEntity<List<UserResponse>> {
        val users = userService.findAllUsers()
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
