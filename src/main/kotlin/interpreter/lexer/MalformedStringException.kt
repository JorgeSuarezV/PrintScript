package interpreter.lexer

class MalformedStringException(message: String, location: Location) : Exception(message + " at " +  location.row + ", " + location.column)
