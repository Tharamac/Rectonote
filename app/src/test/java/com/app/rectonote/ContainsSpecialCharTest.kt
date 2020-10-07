package com.app.rectonote

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContainsSpecialCharTest {
    @Test
    @Throws(Exception::class)
    fun testNotAnySpecialCharacterWithUpperCase() {
        val testStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        assertFalse {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNotAnySpecialCharacterWithLowerCase() {
        val testStr = "abcdefghijklmnopqrstuvwxyz"
        assertFalse {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNotAnySpecialCharacterWithNumber() {
        val testStr = "0123456789"
        assertFalse {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testExclamationMark() {
        val testStr = "A!"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testQuestionMark() {
        val testStr = "A?"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPlusSign() {
        val testStr = "A+"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testMinusSign() {
        val testStr = "A-"
        assertFalse {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testStar() {
        val testStr = "A*"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testBackSlash() {
        val testStr = """A\"""
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSlash() {
        val testStr = """A/"""
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testParenthesesLeft() {
        val testStr = """A("""
        assertFalse {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testParenthesesRight() {
        val testStr = """A)"""
        assertFalse {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testAtSign() {
        val testStr = """A@"""
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testAndSign() {
        val testStr = """A&"""
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testUnderScore() {
        val testStr = """A_"""
        assertFalse {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testHyphen() {
        val testStr = """A^"""
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSquareBracketLeft() {
        val testStr = """A["""
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSquareBracketRight() {
        val testStr = """A]"""
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testCurlyBraceLeft() {
        val testStr = """A{"""
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testCurlyBraceRight() {
        val testStr = """}A"""
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }


}