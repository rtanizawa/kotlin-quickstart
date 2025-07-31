package com.example.kotlinquickstart.unit.service

import com.example.kotlinquickstart.domain.CreateUserRequest
import com.example.kotlinquickstart.domain.UpdateUserRequest
import com.example.kotlinquickstart.domain.UserService
import com.example.kotlinquickstart.persitence.UserEntity
import com.example.kotlinquickstart.persitence.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Tag
import java.time.LocalDateTime
import java.util.*

private lateinit var userService: UserService
private lateinit var mockUserRepository: UserRepository

@Tag("unit")
class UserServiceUnitTest : DescribeSpec({

    beforeEach {
        mockUserRepository = mockk<UserRepository>()
        userService = UserService(mockUserRepository)
    }


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

    describe("getAllUsers") {
        it("should return all users") {
            // Given
            val users = listOf(
                UserEntity(
                    id = UUID.randomUUID(),
                    name = "John Doe",
                    email = "john@example.com",
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                ),
                UserEntity(
                    id = UUID.randomUUID(),
                    name = "Jane Smith",
                    email = "jane@example.com",
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )

            every { mockUserRepository.findAll() } returns users

            // When
            val result = userService.getAllUsers()

            // Then
            result.size shouldBe 2
            result[0].name shouldBe "John Doe"
            result[1].name shouldBe "Jane Smith"

            verify { mockUserRepository.findAll() }
        }

        it("should return empty list when no users exist") {
            // Given
            every { mockUserRepository.findAll() } returns emptyList()

            // When
            val result = userService.getAllUsers()

            // Then
            result shouldBe emptyList()

            verify { mockUserRepository.findAll() }
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

        it("should throw exception when user not found") {
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

        it("should throw exception when new email already exists") {
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
                email = "existing@example.com"
            )

            every { mockUserRepository.findById(userId) } returns java.util.Optional.of(existingUser)
            every { mockUserRepository.existsByEmail(request.email) } returns true

            // When & Then
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
    }

    describe("deleteUser") {
        it("should delete user successfully") {
            // Given
            val userId = UUID.randomUUID()
            every { mockUserRepository.existsById(userId) } returns true
            every { mockUserRepository.deleteById(userId) } returns Unit

            // When
            userService.deleteUser(userId)

            // Then
            verify {
                mockUserRepository.existsById(userId)
                mockUserRepository.deleteById(userId)
            }
        }

        it("should throw exception when user not found") {
            // Given
            val userId = UUID.randomUUID()
            every { mockUserRepository.existsById(userId) } returns false

            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                userService.deleteUser(userId)
            }

            exception.message shouldContain "not found"

            verify {
                mockUserRepository.existsById(userId)
                mockUserRepository.deleteById(any()) wasNot Called
            }
        }
    }

})

// Property-based testing example
@Tag("unit")
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