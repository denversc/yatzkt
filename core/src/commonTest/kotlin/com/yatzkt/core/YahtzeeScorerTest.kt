package com.yatzkt.core

import kotlin.test.Test
import kotlin.test.assertEquals

class YahtzeeScorerTest {

  @Test
  fun testUpperSectionScoring() {
    val dice = listOf(1, 1, 2, 3, 6)
    assertEquals(2, YahtzeeScorer.calculateScore(YahtzeeCategory.ONES, dice))
    assertEquals(2, YahtzeeScorer.calculateScore(YahtzeeCategory.TWOS, dice))
    assertEquals(3, YahtzeeScorer.calculateScore(YahtzeeCategory.THREES, dice))
    assertEquals(0, YahtzeeScorer.calculateScore(YahtzeeCategory.FOURS, dice))
    assertEquals(0, YahtzeeScorer.calculateScore(YahtzeeCategory.FIVES, dice))
    assertEquals(6, YahtzeeScorer.calculateScore(YahtzeeCategory.SIXES, dice))
  }

  @Test
  fun testThreeOfAKind() {
    assertEquals(
      17,
      YahtzeeScorer.calculateScore(YahtzeeCategory.THREE_OF_A_KIND, listOf(3, 3, 3, 4, 4)),
    )
    assertEquals(
      23,
      YahtzeeScorer.calculateScore(YahtzeeCategory.THREE_OF_A_KIND, listOf(5, 5, 5, 5, 3)),
    )
    assertEquals(
      0,
      YahtzeeScorer.calculateScore(YahtzeeCategory.THREE_OF_A_KIND, listOf(1, 2, 3, 4, 5)),
    )
  }

  @Test
  fun testFourOfAKind() {
    assertEquals(
      22,
      YahtzeeScorer.calculateScore(YahtzeeCategory.FOUR_OF_A_KIND, listOf(5, 5, 5, 5, 2)),
    )
    assertEquals(
      25,
      YahtzeeScorer.calculateScore(YahtzeeCategory.FOUR_OF_A_KIND, listOf(5, 5, 5, 5, 5)),
    )
    assertEquals(
      0,
      YahtzeeScorer.calculateScore(YahtzeeCategory.FOUR_OF_A_KIND, listOf(5, 5, 5, 2, 2)),
    )
  }

  @Test
  fun testFullHouse() {
    assertEquals(
      25,
      YahtzeeScorer.calculateScore(YahtzeeCategory.FULL_HOUSE, listOf(2, 2, 3, 3, 3)),
    )
    assertEquals(
      25,
      YahtzeeScorer.calculateScore(YahtzeeCategory.FULL_HOUSE, listOf(6, 6, 6, 1, 1)),
    )
    assertEquals(
      25,
      YahtzeeScorer.calculateScore(YahtzeeCategory.FULL_HOUSE, listOf(4, 4, 4, 4, 4)),
    )
    assertEquals(0, YahtzeeScorer.calculateScore(YahtzeeCategory.FULL_HOUSE, listOf(2, 2, 2, 2, 3)))
    assertEquals(0, YahtzeeScorer.calculateScore(YahtzeeCategory.FULL_HOUSE, listOf(1, 2, 3, 4, 5)))
  }

  @Test
  fun testSmallStraight() {
    // 1-2-3-4
    assertEquals(
      30,
      YahtzeeScorer.calculateScore(YahtzeeCategory.SMALL_STRAIGHT, listOf(1, 3, 4, 2, 6)),
    )
    // 2-3-4-5 with duplicate
    assertEquals(
      30,
      YahtzeeScorer.calculateScore(YahtzeeCategory.SMALL_STRAIGHT, listOf(2, 3, 4, 4, 5)),
    )
    // 3-4-5-6
    assertEquals(
      30,
      YahtzeeScorer.calculateScore(YahtzeeCategory.SMALL_STRAIGHT, listOf(6, 4, 3, 5, 1)),
    )
    // 5 dice straight also has small straight
    assertEquals(
      30,
      YahtzeeScorer.calculateScore(YahtzeeCategory.SMALL_STRAIGHT, listOf(1, 2, 3, 4, 5)),
    )
    // Non-straight
    assertEquals(
      0,
      YahtzeeScorer.calculateScore(YahtzeeCategory.SMALL_STRAIGHT, listOf(1, 2, 4, 5, 6)),
    )
  }

  @Test
  fun testLargeStraight() {
    assertEquals(
      40,
      YahtzeeScorer.calculateScore(YahtzeeCategory.LARGE_STRAIGHT, listOf(5, 3, 1, 4, 2)),
    )
    assertEquals(
      40,
      YahtzeeScorer.calculateScore(YahtzeeCategory.LARGE_STRAIGHT, listOf(6, 4, 3, 5, 2)),
    )
    assertEquals(
      0,
      YahtzeeScorer.calculateScore(YahtzeeCategory.LARGE_STRAIGHT, listOf(1, 2, 3, 4, 6)),
    )
    assertEquals(
      0,
      YahtzeeScorer.calculateScore(YahtzeeCategory.LARGE_STRAIGHT, listOf(2, 3, 4, 5, 5)),
    )
  }

  @Test
  fun testYahtzee() {
    assertEquals(50, YahtzeeScorer.calculateScore(YahtzeeCategory.YAHTZEE, listOf(1, 1, 1, 1, 1)))
    assertEquals(50, YahtzeeScorer.calculateScore(YahtzeeCategory.YAHTZEE, listOf(6, 6, 6, 6, 6)))
    assertEquals(0, YahtzeeScorer.calculateScore(YahtzeeCategory.YAHTZEE, listOf(6, 6, 6, 6, 5)))
  }

  @Test
  fun testChance() {
    assertEquals(15, YahtzeeScorer.calculateScore(YahtzeeCategory.CHANCE, listOf(1, 2, 3, 4, 5)))
    assertEquals(30, YahtzeeScorer.calculateScore(YahtzeeCategory.CHANCE, listOf(6, 6, 6, 6, 6)))
  }
}
