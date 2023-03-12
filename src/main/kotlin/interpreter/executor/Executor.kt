package interpreter.executor

interface Binary{
    fun evaluate(): Any
}
data class IntValue(val value: Int): Binary{
    override fun evaluate(): Any = value
}
data class StringValue(val value: String): Binary{
    override fun evaluate(): Any = value
}
data class BinaryExprecion(val left: Binary, val right: Binary, val op: Operator): Binary{
    override fun evaluate(): Any {
        val leftValue = left.evaluate()
        val rightValue = right.evaluate()
        return when {
            leftValue is String && rightValue is String -> leftValue + rightValue
            leftValue is Int && rightValue is Int -> leftValue + rightValue
            leftValue is String && rightValue is Int -> leftValue + rightValue.toString()
            leftValue is Int && rightValue is String -> leftValue.toString() + rightValue
            else -> throw IllegalArgumentException("Tipos incompatibles: $leftValue, $rightValue")
        }
    }

}

enum class Operator{
    PLUS, MINUS, PRODUCT, DIVISION
}