package interpreter.lexer

class LexerV1 : Lexer {

    private val tokenMap = LinkedHashMap<TokenVerifierFunc, StringToTokenFunc>()

    init {
        tokenMap.put({ string -> string == "let" }, { id, _, location -> Token(id, TokenType.LET_KEYWORD, location, "")})
        tokenMap.put({string -> string == "string" }, { id, _, location -> Token(id, TokenType.STRING_KEYWORD, location, "")})
        tokenMap.put({string -> string == "number" }, { id, _, location -> Token(id, TokenType.NUMBER_KEYWORD, location, "")})
        tokenMap.put({ string -> string == ":" }, { id, _, location -> Token(id, TokenType.DOUBLE_DOTS, location, "")})
        tokenMap.put({ string -> string == ";" }, { id, _, location -> Token(id, TokenType.SEMI_COLON, location, "")})
        tokenMap.put({ string -> string == "+" }, { id, _, location -> Token(id, TokenType.OPERATOR_PLUS, location, "")})
        tokenMap.put({ string -> string == "-" }, { id, _, location -> Token(id, TokenType.OPERATOR_MINUS, location, "")})
        tokenMap.put({ string -> string == "/" }, { id, _, location -> Token(id, TokenType.OPERATOR_DIVIDE, location, "")})
        tokenMap.put({ string -> string == "*" }, { id, _, location -> Token(id, TokenType.OPERATOR_TIMES, location, "")})
        tokenMap.put({ string -> string == "(" }, { id, _, location -> Token(id, TokenType.LEFT_PARENTHESIS, location, "")})
        tokenMap.put({ string -> string == ")" }, { id, _, location -> Token(id, TokenType.RIGHT_PARENTHESIS, location, "")})
        tokenMap.put({ string -> string == "=" }, { id, _, location -> Token(id, TokenType.ASIGNATION_EQUALS, location, "")})
        tokenMap.put({ string -> string[0].isDigit() }, { id, string, location -> Token(id, TokenType.NUMBER_LITERAL, location, string.substring(0, string.length))})
        tokenMap.put({ string -> string[0].isLetter() }, { id, string, location -> Token(id, TokenType.IDENTIFIER, location, string.substring(0, string.length))})
        tokenMap.put({ string -> string[0] == '"' || string[0] == '\'' }, { id, string, location -> Token(id, TokenType.STRING_LITERAL, location, string.substring(0, string.length))})
    }

    override fun stringTokenizer(text: String): List<Token>{
        return generateTokens(text)

    }

    private fun createToken(
        first: String,
        i: Int,
        location: Location
    ): Token {
        for ((key, value) in tokenMap) {
            if (!key.invoke(first)) continue
            return value.invoke(i, first, location)
        }
        throw InvalidTokenException("Invalid character", location)
    }

    private fun breakIntoLines(text: String): List<String>{
        return text.trim().split(System.lineSeparator())
    }

    private fun isAplhanumeric(c: Char): Boolean {
        return c.isLetterOrDigit()
    }

    private fun generateTokens(text: String): List<Token> {
        val lines = breakIntoLines(text)
        val tokens = mutableListOf<Token>()
        for (j in lines.indices) {
            evaluateLine(lines[j], j, tokens)
        }
        return tokens
    }

    private fun evaluateLine(
        line: String,
        lineNumber: Int,
        tokens: MutableList<Token>
    ) {
        var i = 0
        while (i < line.length) {
            if (isWhiteSpace(line[i])){
                i++
            } else if (startLiteralString(line[i])){
                i = evaluateStringLiteral(line, i, lineNumber, tokens)
            } else if ((line[i]).isLetter()) {
                i = evaluateIdentifier(line, i, tokens, lineNumber)
            }else if (line[i].isDigit()){
                i = evaluateNumberLiteral(line, i, tokens, lineNumber)
            } else { //  is a symbol
                tokens.add(createToken(line[i].toString(), tokens.size, Location(lineNumber, i)))
                i++
            }
        }
    }

    private fun evaluateNumberLiteral(
        line: String,
        i: Int,
        tokens: MutableList<Token>,
        lineNumber: Int
    ): Int {
        var i1 = i
        val endIndex = calculateEndOfNumber(line, i1)
        tokens.add(createToken(line.substring(i1, endIndex), tokens.size, Location(lineNumber, i1)))
        i1 = endIndex
        return i1
    }

    private fun evaluateIdentifier(
        line: String,
        i: Int,
        tokens: MutableList<Token>,
        lineNumber: Int
    ): Int {
        var i1 = i
        val endIndex = calculateEndOfIdentifier(line, i1)
        tokens.add(createToken(line.substring(i1, endIndex), tokens.size, Location(lineNumber, i1)))
        i1 = endIndex
        return i1
    }

    private fun evaluateStringLiteral(
        line: String,
        i: Int,
        lineNumber: Int,
        tokens: MutableList<Token>
    ): Int {
        var i1 = i
        val endIndex = calculateEndOfString(line, i1, line[i1], Location(lineNumber, i1))
        tokens.add(createToken(line.substring(i1, endIndex), tokens.size, Location(lineNumber, i1)))
        i1 = endIndex
        return i1
    }

    private fun calculateEndOfNumber(line: String, startIndex: Int): Int {
        val passedADot = false
        for (i in startIndex + 1 until line.length){
            if ((line[i]).isDigit() || (line[i] == '.' && !passedADot)) continue
            else return i
        }
        return line.length
    }


    private fun calculateEndOfString(line: String, startIndex: Int, c: Char, location: Location): Int {
        for (i in startIndex + 1 until line.length){
            if (!isSameChar(c, line[i])) continue
            else return i + 1
        }
        throw MalformedStringException("String was not closed properly at ", location)
    }

    private fun isSameChar(c: Char, c1: Char): Boolean {
        return c == c1
    }

    private fun startLiteralString(c: Char): Boolean {
        return c == '"' || c == '\''
    }

    private fun calculateEndOfIdentifier(line: String, startIndex: Int): Int {
        for (i in startIndex + 1 until line.length){
            if (isAplhanumeric(line[i])) continue
            else return i
        }
        return line.length
    }

    private fun isWhiteSpace(char: Char): Boolean {
        return char.isWhitespace()
    }
}

typealias TokenVerifierFunc = (String) -> Boolean
typealias StringToTokenFunc = (Int, String, Location) -> Token