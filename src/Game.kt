import kotlin.math.min

data class Direction(val rDir: Int, val cDir: Int) {
    companion object {
        val DOWN = Direction(1, 0)
        val RIGHT = Direction(0, 1)
        val UP = Direction(-1, 0)
        val LEFT = Direction(0, -1)

        val ALL = listOf(UP, LEFT, DOWN, RIGHT)
    }
}


class Game(
    private val n: Int,
    private val m: Int,
    private val start: Cell = Cell(0, 0, n, m),
) {
    private var current: Cell = start
    private var field = MutableList<MutableList<Int?>>(n) { MutableList(m) { null } }
    var totalSteps = 0
    private val limit = n * m * 35
    private var unCovered = n * m

    private fun getCellValue(c: Cell): Int? = field[c.row][c.col]

    private fun setCellValue(c: Cell, value: Int?) {
        when {
            (getCellValue(c) == null && value != null) -> unCovered -= 1
            (getCellValue(c) != null && value == null) -> unCovered += 1
        }
        field[c.row][c.col] = when {
            field[c.row][c.col] == null || value == null -> value
            else -> min(field[c.row][c.col]!!, value) // very dumb stuff on kotlin's side, not going to lie
        }
    }

    init {
        setCellValue(start, 0)
    }

    fun moveSnake(dir: Direction): Boolean {
        totalSteps += 1
        current = current.moveTo(dir)
        setCellValue(
            current, totalSteps
        )
        return totalSteps - 1 >= limit // returns false if move was illegal
    }

    fun isCovered(): Boolean {
        return unCovered == 0
    }

    fun isFinished(): Boolean {
        return isCovered() || totalSteps >= limit
    }

    private fun intToStringOrSub(value: Int?, sub: String = ".") = (value ?: sub).toString()

    fun printField(printSnake: Boolean = false, snakeSymbol: String = "*") {
        println()

        val maxWidth = field.maxOfOrNull { row -> row.maxOfOrNull { intToStringOrSub(it).length } ?: 0 } ?: 0

        field.forEachIndexed { i, row ->
            println(row.mapIndexed { j, el ->
                (if (current.row != i || current.col != j || !printSnake)
                    intToStringOrSub(el)
                else snakeSymbol)
                    .padStart(maxWidth)
            }.joinToString(" "))
        }

        println()
    }

    fun printGameState() {
        println(
            "${if (isFinished()) "Finished in" else "Currently"} {${totalSteps} / $limit" +
                    " (${round(totalSteps * 1.0 / limit, 2)})} steps. " +
                    "The field is ${if (isCovered()) "" else "not "}covered."
        )
    }
}