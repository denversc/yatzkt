package com.yatzkt.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScorecardTest {

    @Test
    fun testUpperBonusThreshold() {
        var card = Scorecard()
            .withScore(YahtzeeCategory.ONES, 3)
            .withScore(YahtzeeCategory.TWOS, 6)
            .withScore(YahtzeeCategory.THREES, 9)
            .withScore(YahtzeeCategory.FOURS, 12)
            .withScore(YahtzeeCategory.FIVES, 15)
            .withScore(YahtzeeCategory.SIXES, 17) // Total = 62

        assertEquals(62, card.upperSectionSubtotal)
        assertFalse(card.hasUpperBonus)
        assertEquals(0, card.upperBonusScore)
        assertEquals(62, card.upperSectionTotal)

        // Reach exactly 63
        card = Scorecard()
            .withScore(YahtzeeCategory.ONES, 3)
            .withScore(YahtzeeCategory.TWOS, 6)
            .withScore(YahtzeeCategory.THREES, 9)
            .withScore(YahtzeeCategory.FOURS, 12)
            .withScore(YahtzeeCategory.FIVES, 15)
            .withScore(YahtzeeCategory.SIXES, 18) // Total = 63

        assertEquals(63, card.upperSectionSubtotal)
        assertTrue(card.hasUpperBonus)
        assertEquals(35, card.upperBonusScore)
        assertEquals(98, card.upperSectionTotal)
    }

    @Test
    fun testGrandTotalAndCompletion() {
        var card = Scorecard()
        assertFalse(card.isFilled)
        assertEquals(0, card.completedRoundsCount)

        YahtzeeCategory.entries.forEach { category ->
            card = card.withScore(category, 10)
        }

        assertTrue(card.isFilled)
        assertEquals(13, card.completedRoundsCount)
        // Upper: 6 * 10 = 60 (<63, no bonus)
        // Lower: 7 * 10 = 70
        // Grand: 60 + 70 = 130
        assertEquals(60, card.upperSectionSubtotal)
        assertEquals(0, card.upperBonusScore)
        assertEquals(70, card.lowerSectionTotal)
        assertEquals(130, card.grandTotal)
    }
}
