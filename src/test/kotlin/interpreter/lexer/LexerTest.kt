package interpreter.lexer

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LexerTest {

    @Test
    fun testEvaluateText() {
        val mockText = getResourceAsText("mock_text.txt").toString()
        val mockTestInvalidChar = getResourceAsText("mock_text_with_invalid_char.txt").toString()
        val mockTestMalformedString = getResourceAsText("mock_text_with_unclosed_string.txt").toString()
        val lexer: Lexer = LexerV1()
        val evaluatedText: List<Token> = lexer.stringTokenizer(mockText)
        Assertions.assertEquals(37, evaluatedText.size, "number of tokens differ")
        Assertions.assertEquals(3, countTokenType(evaluatedText, TokenType.ASIGNATION_EQUALS), "number of equals")
        Assertions.assertEquals(3, countTokenType(evaluatedText, TokenType.ASIGNATION_EQUALS), "number of equals")
        Assertions.assertEquals(9, countTokenType(evaluatedText, TokenType.IDENTIFIER), "number of identifiers")
        Assertions.assertEquals(2, countTokenType(evaluatedText, TokenType.OPERATOR_PLUS), "number of plus")
        Assertions.assertEquals(1, countTokenType(evaluatedText, TokenType.OPERATOR_MINUS), "number of minus")
        Assertions.assertEquals(5, countTokenType(evaluatedText, TokenType.SEMI_COLON), "number of semi colons")
        Assertions.assertEquals("\"hello\"", evaluatedText[5].symbol, "string literal")
        Assertions.assertThrows(InvalidTokenException:: class.java){ lexer.stringTokenizer(mockTestInvalidChar) }
        Assertions.assertThrows(MalformedStringException:: class.java){ lexer.stringTokenizer(mockTestMalformedString)}
    }

    private fun countTokenType(tokens: List<Token>, tokenType: TokenType): Int{
        return tokens.filter { token: Token -> token.type == tokenType }.size
    }

    private fun getResourceAsText(path: String): String? {
        return this::class.java.classLoader.getResource(path)?.readText()
    }

}