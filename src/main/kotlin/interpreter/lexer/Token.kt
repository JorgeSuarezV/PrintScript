package interpreter.lexer

data class Token(val id: Int, val type: TokenType, val location: Location, val symbol: String)

data class Location(val row: Int, val column: Int)

enum class TokenType {
    NUMBER_KEYWORD,
    STRING_KEYWORD,
    NUMBER_LITERAL,
    STRING_LITERAL,
    IDENTIFIER,
    LET_KEYWORD,
    OPERATOR_PLUS,
    OPERATOR_MINUS,
    OPERATOR_TIMES,
    OPERATOR_DIVIDE,
    DOUBLE_DOTS,
    SEMI_COMA,
    ASIGNATION_EQUALS,
    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS
}
