package com.example.kotlinquickstart.domain

import com.example.kotlinquickstart.persitence.UserEntity
import com.example.kotlinquickstart.persitence.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.util.*

private lateinit var userService: UserService
private lateinit var mockUserRepository: UserRepository

class UserServiceUnitTest : FunSpec({

    beforeEach {
        mockUserRepository = mockk<UserRepository>()
        userService = UserService(mockUserRepository)
    }

    context("createUser") {
        test("should throw exception when email already exists") {
            val request = CreateUserRequest(
                name = "John Doe",
                email = "existing@example.com"
            )
            every { mockUserRepository.existsByEmail(request.email) } returns true

            val exception = shouldThrow<IllegalArgumentException> {
                userService.createUser(request)
            }

            exception.message shouldContain "already exists"

            verify {
                mockUserRepository.existsByEmail(request.email)
                mockUserRepository.save(any()) wasNot Called
            }
        }

        test("should create user successfully when email is unique") {
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

            val result = userService.createUser(request)

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
    }

    context("updateUser") {
        test("should throw exception when user not found") {
            // Given
            val userId = UUID.randomUUID()
            val request = UpdateUserRequest(
                name = "John Smith",
                email = "john.smith@example.com"
            )

            every { mockUserRepository.findById(userId) } returns java.util.Optional.empty()

            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                userService.updateUser(userId, request)
            }

            exception.message shouldContain "not found"

            verify {
                mockUserRepository.findById(userId)
                mockUserRepository.existsByEmail(any()) wasNot Called
                mockUserRepository.save(any()) wasNot Called
            }
        }

        test("should throw exception when new email already exists") {
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
                email = "existing@example.com"
            )

            every { mockUserRepository.findById(userId) } returns Optional.of(existingUser)
            every { mockUserRepository.existsByEmail(request.email) } returns true

            val exception = shouldThrow<IllegalArgumentException> {
                userService.updateUser(userId, request)
            }

            exception.message shouldContain "already exists"
            verify {
                mockUserRepository.findById(userId)
                mockUserRepository.existsByEmail(request.email)
                mockUserRepository.save(any()) wasNot Called
            }
        }

        test("should update user successfully") {
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

            val result = userService.updateUser(userId, request)

            result.name shouldBe "John Smith"
            result.email shouldBe "john.smith@example.com"

            verify {
                mockUserRepository.findById(userId)
                mockUserRepository.existsByEmail(request.email)
                mockUserRepository.save(any())
            }
        }
    }

})
