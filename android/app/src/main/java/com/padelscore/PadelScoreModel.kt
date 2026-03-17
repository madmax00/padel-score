package com.padelscore

data class GameScore(val myPoints: Int = 0, val oppPoints: Int = 0)
data class SetScore(val myGames: Int = 0, val oppGames: Int = 0)

data class PadelMatch(
    val sets: List<SetScore> = emptyList(),
    val currentSet: SetScore = SetScore(),
    val currentGame: GameScore = GameScore(),
    val isTiebreak: Boolean = false,
    val history: List<PadelMatch> = emptyList()
) {
    val mySets: Int get() = sets.count { it.myGames > it.oppGames }
    val oppSets: Int get() = sets.count { it.oppGames > it.myGames }

    fun displayPoints(): Pair<String, String> {
        if (isTiebreak) return currentGame.myPoints.toString() to currentGame.oppPoints.toString()
        return pointLabel(currentGame.myPoints, currentGame.oppPoints) to
               pointLabel(currentGame.oppPoints, currentGame.myPoints)
    }

    private fun pointLabel(mine: Int, opp: Int): String = when {
        mine == 3 && opp == 3 -> "40"
        mine == 4 && opp == 3 -> "AD"
        mine == 3 && opp == 4 -> "40"
        else -> listOf("0", "15", "30", "40")[mine.coerceAtMost(3)]
    }
}

object ScoreEngine {
    fun addPoint(match: PadelMatch, isMyPoint: Boolean): PadelMatch {
        val saved = match.copy(history = emptyList())
        val updated = processPoint(match, isMyPoint)
        return updated.copy(history = match.history + saved)
    }

    fun undo(match: PadelMatch): PadelMatch {
        return match.history.lastOrNull() ?: match
    }

    private fun processPoint(match: PadelMatch, isMyPoint: Boolean): PadelMatch {
        val g = match.currentGame
        val newGame = if (isMyPoint) g.copy(myPoints = g.myPoints + 1)
                      else g.copy(oppPoints = g.oppPoints + 1)

        return if (match.isTiebreak) {
            processTiebreakPoint(match, newGame)
        } else {
            processGamePoint(match, newGame)
        }
    }

    private fun processGamePoint(match: PadelMatch, g: GameScore): PadelMatch {
        val myWins = g.myPoints >= 4 && g.myPoints - g.oppPoints >= 2
        val oppWins = g.oppPoints >= 4 && g.oppPoints - g.myPoints >= 2
        return when {
            myWins -> winGame(match, isMyGame = true)
            oppWins -> winGame(match, isMyGame = false)
            else -> match.copy(currentGame = g)
        }
    }

    private fun processTiebreakPoint(match: PadelMatch, g: GameScore): PadelMatch {
        val myWins = g.myPoints >= 7 && g.myPoints - g.oppPoints >= 2
        val oppWins = g.oppPoints >= 7 && g.oppPoints - g.myPoints >= 2
        return when {
            myWins -> winSet(match, isMySet = true)
            oppWins -> winSet(match, isMySet = false)
            else -> match.copy(currentGame = g)
        }
    }

    private fun winGame(match: PadelMatch, isMyGame: Boolean): PadelMatch {
        val s = match.currentSet
        val newSet = if (isMyGame) s.copy(myGames = s.myGames + 1)
                     else s.copy(oppGames = s.oppGames + 1)
        val myWinsSet = newSet.myGames >= 6 && newSet.myGames - newSet.oppGames >= 2
        val oppWinsSet = newSet.oppGames >= 6 && newSet.oppGames - newSet.myGames >= 2
        val isTiebreak = newSet.myGames == 6 && newSet.oppGames == 6
        return when {
            myWinsSet -> winSet(match.copy(currentSet = newSet), isMySet = true)
            oppWinsSet -> winSet(match.copy(currentSet = newSet), isMySet = false)
            isTiebreak -> match.copy(currentSet = newSet, currentGame = GameScore(), isTiebreak = true)
            else -> match.copy(currentSet = newSet, currentGame = GameScore())
        }
    }

    private fun winSet(match: PadelMatch, isMySet: Boolean): PadelMatch {
        val completedSet = match.currentSet
        return match.copy(
            sets = match.sets + completedSet,
            currentSet = SetScore(),
            currentGame = GameScore(),
            isTiebreak = false
        )
    }
}
