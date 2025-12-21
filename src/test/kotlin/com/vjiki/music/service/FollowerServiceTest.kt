package com.vjiki.music.service

import com.vjiki.music.entity.AccessLevel
import com.vjiki.music.entity.AuthProvider
import com.vjiki.music.entity.User
import com.vjiki.music.entity.UserFollow
import com.vjiki.music.repository.UserFollowRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class FollowerServiceTest : DescribeSpec({

    val userFollowRepository = mockk<UserFollowRepository>()
    val followerService = FollowerServiceImpl(userFollowRepository)

    fun createTestUser(email: String, nickname: String): User {
        return User(
            email = email,
            nickname = nickname,
            accessLevel = AccessLevel.USER,
            provider = AuthProvider.LOCAL,
            isActive = true,
            isVerified = false,
            createdBy = "system",
            modifiedBy = "system"
        )
    }

    describe("getFollowersByUserId") {
        it("should return followers for user") {
            val userId = UUID.randomUUID()
            val follower1 = createTestUser("follower1@example.com", "follower1")
            val follower2 = createTestUser("follower2@example.com", "follower2")

            val followed = createTestUser("followed@example.com", "followed")
            val userFollow1 = UserFollow(
                followerId = follower1.id,
                followedId = followed.id
            )
            val userFollow2 = UserFollow(
                followerId = follower2.id,
                followedId = followed.id
            )

            every { userFollowRepository.findByFollowedId(userId) } returns listOf(userFollow1, userFollow2)

            val result = followerService.getFollowersByUserId(userId)

            result.size shouldBe 2
            verify { userFollowRepository.findByFollowedId(userId) }
        }

        it("should return empty list when no followers") {
            val userId = UUID.randomUUID()

            every { userFollowRepository.findByFollowedId(userId) } returns emptyList()

            val result = followerService.getFollowersByUserId(userId)

            result shouldBe emptyList()
        }
    }
})

