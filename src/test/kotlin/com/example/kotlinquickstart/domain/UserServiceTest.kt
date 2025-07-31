package com.example.kotlinquickstart.domain

import com.example.kotlinquickstart.persitence.UserEntity
import com.example.kotlinquickstart.persitence.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.email
import io.kotest.property.checkAll
import io.mockk.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.UUID

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest : DescribeSpec({

    describe("UserService") {
        
        val mockUserRepository = mockk<UserRepository>()
        val userService = UserService(mockUserRepository)
        
        describe("createUser") {
            it("should create user successfully when email is unique") {
                // Given
                val request = CreateUserRequest(
                    name = "John Doe",
                    email = "john.doe@example.com"
                )
                
                val savedUser = UserEntity(
                    id = UUID.randomUUID(),
                    name = request.name,
                    email = request.email,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                
                every { mockUserRepository.existsByEmail(request.email) } returns false
                every { mockUserRepository.save(any()) } returns savedUser
                
                // When
                val result = userService.createUser(request)
                
                // Then
                result.id shouldNotBe null
                result.name shouldBe "John Doe"
                result.email shouldBe "john.doe@example.com"
                result.createdAt shouldNotBe null
                result.updatedAt shouldNotBe null
                
                verify { 
                    mockUserRepository.existsByEmail(request.email)
                    mockUserRepository.save(any())
                }
            }
            
            it("should throw exception when email already exists") {
                // Given
                val request = CreateUserRequest(
                    name = "John Doe",
                    email = "existing@example.com"
                )
                
                every { mockUserRepository.existsByEmail(request.email) } returns true
                
                // When & Then
                val exception = shouldThrow<IllegalArgumentException> {
                    userService.createUser(request)
                }
                
                exception.message shouldContain "already exists"
                
                verify { 
                    mockUserRepository.existsByEmail(request.email)
                    mockUserRepository.save(any()) wasNot Called
                }
            }
        }
        
        describe("getUserById") {
            it("should return user when found") {
                // Given
                val userId = UUID.randomUUID()
                val user = UserEntity(
                    id = userId,
                    name = "John Doe",
                    email = "john.doe@example.com",
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                
                every { mockUserRepository.findById(userId) } returns java.util.Optional.of(user)
                
                // When
                val result = userService.getUserById(userId)
                
                // Then
                result.id shouldBe userId
                result.name shouldBe "John Doe"
                
                verify { mockUserRepository.findById(userId) }
            }
            
            it("should throw exception when user not found") {
                // Given
                val userId = UUID.randomUUID()
                every { mockUserRepository.findById(userId) } returns java.util.Optional.empty()
                
                // When & Then
                val exception = shouldThrow<IllegalArgumentException> {
                    userService.getUserById(userId)
                }
                
                exception.message shouldContain "not found"
                
                verify { mockUserRepository.findById(userId) }
            }
        }
        
        describe("updateUser") {
            it("should update user successfully") {
                // Given
                val userId = UUID.randomUUID()
                val existingUser = UserEntity(
                    id = userId,
                    name = "John Doe",
                    email = "john.doe@example.com",
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                
                val request = UpdateUserRequest(
                    name = "John Smith",
                    email = "john.smith@example.com"
                )
                
                val updatedUser = existingUser.copy(
                    name = request.name,
                    email = request.email,
                    updatedAt = LocalDateTime.now()
                )
                
                every { mockUserRepository.findById(userId) } returns java.util.Optional.of(existingUser)
                every { mockUserRepository.existsByEmail(request.email) } returns false
                every { mockUserRepository.save(any()) } returns updatedUser
                
                // When
                val result = userService.updateUser(userId, request)
                
                // Then
                result.name shouldBe "John Smith"
                result.email shouldBe "john.smith@example.com"
                
                verify { 
                    mockUserRepository.findById(userId)
                    mockUserRepository.existsByEmail(request.email)
                    mockUserRepository.save(any())
                }
            }
        }
    }
})

// Property-based testing example
class UserServicePropertyTest : FunSpec({
    
    test("createUser should always return user with provided name and email") {
        checkAll(Arb.string(minSize = 1, maxSize = 100), Arb.email()) { name, email ->
            val mockUserRepository = mockk<UserRepository>()
            val userService = UserService(mockUserRepository)
            
            val request = CreateUserRequest(name = name, email = email)
            val savedUser = UserEntity(
                id = UUID.randomUUID(),
                name = name,
                email = email,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            every { mockUserRepository.existsByEmail(email) } returns false
            every { mockUserRepository.save(any()) } returns savedUser
            
            val result = userService.createUser(request)
            
            result.name shouldBe name
            result.email shouldBe email
        }
    }
}) 