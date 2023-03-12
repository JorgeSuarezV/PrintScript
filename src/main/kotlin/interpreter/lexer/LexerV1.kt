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
        val preTokens = generatePreTokensText(text)
        return filterPreTokens(preTokens)
    }


    private fun filterPreTokens(preTokens: List<Pair<String, Location>>): List<Token>{
        return preTokens.mapIndexed { index, preToken ->
            createToken(preToken.first, index, preToken.second)
        }
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

    private fun generatePreTokensText(text: String): List<Pair<String, Location>> {
        val lines = breakIntoLines(text)
        val preTokensAndLocations = mutableListOf<Pair<String, Location>>()
        for (j in lines.indices) {
            evaluateLine(lines[j], j, preTokensAndLocations)
        }
        return preTokensAndLocations
    }


    private fun evaluateLine(
        line: String,
        lineNumber: Int,
        preTokensAndLocations: MutableList<Pair<String, Location>>
    ) {
        var i = 0
        while (i < line.length) {
            if (startLiteralString(line[i])){
                val endIndex = calculateEndOfString(line, i, line[i], Location(lineNumber, i))
                preTokensAndLocations.add(Pair(line.substring(i, endIndex), Location(lineNumber, i)))
                i = endIndex
            } else if (isAplhanumeric(line[i])) {
                val endIndex = calculateEndOfIdentifier(line, i)
                preTokensAndLocations.add(Pair(line.substring(i, endIndex), Location(lineNumber, i)))
                i = endIndex
            } else if (isNotWhiteSpace(line[i])) {
                preTokensAndLocations.add(Pair(line[i].toString(), Location(lineNumber, i)))
                i++
            } else {
                i++
                continue
            }
        }
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

    private fun isNotWhiteSpace(char: Char): Boolean {
        return !char.isWhitespace()
    }


}
typealias TokenVerifierFunc = (String) -> Boolean
typealias StringToTokenFunc = (Int, String, Location) -> Token