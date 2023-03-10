package interpreter.lexer

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LexerTest {

//    @Test
//    fun testLineSeparator(){
//        val mockText = getResourceAsText("mock_text.txt")
//        Assertions.assertTrue(Lexer().breakIntoLines(mockText).size == 5)
//    }
//
//    @Test
//    fun testEvaluateText() {
//        val mockText = getResourceAsText("mock_text.txt")
//        val lexer = Lexer()
//        val evaluatedText = lexer.generatePreTokensText(mockText)
//        val ssdas = lexer.filterPreTokens(evaluatedText)
//        Assertions.assertEquals(37, ssdas.size)
//    }

    private fun getResourceAsText(path: String): String {
        return this::class.java.classLoader.getResource(path)?.readText() ?: "hello"
    }

}