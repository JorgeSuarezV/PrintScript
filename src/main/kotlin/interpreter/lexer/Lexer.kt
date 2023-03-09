package interpreter.lexer

class Lexer {

    fun breakIntoLines(text: String): List<String>{
        return text.trim().split(System.lineSeparator())
    }

    fun evaluateLine(text: String): List<Pair<String, Int>> {
        return text.split("(?<=[^\\p{Alnum}])|(?=[^\\p{Alnum}])|\\s+".toRegex())
            .filter { it.isNotBlank() }
            .mapIndexed { index, word -> Pair(word, index) }
    }

    fun transformToTokens(text: String){

    }
}