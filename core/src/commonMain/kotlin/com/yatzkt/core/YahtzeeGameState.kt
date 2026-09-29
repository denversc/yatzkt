package com.yatzkt.core

data class YahtzeeGameState(
  val currentRound: Int = 1,
  val rollsRemaining: Int = 3,
  val dice: List<Die> = List(5) { Die(value = null, isHeld = false) },
  val scorecard: Scorecard = Scorecard(),
  val potentialScores: Map<YahtzeeCategory, Int> = emptyMap(),
) {
  val maxRollsPerTurn: Int = 3
  val totalRounds: Int = 13

  val isGameOver: Boolean
    get() = scorecard.isFilled

  val hasRolledThisTurn: Boolean
    get() = rollsRemaining < maxRollsPerTurn

  val canRoll: Boolean
    get() = !isGameOver && rollsRemaining > 0

  val canSelectCategory: Boolean
    get() = !isGameOver && hasRolledThisTurn

  val canToggleHold: Boolean
    get() = !isGameOver && hasRolledThisTurn && rollsRemaining > 0

  companion object {
    fun initial(): YahtzeeGameState = YahtzeeGameState()
  }
}
