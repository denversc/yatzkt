package com.yatzkt.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class YahtzeeGameEngine(private val diceRoller: DiceRoller = RandomDiceRoller()) {
  private val _state = MutableStateFlow(YahtzeeGameState.initial())
  val state: StateFlow<YahtzeeGameState> = _state.asStateFlow()

  /**
   * Rolls all dice that are not currently held. Decrements the remaining rolls for the current turn
   * and updates potential preview scores. Returns true if the roll succeeded, or false if rolling
   * is not permitted.
   */
  fun rollDice(): Boolean {
    var didRoll = false
    _state.update { current ->
      if (!current.canRoll) return@update current

      val updatedDice =
        current.dice.map { die -> if (die.isHeld) die else die.copy(value = diceRoller.roll()) }

      val newRollsRemaining = current.rollsRemaining - 1
      val diceValues = updatedDice.mapNotNull { it.value }

      val previews =
        YahtzeeCategory.entries
          .filter { !current.scorecard.isCategoryFilled(it) }
          .associateWith { category -> YahtzeeScorer.calculateScore(category, diceValues) }

      didRoll = true
      current.copy(
        rollsRemaining = newRollsRemaining,
        dice = updatedDice,
        potentialScores = previews,
      )
    }
    return didRoll
  }

  /**
   * Toggles the hold status of the die at [dieIndex]. Can only be toggled after the first roll and
   * before 3 rolls are exhausted.
   */
  fun toggleHold(dieIndex: Int): Boolean {
    var didToggle = false
    _state.update { current ->
      if (!current.canToggleHold || dieIndex !in current.dice.indices) return@update current

      val updatedDice =
        current.dice.mapIndexed { index, die ->
          if (index == dieIndex) die.copy(isHeld = !die.isHeld) else die
        }

      didToggle = true
      current.copy(dice = updatedDice)
    }
    return didToggle
  }

  /** Scores the current dice in [category] and advances to the next turn (or finishes the game). */
  fun selectCategory(category: YahtzeeCategory): Boolean {
    var didSelect = false
    _state.update { current ->
      if (!current.canSelectCategory || current.scorecard.isCategoryFilled(category)) {
        return@update current
      }

      val diceValues = current.dice.mapNotNull { it.value }
      val score = YahtzeeScorer.calculateScore(category, diceValues)
      val updatedScorecard = current.scorecard.withScore(category, score)

      // Reset dice for the next turn: unheld and blank before rolled
      val resetDice = List(5) { Die(value = null, isHeld = false) }
      val nextRound = (updatedScorecard.completedRoundsCount + 1).coerceAtMost(current.totalRounds)

      didSelect = true
      current.copy(
        currentRound = nextRound,
        rollsRemaining = current.maxRollsPerTurn,
        dice = resetDice,
        scorecard = updatedScorecard,
        potentialScores = emptyMap(),
      )
    }
    return didSelect
  }

  /** Resets the game to a clean initial state. */
  fun resetGame() {
    _state.value = YahtzeeGameState.initial()
  }
}
