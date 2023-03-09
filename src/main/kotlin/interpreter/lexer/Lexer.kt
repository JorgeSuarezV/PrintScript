package interpreter.lexer

class Lexer {
    private var actualID = 1

    fun process(text: String){
        var index = 0
        while (text.length > index){
            text.subSequence(0, text.length-1)
        }
    }
}
fun main(){
    Lexer().process("hello world!")
}