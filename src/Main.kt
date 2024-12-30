import kotlin.math.round
import kotlin.math.sqrt
import kotlin.random.Random

const val MAX_S = 1_000_000

fun Int.pow(power: Int): Int {
    var result = 1
    for (i in 1..power) {
        result *= this
    }
    return result // there is no need for me to write binary pow.
}

fun round(value: Double, digits: Int) = round(value * 10.pow(digits)) / 10.pow(digits)


class StrategyA : Strategy("a: move farther and farther to the right") {
    private var currentStep = 1
    private var moved = 0
    override fun nextMove(game: Game) {
        moved += 1
        if (moved == currentStep) {
            currentStep += 1
            moved = 0
            game.moveSnake(Direction.DOWN)
        } else {
            game.moveSnake(Direction.RIGHT)
        }
    }
}

class StrategyC : Strategy("c: randomly go down or right") {
    // works, but not clear why
    override fun nextMove(game: Game) {
        if (Random.nextInt() % 2 == 1) {
            game.moveSnake(Direction.RIGHT)
        } else {
            game.moveSnake(Direction.DOWN)
        }
    }

}


class StrategyD : Strategy("d: go right and down in order") {
    override fun nextMove(game: Game) {
        if (game.totalSteps % 2 == 1) {
            game.moveSnake(Direction.RIGHT)
        } else {
            game.moveSnake(Direction.DOWN)
        }
    }
}

class StrategyE : Strategy("e: go totally randomly") {
    override fun nextMove(game: Game) {
        game.moveSnake(Direction.ALL[Random.nextInt(4)])
    }
}

data class MovingData(var moved: Int, var currentStep: Int) {
    fun reset() {
        moved = 0
        currentStep = 1
    }
}

class StrategyF : Strategy("f: try to go sometimes right, sometimes down") {
    private val right = MovingData(0, 1)
    private val down = MovingData(0, 1)
    private var isMovingRight = true

    private fun moveInDirection(game: Game, direction: Direction, movingData: MovingData) {
        if (movingData.moved == movingData.currentStep) {
            game.moveSnake(if (direction == Direction.RIGHT) Direction.DOWN else Direction.RIGHT)
            movingData.moved = 0
            movingData.currentStep += 1
        } else {
            game.moveSnake(direction)
            movingData.moved += 1
        }
    }

    override fun nextMove(game: Game) {
        if (isMovingRight) {
            moveInDirection(game, Direction.RIGHT, right)
        } else {
            moveInDirection(game, Direction.DOWN, down)
        }
        // do we need to switch direction?
        if (right.currentStep - 1 >= down.currentStep * 2) {
            isMovingRight = false
        } else if (down.currentStep - 1 >= right.currentStep) {
            isMovingRight = true
        }
    }
}

data class Point(val x: Int, val y: Int) {
    fun down() = Point(x, y - 1)
    fun up() = Point(x, y + 1)
    fun right() = Point(x + 1, y)
    fun left() = Point(x - 1, y)
}

class StrategyG : Strategy("g: covering a hyperbola") {
    val k = 2
    var currentS = 1
    val usedCells = mutableSetOf<Point>()
    var current = Point(1, 1)
    private var isStartingFromTop = false
    private var wentFar = false

    fun isStartingFromTop() = isStartingFromTop

    fun increaseS() {
        currentS *= k
        isStartingFromTop = !isStartingFromTop
        wentFar = false
    }

    private fun move(game: Game, direction: Direction) {
        game.moveSnake(direction)
        current = when (direction) {
            Direction.UP -> current.up()
            Direction.RIGHT -> current.right()
            Direction.DOWN -> current.down()
            Direction.LEFT -> current.left()
            else -> throw Exception("Direction should be up, right, down or left!")
        }
    }

    private fun nextMoveFromTop(game: Game) {
        // check if we can move down
        val down = current.down()
        if (down.y >= 1 && !usedCells.contains(down)) {
            move(game, Direction.DOWN)
            return
        }

        // check if we can move right
        val right = current.right()
        if (right.x <= currentS) {
            move(game, Direction.RIGHT)
            return
        }

        // else we finished this S! time to increase it
        increaseS()
    }

    private fun nextMoveFromSide(game: Game) {
        // check if we can move left
        val left = current.left()
        if (left.x >= 1 && !usedCells.contains(left)) {
            move(game, Direction.LEFT)
            return
        }

        // check if we can move up
        val up = current.up()
        if (up.y <= currentS) {
            move(game, Direction.UP)
            return
        }

        // else we finished this S! time to increase it
        increaseS()
    }


    override fun nextMove(game: Game) {
        usedCells.add(current)
        if (isStartingFromTop()) {
            if (!wentFar && current.up().y <= currentS) {
                move(game, Direction.UP)
            } else {
                wentFar = true
                nextMoveFromTop(game)
            }
        } else {
            if (!wentFar && current.right().x <= currentS) {
                move(game, Direction.RIGHT)
            } else {
                wentFar = true
                nextMoveFromSide(game)
            }
        }
    }

}

fun getDivisors(x: Int): List<Int> {
    val result = mutableListOf<Int>()
    for (d in 1..sqrt(x.toDouble()).toInt() + 1) {
        if (x % d == 0) {
            result.add(d)
            if (d != x / d) result.add(x / d)
        }
    }
    return result
}


fun main() {
//    val sizes = mutableListOf(Pair(10, 10), Pair(1000, 1000), Pair(200, 200))
//
//    for ((n, m) in sizes) {
//        val g = Game(n, m)
//        val strategy = StrategyG()
//
//        strategy.test(g)
//    }

//    var maxFraction = 0.0
//    var mn = 0
//    var mm = 0
//    var tested = 0
//    while (true) {
//        val S = 1 + Random.nextInt(MAX_S)
//        val divisors = getDivisors(S)
//        val n = divisors[Random.nextInt(divisors.size)]
//        val m = S / n
//        val game = Game(n, m)
//        StrategyC().test(game, silent = true)
//        val currentFraction = (game.totalSteps * 1.0) / n / m / 35
//        if (currentFraction > maxFraction) {
//            maxFraction = currentFraction
//            mn = n
//            mm = m
//        }
//
//        tested += 1
//        if (tested % 100 == 0) {
//            println("$tested tested. current: $maxFraction, $mn, $mm")
//        }
//    }
//    println(maxFraction)
//    println("$mn $mm")

//    val tmp = Game(mn, mm)
//    StrategyF().test(tmp, silent = true)
//    println(tmp.totalSteps)
//    println((tmp.totalSteps * 1.0) / mn / mm / 35)

//    val gameE = Game(1000, 1000)
//    StrategyE.checkTime(gameE, 1e8.toInt())
}