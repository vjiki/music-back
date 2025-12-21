# Testing Strategy

## Overview

This project uses **Kotlin-native testing frameworks** with a clear separation between unit tests and integration tests.

## Testing Stack

- **Kotest** - Kotlin-native testing framework (replaces JUnit)
- **MockK** - Kotlin-native mocking framework (replaces Mockito)
- **Spring Boot Test** - For integration tests only
- **Testcontainers** - For database integration tests

## Test Structure

### Unit Tests (`*Test.kt`)

**Purpose:** Test individual components in isolation

**Characteristics:**
- ✅ Use **Kotest** + **MockK** only
- ✅ **NO Spring Boot Test** annotations
- ✅ Fast execution (no Spring context loading)
- ✅ Mock all dependencies
- ✅ Test business logic in isolation

**Example:**
```kotlin
class UserServiceTest : DescribeSpec({
    val userRepository = mockk<UserRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val userService = UserServiceImpl(userRepository, passwordEncoder)

    describe("getUserById") {
        it("should return user when found") {
            // Test implementation
        }
    }
})
```

**Files:**
- `UserServiceTest.kt`
- `PlaylistServiceTest.kt`
- `ChatServiceTest.kt`
- `MessageServiceTest.kt`
- `SongMapperTest.kt`
- etc.

### Integration Tests (`*IntegrationTest.kt`)

**Purpose:** Test components with real Spring context and database

**Characteristics:**
- ✅ Use **Spring Boot Test** annotations
- ✅ Use **Kotest** for test structure
- ✅ Use **Testcontainers** for database
- ✅ Load real Spring application context
- ✅ Test with real dependencies

**Spring Boot Test Annotations:**
- `@SpringBootTest` - Full application context
- `@WebMvcTest` - Web layer only
- `@DataJpaTest` - Data layer only
- `@AutoConfigureMockMvc` - For web testing
- `@Transactional` - For test isolation

**Example:**
```kotlin
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
open class AuthControllerIntegrationTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)
    
    @Autowired
    lateinit var mockMvc: MockMvc
    
    @Autowired
    lateinit var userRepository: UserRepository
    
    init {
        describe("AuthController Integration Tests") {
            it("should authenticate user with valid credentials") {
                // Test implementation
            }
        }
    }
}
```

**Files:**
- `AuthControllerIntegrationTest.kt`
- `UserControllerIntegrationTest.kt`
- `PlaylistControllerIntegrationTest.kt`
- `UserServiceIntegrationTest.kt`
- `PlaylistServiceIntegrationTest.kt`
- `UserRepositoryIntegrationTest.kt`
- etc.

## Best Practices

### ✅ DO

1. **Unit Tests:**
   - Mock all external dependencies
   - Test one thing at a time
   - Keep tests fast (< 1 second each)
   - Use descriptive test names

2. **Integration Tests:**
   - Use `@Transactional` for database isolation
   - Use Testcontainers for real database testing
   - Test complete workflows
   - Clean up test data

### ❌ DON'T

1. **Unit Tests:**
   - Don't use `@SpringBootTest` or other Spring annotations
   - Don't load Spring context
   - Don't use real database connections
   - Don't test multiple components together

2. **Integration Tests:**
   - Don't mock everything (test real interactions)
   - Don't skip database setup
   - Don't forget to clean up test data

## Test Naming Convention

- **Unit Tests:** `*Test.kt` (e.g., `UserServiceTest.kt`)
- **Integration Tests:** `*IntegrationTest.kt` (e.g., `UserServiceIntegrationTest.kt`)

## Running Tests

```bash
# Run all tests
./gradlew test

# Run only unit tests
./gradlew test --tests "*Test"

# Run only integration tests
./gradlew test --tests "*IntegrationTest"

# Run specific test class
./gradlew test --tests "*UserServiceTest"
```

## Dependencies

All testing dependencies are in `build.gradle.kts`:

```kotlin
testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
testImplementation("io.kotest:kotest-assertions-core:5.9.1")
testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
testImplementation("io.mockk:mockk:1.14.6")
testImplementation("org.springframework.boot:spring-boot-starter-test")
testImplementation("org.testcontainers:testcontainers:1.19.8")
```

## Summary

- **Unit Tests** = Kotest + MockK (no Spring)
- **Integration Tests** = Kotest + Spring Boot Test + Testcontainers

This gives you:
- ✅ Fast unit tests
- ✅ Real integration testing
- ✅ Kotlin-native stack
- ✅ Clear separation of concerns

