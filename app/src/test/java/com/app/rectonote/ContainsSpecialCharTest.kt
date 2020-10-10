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

    @Test
    @Throws(Exception::class)
    fun testDollarSign() {
        val testStr = "$\\A"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testTilde() {
        val testStr = "~A"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testGraveAccent() {
        val testStr = "`A"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testEqualSign() {
        val testStr = "=A"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testColon() {
        val testStr = "A:"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSemiColon() {
        val testStr = "A;"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testComma() {
        val testStr = "A,"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPeriod() {
        val testStr = "A."
        assertFalse {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testVerticalLine() {
        val testStr = "A|"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPercentSign() {
        val testStr = "A%"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testLessThan() {
        val testStr = "A<"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testMoreThan() {
        val testStr = "A>"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testCompoundSample() {
        val testStr = "A{|<"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSingleQuote() {
        val testStr = "A\'"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testDoubleQuote() {
        val testStr = "A\""
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testAll() {
        val testStr = "!$%^&*\\\\+|@~=`{}\\[\\]:\";'<>?,\\/"
        assertTrue {
            testStr.containsSpecialCharacters()
        }
    }

}