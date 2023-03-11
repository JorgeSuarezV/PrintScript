package interpreter.lexer

interface Lexer {
    fun stringTokenizer(text: String): List<Token>
}