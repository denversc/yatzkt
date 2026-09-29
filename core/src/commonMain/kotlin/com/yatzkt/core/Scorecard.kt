package com.yatzkt.core

data class Scorecard(val scores: Map<YahtzeeCategory, Int> = emptyMap()) {
  val totalRounds = 13

  val completedRoundsCount: Int
    get() = scores.size

  val isFilled: Boolean
    get() = completedRoundsCount == totalRounds

  fun isCategoryFilled(category: YahtzeeCategory): Boolean = scores.containsKey(category)

  fun scoreFor(category: YahtzeeCategory): Int? = scores[category]

  fun withScore(category: YahtzeeCategory, score: Int): Scorecard {
    require(!isCategoryFilled(category)) { "Category $category is already filled." }
    return copy(scores = scores + (category to score))
  }

  val upperSectionSubtotal: Int
    get() = YahtzeeCategory.upperCategories.sumOf { scores[it] ?: 0 }

  val hasUpperBonus: Boolean
    get() = upperSectionSubtotal >= 63

  val upperBonusScore: Int
    get() = if (hasUpperBonus) 35 else 0

  val upperSectionTotal: Int
    get() = upperSectionSubtotal + upperBonusScore

  val lowerSectionTotal: Int
    get() = YahtzeeCategory.lowerCategories.sumOf { scores[it] ?: 0 }

  val grandTotal: Int
    get() = upperSectionTotal + lowerSectionTotal
}
