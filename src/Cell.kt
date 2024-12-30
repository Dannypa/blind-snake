import kotlin.random.Random

data class Cell(val row: Int, val col: Int, val n: Int, val m: Int) {
    companion object {
        fun getRandomCell(n: Int, m: Int): Cell {
            return Cell(Random.nextInt(n), Random.nextInt(m), n, m)
        }
    }

    fun moveTo(dir: Direction): Cell {
        return Cell((row + dir.rDir + n) % n, (col + dir.cDir + m) % m, n, m)
    }
}