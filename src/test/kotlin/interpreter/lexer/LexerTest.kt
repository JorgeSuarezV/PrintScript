package interpreter.lexer

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LexerTest {

    @Test
    fun testLineSeparator(){
        val mockText = getResourceAsText("mock_text.txt")
        Assertions.assertTrue(Lexer().breakIntoLines(mockText).size == 5)
    }

    @Test
    fun testEvaluateLine() {
        val mockText = getResourceAsText("mock_text.txt")
        val lexer = Lexer()
        val lines = lexer.breakIntoLines(mockText)
        val semiTokens = mutableListOf<Pair<String, Int>>()
        for (line in lines){
            semiTokens.addAll(lexer.evaluateLine(line))
        }
        Assertions.assertEquals(39, semiTokens.size)
    }

    private fun getResourceAsText(path: String): String {
        return this::class.java.classLoader.getResource(path)?.readText() ?: "hello"
    }

}