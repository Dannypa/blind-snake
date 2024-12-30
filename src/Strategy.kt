abstract class Strategy(protected val name: String) {
    protected abstract fun nextMove(game: Game)

    private fun play(game: Game, log: Boolean, silent: Boolean, isEnded: (Game) -> Boolean) {
        if (!silent) println("Playing strategy '${name}'")
        if (log) {
            game.printField()
        }

        while (!isEnded(game)) {
            nextMove(game)

            if (log) {
                game.printField()
            }
        }

        if (!silent) game.printGameState()
    }

    fun test(game: Game, log: Boolean = false, silent: Boolean = false) {
        play(game, log, silent) { it.isFinished() }
    }

    fun checkTime(game: Game, limit: Int, log: Boolean = false, silent: Boolean = false) {
        play(game, log, silent) { it.totalSteps > limit }
    }
}