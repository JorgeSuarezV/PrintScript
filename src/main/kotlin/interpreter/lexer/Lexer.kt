package interpreter.lexer

class Lexer {

    fun StringTokenizer(text: String): List<Token>{
        val preTokens = generatePreTokensText(text)
        return filterPreTokens(preTokens)
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
            evaluateLine(lines, j, preTokensAndLocations)
        }
        return preTokensAndLocations
    }

    private fun filterPreTokens(preTokens: List<Pair<String, Location>>): List<Token>{
        val tokens = mutableListOf<Token>()

        for (i in preTokens.indices){
            val first = preTokens[i].first
            if (first == "let") {
                tokens.add(Token(i, TokenType.LET_KEYWORD, preTokens[i].second, ""))
            } else if (first == "string") {
                tokens.add(Token(i, TokenType.STRING_KEYWORD, preTokens[i].second, ""))
            } else if (first == "number") {
                tokens.add(Token(i, TokenType.NUMBER_KEYWORD, preTokens[i].second, ""))
            } else if (first == ":") {
                tokens.add(Token(i, TokenType.DOUBLE_DOTS, preTokens[i].second, ""))
            } else if (first == ";") {
                tokens.add(Token(i, TokenType.SEMI_COMA, preTokens[i].second, ""))
            } else if (first == "+") {
                tokens.add(Token(i, TokenType.OPERATOR_PLUS, preTokens[i].second, ""))
            } else if (first == "-") {
                tokens.add(Token(i, TokenType.OPERATOR_MINUS, preTokens[i].second, ""))
            } else if (first == "/") {
                tokens.add(Token(i, TokenType.OPERATOR_DIVIDE, preTokens[i].second, ""))
            } else if (first == "*") {
                tokens.add(Token(i, TokenType.OPERATOR_TIMES, preTokens[i].second, ""))
            } else if (first == "(") {
                tokens.add(Token(i, TokenType.LEFT_PARENTHESIS, preTokens[i].second, ""))
            } else if (first == ")") {
                tokens.add(Token(i, TokenType.RIGHT_PARENTHESIS, preTokens[i].second, ""))
            } else if (first == "=") {
                tokens.add(Token(i, TokenType.ASIGNATION_EQUALS, preTokens[i].second, ""))
            } else if (first[0].isDigit()) {
                tokens.add(Token(i, TokenType.NUMBER_LITERAL, preTokens[i].second, first.substring(0, first.length)))
            } else if (first[0].isLetter()){
                tokens.add(Token(i, TokenType.IDENTIFIER, preTokens[i].second, first.substring(0, first.length)))
            } else if (first[0] == '"' || first[0] == '\'') {
                tokens.add(Token(i, TokenType.STRING_LITERAL, preTokens[i].second, first.substring(0, first.length)))
            }else throw InvalidTokenException("Invalid character", preTokens[i].second)
        }
        return tokens
    }


    private fun evaluateLine(
        lines: List<String>,
        j: Int,
        preTokensAndLocations: MutableList<Pair<String, Location>>
    ) {
        var i = 0
        while (i < lines[j].length) {
            if (startLiteralString(lines[j][i])){
                val endIndex = calculateEndOfString(lines[j], i, lines[j][i])
                if (endIndex == -1) throw MalformedStringException("String was not closed properly at ", Location(j, i))
                else {
                    i = extractString(preTokensAndLocations, lines, j, i, endIndex)
                }
            }
            if (isAplhanumeric(lines[j][i])) {
                val endIndex = calculateEndOfIdentifier(lines[j], i)
                i = extractString(preTokensAndLocations, lines, j, i, endIndex)
            } else if (isNotWhiteSpace(lines[j][i])) {
                preTokensAndLocations.add(Pair(lines[j][i].toString(), Location(j, i)))
                i++
            } else {
                i++
                continue
            }
        }
    }

    private fun extractString(
        preTokensAndLocations: MutableList<Pair<String, Location>>,
        lines: List<String>,
        j: Int,
        i: Int,
        endIndex: Int
    ): Int {
        var i1 = i
        preTokensAndLocations.add(Pair(lines[j].substring(i1, endIndex), Location(j, i1)))
        i1 = endIndex
        return i1
    }

    private fun calculateEndOfString(line:String, startIndex: Int, c: Char): Int {
        for (i in startIndex + 1 until line.length){
            if (!isSameChar(c, line[i])) continue
            else return i + 1
        }
        return -1
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