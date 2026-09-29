package com.yatzkt.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FixedDiceRoller(private val sequence: List<Int>) : DiceRoller {
  private var index = 0

  override fun roll(): Int {
    val value = sequence[index % sequence.size]
    index++
    return value
  }
}

class YahtzeeGameEngineTest {

  @Test
  fun testRollingLifecycle() {
    val roller = FixedDiceRoller(listOf(3, 3, 3, 3, 3))
    val engine = YahtzeeGameEngine(roller)

    assertEquals(3, engine.state.value.rollsRemaining)
    assertTrue(engine.state.value.canRoll)
    assertFalse(engine.state.value.canSelectCategory)

    // Roll 1
    assertTrue(engine.rollDice())
    assertEquals(2, engine.state.value.rollsRemaining)
    assertTrue(engine.state.value.canSelectCategory)
    assertTrue(engine.state.value.canToggleHold)

    // Roll 2
    assertTrue(engine.rollDice())
    assertEquals(1, engine.state.value.rollsRemaining)

    // Roll 3
    assertTrue(engine.rollDice())
    assertEquals(0, engine.state.value.rollsRemaining)
    assertFalse(engine.state.value.canRoll)
    assertTrue(engine.state.value.canSelectCategory)

    // Roll 4 should fail
    assertFalse(engine.rollDice())
  }

  @Test
  fun testHoldingDice() {
    // First roll gives all 1s
    // Second roll will supply 6s
    val values = mutableListOf(1, 1, 1, 1, 1, 6, 6, 6, 6, 6)
    var idx = 0
    val roller =
      object : DiceRoller {
        override fun roll(): Int = values[idx++]
      }
    val engine = YahtzeeGameEngine(roller)

    engine.rollDice()
    assertEquals(listOf(1, 1, 1, 1, 1), engine.state.value.dice.map { it.value })

    // Hold die at index 0 and 2
    assertTrue(engine.toggleHold(0))
    assertTrue(engine.toggleHold(2))
    assertTrue(engine.state.value.dice[0].isHeld)
    assertFalse(engine.state.value.dice[1].isHeld)
    assertTrue(engine.state.value.dice[2].isHeld)

    // Roll again: only index 1, 3, 4 should re-roll
    engine.rollDice()
    val currentDice = engine.state.value.dice
    assertEquals(1, currentDice[0].value)
    assertEquals(6, currentDice[1].value)
    assertEquals(1, currentDice[2].value)
    assertEquals(6, currentDice[3].value)
    assertEquals(6, currentDice[4].value)
  }

  @Test
  fun testScoreSelectionAndTurnAdvancement() {
    val roller = FixedDiceRoller(listOf(5, 5, 5, 2, 2))
    val engine = YahtzeeGameEngine(roller)

    engine.rollDice()
    assertEquals(1, engine.state.value.currentRound)

    // Select FULL_HOUSE
    assertTrue(engine.selectCategory(YahtzeeCategory.FULL_HOUSE))
    assertEquals(25, engine.state.value.scorecard.scoreFor(YahtzeeCategory.FULL_HOUSE))
    assertEquals(2, engine.state.value.currentRound)
    assertEquals(3, engine.state.value.rollsRemaining)
    assertFalse(engine.state.value.dice.any { it.isHeld })

    // Cannot select FULL_HOUSE again
    engine.rollDice()
    assertFalse(engine.selectCategory(YahtzeeCategory.FULL_HOUSE))
  }

  @Test
  fun testFullGameCompletion() {
    val roller = FixedDiceRoller(listOf(6, 6, 6, 6, 6))
    val engine = YahtzeeGameEngine(roller)

    YahtzeeCategory.entries.forEach { category ->
      assertFalse(engine.state.value.isGameOver)
      engine.rollDice()
      assertTrue(engine.selectCategory(category))
    }

    assertTrue(engine.state.value.isGameOver)
    assertFalse(engine.state.value.canRoll)
    assertFalse(engine.state.value.canSelectCategory)

    // Reset
    engine.resetGame()
    assertFalse(engine.state.value.isGameOver)
    assertEquals(1, engine.state.value.currentRound)
    assertEquals(3, engine.state.value.rollsRemaining)
  }
}
