package interpreter.lexer

class InvalidTokenException(message: String, location: Location) : Exception(message + " at " + location.row + ", " + location.column)
