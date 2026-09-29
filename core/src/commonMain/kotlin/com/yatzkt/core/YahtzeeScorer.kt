package com.yatzkt.core

object YahtzeeScorer {

    /**
     * Calculates the score for a specific category given a list of 5 dice values.
     */
    fun calculateScore(category: YahtzeeCategory, dice: List<Int>): Int {
        require(dice.size == 5) { "Exactly 5 dice are required to score, got ${dice.size}" }
        val counts = dice.groupingBy { it }.eachCount()

        return when (category) {
            YahtzeeCategory.ONES -> sumOfSpecificValue(dice, 1)
            YahtzeeCategory.TWOS -> sumOfSpecificValue(dice, 2)
            YahtzeeCategory.THREES -> sumOfSpecificValue(dice, 3)
            YahtzeeCategory.FOURS -> sumOfSpecificValue(dice, 4)
            YahtzeeCategory.FIVES -> sumOfSpecificValue(dice, 5)
            YahtzeeCategory.SIXES -> sumOfSpecificValue(dice, 6)

            YahtzeeCategory.THREE_OF_A_KIND -> {
                if (counts.values.any { it >= 3 }) dice.sum() else 0
            }

            YahtzeeCategory.FOUR_OF_A_KIND -> {
                if (counts.values.any { it >= 4 }) dice.sum() else 0
            }

            YahtzeeCategory.FULL_HOUSE -> {
                val sortedCounts = counts.values.sorted()
                // A standard Full House is 3 of one number and 2 of another.
                // 5 of a kind is also valid as a Full House in Yahtzee rules.
                if (sortedCounts == listOf(2, 3) || sortedCounts == listOf(5)) 25 else 0
            }

            YahtzeeCategory.SMALL_STRAIGHT -> {
                if (hasStraightOfLength(dice, 4)) 30 else 0
            }

            YahtzeeCategory.LARGE_STRAIGHT -> {
                if (hasStraightOfLength(dice, 5)) 40 else 0
            }

            YahtzeeCategory.YAHTZEE -> {
                if (counts.values.any { it == 5 }) 50 else 0
            }

            YahtzeeCategory.CHANCE -> dice.sum()
        }
    }

    private fun sumOfSpecificValue(dice: List<Int>, value: Int): Int {
        return dice.filter { it == value }.sum()
    }

    private fun hasStraightOfLength(dice: List<Int>, length: Int): Boolean {
        val uniqueSorted = dice.toSet().sorted()
        if (uniqueSorted.size < length) return false

        // Check contiguous streaks of length
        var streak = 1
        for (i in 1 until uniqueSorted.size) {
            if (uniqueSorted[i] == uniqueSorted[i - 1] + 1) {
                streak++
                if (streak >= length) return true
            } else {
                streak = 1
            }
        }
        return false
    }
}
