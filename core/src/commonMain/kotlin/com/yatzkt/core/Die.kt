package com.yatzkt.core

import kotlin.random.Random

data class Die(val value: Int? = null, val isHeld: Boolean = false) {
  init {
    require(value == null || value in 1..6) {
      "Die value must be between 1 and 6 or null when unrolled, but was $value"
    }
  }
}

interface DiceRoller {
  fun roll(): Int
}

class RandomDiceRoller(private val random: Random = Random.Default) : DiceRoller {
  override fun roll(): Int = random.nextInt(1, 7)
}
