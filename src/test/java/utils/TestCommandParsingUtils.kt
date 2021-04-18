package utils

import com.bot.models.Scroll
import com.bot.utils.CommandParsingUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class TestCommandParsingUtils {

    @Test
    fun testParseScrollArgsBossNameNotValid() {
        val args = "muskan 2, bheg 1, notABoss 1"
        assertThrows<IllegalArgumentException> { CommandParsingUtils.parseScrollUpdates(args) }
    }

    @Test
    fun testParseScrollArgsQuantityNotValid() {
        val args  = "muskan 1, bheg test, hexe 1"
        assertThrows<IllegalArgumentException> { CommandParsingUtils.parseScrollUpdates(args) }
    }

    @Test
    fun testParseScrollArgsValid() {
        val expected = listOf(Pair(Scroll.AWK_MUSKAN, 2),
                Pair(Scroll.PUTURUM, 1),
                Pair(Scroll.AWK_BHEG, 1))
        val args = "a_muskan 2, puturum 1, abheg 1"
        val returned = CommandParsingUtils.parseScrollUpdates(args)
        assertEquals(expected, returned)
    }
}