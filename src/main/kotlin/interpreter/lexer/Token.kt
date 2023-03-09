package interpreter.lexer

data class Token(val id: Long, val type: TokenType, val line: Line, val symbol: String)

data class Line(val row: Long, val column: Long)

enum class TokenType {
    NUMBER,
    STRING,
    IDENTIFIER,
    KEYWORD_LET,
    OPERATOR_PLUS,
    OPERATOR_MINUS,
    OPERATOR_TIMES,
    OPERATOR_DIVIDE,
    DOUBLE_DOTS,
    SEMI_COMA,
    ASIGNATION_EQUALS,
    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS,
    QUOTATION_MARKS
}
