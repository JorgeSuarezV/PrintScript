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
data class BinaryOperation(val left: Binary, val right: Binary, val op: Operator): Binary{
    override fun evaluate(): Any {
        val leftValue = left.evaluate()
        val rightValue = right.evaluate()
        return when {
            leftValue is String && rightValue is String -> operationString(leftValue, rightValue, op)
            leftValue is Int && rightValue is Int -> operation(leftValue, rightValue, op)
            leftValue is String && rightValue is Int -> operationString(leftValue, rightValue.toString(), op)
            leftValue is Int && rightValue is String -> operationString(leftValue.toString(), rightValue, op)
            else -> throw IllegalArgumentException("Tipos incompatibles: $leftValue, $rightValue")
        }
    }

    fun operation(left: Int, right: Int, op: Operator) : Int{
        return when{
            op == Operator.PLUS -> left + right
            op == Operator.MINUS -> left - right
            op == Operator.PRODUCT -> left * right
            op == Operator.DIVISION -> left / right
            else -> throw IllegalArgumentException("Error")
        }
    }

    fun operationString(left: String, right: String, op: Operator) : String{
        return when{
            op == Operator.PLUS -> left + right
            else -> throw IllegalArgumentException("Error")
        }
    }

}

enum class Operator{
    PLUS, MINUS, PRODUCT, DIVISION
}