package com.yatzkt.core

enum class CategorySection {
    UPPER,
    LOWER
}

enum class YahtzeeCategory(
    val displayName: String,
    val description: String,
    val section: CategorySection
) {
    // Upper Section
    ONES("Ones", "Sum of all ones", CategorySection.UPPER),
    TWOS("Twos", "Sum of all twos", CategorySection.UPPER),
    THREES("Threes", "Sum of all threes", CategorySection.UPPER),
    FOURS("Fours", "Sum of all fours", CategorySection.UPPER),
    FIVES("Fives", "Sum of all fives", CategorySection.UPPER),
    SIXES("Sixes", "Sum of all sixes", CategorySection.UPPER),

    // Lower Section
    THREE_OF_A_KIND("Three of a Kind", "At least 3 same dice. Sum of all dice.", CategorySection.LOWER),
    FOUR_OF_A_KIND("Four of a Kind", "At least 4 same dice. Sum of all dice.", CategorySection.LOWER),
    FULL_HOUSE("Full House", "Three of one and two of another. Scores 25.", CategorySection.LOWER),
    SMALL_STRAIGHT("Small Straight", "Sequence of 4 dice. Scores 30.", CategorySection.LOWER),
    LARGE_STRAIGHT("Large Straight", "Sequence of 5 dice. Scores 40.", CategorySection.LOWER),
    YAHTZEE("Yahtzee", "All 5 dice identical. Scores 50.", CategorySection.LOWER),
    CHANCE("Chance", "Sum of all 5 dice.", CategorySection.LOWER);

    companion object {
        val upperCategories = entries.filter { it.section == CategorySection.UPPER }
        val lowerCategories = entries.filter { it.section == CategorySection.LOWER }
    }
}
