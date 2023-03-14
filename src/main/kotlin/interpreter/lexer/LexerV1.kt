package interpreter.lexer

import java.util.LinkedList

class LexerV1 : Lexer {

    private val ignoredStrings: MutableList<IgnoredVerifierFunc> = LinkedList()
    private val tokenMap: MutableMap<TokenVerifierFunc, StringToTokenFunc> = LinkedHashMap()

    init {
        ignoredStrings.add { string, startIndex -> if (string[startIndex] == ' ') startIndex + 1 else -1 }
        ignoredStrings.add { string, startIndex -> if (checkIfStringEvaluatedFits(string, startIndex, 2) && string.substring(startIndex, startIndex + 2) == "//") string.length else -1 }

        tokenMap.put(
            {string, startIndex -> isThisString(string, startIndex, "let") },
            { id, _, location -> Pair(Token(id, TokenType.LET_KEYWORD, location, ""), location.column + 3)})
        tokenMap.put(
            {string, startIndex -> isThisString(string, startIndex, "number") },
            { id, _, location -> Pair(Token(id, TokenType.NUMBER_KEYWORD, location, ""), location.column + 6)})
        tokenMap.put(
            {string, startIndex -> isThisString(string, startIndex, "string") },
            { id, _, location -> Pair(Token(id, TokenType.STRING_KEYWORD, location, ""), location.column + 6)})
        tokenMap.put(
            { string, startIndex -> isThisString(string, startIndex, ":")},
            { id, _, location -> Pair(Token(id, TokenType.DOUBLE_DOTS, location, ""), location.column + 1)})
        tokenMap.put(
            { string, startIndex -> isThisString(string, startIndex, ";")},
            { id, _, location -> Pair(Token(id, TokenType.SEMI_COLON, location, ""), location.column + 1)})
        tokenMap.put(
            { string, startIndex -> isThisString(string, startIndex, "+")},
            { id, _, location -> Pair(Token(id, TokenType.OPERATOR_PLUS, location, ""), location.column + 1)})
        tokenMap.put(
            { string, startIndex -> isThisString(string, startIndex, "-")},
            { id, _, location -> Pair(Token(id, TokenType.OPERATOR_MINUS, location, ""), location.column + 1)})
        tokenMap.put(
            { string, startIndex -> isThisString(string, startIndex, "/")},
            { id, _, location -> Pair(Token(id, TokenType.OPERATOR_DIVIDE, location, ""), location.column + 1)})
        tokenMap.put(
            { string, startIndex -> isThisString(string, startIndex, "*")},
            { id, _, location -> Pair(Token(id, TokenType.OPERATOR_TIMES, location, ""), location.column + 1)})
        tokenMap.put(
            { string, startIndex -> isThisString(string, startIndex, "(")},
            { id, _, location -> Pair(Token(id, TokenType.LEFT_PARENTHESIS, location, ""), location.column + 1)})
        tokenMap.put(
            { string, startIndex -> isThisString(string, startIndex, ")")},
            { id, _, location -> Pair(Token(id, TokenType.RIGHT_PARENTHESIS, location, ""), location.column + 1)})
        tokenMap.put(
            { string, startIndex -> isThisString(string, startIndex, "=")},
            { id, _, location -> Pair(Token(id, TokenType.ASIGNATION_EQUALS, location, ""), location.column + 1)})
        tokenMap.put(
            { string, startIndex -> string[startIndex].isDigit() },
            { id, string, location -> Pair(Token(id, TokenType.NUMBER_LITERAL, location, cutNumberFromLine(string, location)), calculateEndOfNumber(string, location.column))})
        tokenMap.put(
            { string, startIndex -> string[startIndex].isLetter() },
            { id, string, location -> Pair(Token(id, TokenType.IDENTIFIER, location, cutIdentifierFromLine(string, location)), calculateEndOfIdentifier(string, location.column))})
        tokenMap.put(
            { string, startIndex -> string[startIndex] == '"'},
            { id, string, location -> Pair(Token(id, TokenType.STRING_LITERAL, location, curStringLitFromLine(string, location, '"')), calculateEndOfString(string, location.column, '"', location))})
        tokenMap.put(
            { string, startIndex -> string[startIndex] == '\''},
            { id, string, location -> Pair(Token(id, TokenType.STRING_LITERAL, location, curStringLitFromLine(string, location, '\'')), calculateEndOfString(string, location.column, '\'', location))})
    }

    private fun curStringLitFromLine(string: String, location: Location, stringStarter: Char) =
        string.substring(location.column, calculateEndOfString(string, location.column, stringStarter, location))

    private fun cutIdentifierFromLine(string: String, location: Location) =
        string.substring(location.column, calculateEndOfIdentifier(string, location.column))

    private fun cutNumberFromLine(string: String, location: Location) =
        string.substring(location.column, calculateEndOfNumber(string, location.column))

    private fun isThisString(string: String, startIndex: Int, target: String) =
        (checkIfStringEvaluatedFits(string, startIndex, target.length) && string.substring(startIndex, startIndex + target.length) == target)

    private fun checkIfStringEvaluatedFits(string: String, index: Int, stringEvaluatedLength: Int) = string.length >= index + stringEvaluatedLength

    override fun stringTokenizer(text: String): List<Token>{
        return generateTokens(text)

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
            val ignoredWordsResult = checkIfIgnoredString(line, i)
            if (ignoredWordsResult.first) {
                i= ignoredWordsResult.second
                continue
            }
            i = matchWord(line, i, tokens, lineNumber)
        }
    }

    private fun checkIfIgnoredString(line: String, i: Int): Pair<Boolean,Int> {
        var i1 = i
        val ignoredStringResult = isIgnoredString(line, i1)
        if (ignoredStringResult.first) {
            i1 = ignoredStrings[ignoredStringResult.second].invoke(line, i1)
            return Pair(true, i1)
        }
        return Pair(false, i)
    }

    private fun matchWord(
        line: String,
        startIndex: Int,
        tokens: MutableList<Token>,
        lineNumber: Int
    ): Int {
        var i = startIndex
        for ((key, value) in tokenMap) {
            if (key.invoke(line, i)) {
                val result = value.invoke(tokens.size, line, Location(lineNumber, i))
                tokens.add(result.first)
                i = result.second
                break
            }
        }
        if (i == startIndex) throw InvalidTokenException("Invalid character", Location(lineNumber, startIndex)) else return i
    }

    private fun isIgnoredString(line: String, i: Int): Pair<Boolean, Int> {
        for (j in ignoredStrings.indices) {
            if (ignoredStrings[j].invoke(line, i) == -1) continue
            else return Pair(true, j)
        }
        return Pair(false, 0)
    }

    private fun calculateEndOfNumber(line: String, startIndex: Int): Int {
        var passedADot = false
        for (i in startIndex + 1 until line.length){
            if ((line[i]).isDigit() || (line[i] == '.' && !passedADot)){
                if (line[i] == '.') passedADot = true
                continue
            }
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



    private fun calculateEndOfIdentifier(line: String, startIndex: Int): Int {
        for (i in startIndex + 1 until line.length){
            if (isAplhanumeric(line[i])) continue
            else return i
        }
        return line.length
    }
}

typealias IgnoredVerifierFunc = (line: String, startIndex: Int) -> Int
typealias TokenVerifierFunc = (line: String, startIndex: Int) -> Boolean
typealias StringToTokenFunc = (id: Int, line: String, location: Location) -> Pair<Token, Int>