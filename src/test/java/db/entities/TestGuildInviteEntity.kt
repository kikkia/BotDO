package db.entities

import com.bot.db.entities.GuildEntity
import com.bot.db.entities.GuildInviteEntity
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.Instant

class TestGuildInviteEntity {

    var guild = GuildEntity("id", "test", true, emptySet())

    @Test
    fun testInviteIsExpired() {
        // Extremely expired invite
        var invite = GuildInviteEntity(0,
                "123",
                guild,
                0,
                1,
                500,
                emptyList(),
                "1234",
                Timestamp.from(Instant.ofEpochSecond(1)),
                false)
        assertTrue(invite.isExpired())
    }

    @Test
    fun testInviteNotExpired() {
        var invite = GuildInviteEntity(0,
                "123",
                guild,
                0,
                1,
                500,
                emptyList(),
                "1234",
                Timestamp.from(Instant.now()),
                false)
        assertFalse(invite.isExpired())
    }
}