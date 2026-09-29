package com.yatzkt.ui

import com.yatzkt.core.DiceRoller
import com.yatzkt.core.YahtzeeCategory
import com.yatzkt.core.YahtzeeGameEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class YahtzeeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() {
        val engine = YahtzeeGameEngine()
        val viewModel = YahtzeeViewModel(engine)

        val state = viewModel.uiState.value
        assertEquals(1, state.gameState.currentRound)
        assertEquals(3, state.gameState.rollsRemaining)
        assertEquals(5, state.displayDice.size)
        assertFalse(state.isRollingAnimationActive)
        assertFalse(state.gameState.isGameOver)
    }

    @Test
    fun testCategorySelectionUpdatesState() {
        var rollValue = 4
        val deterministicRoller = object : DiceRoller {
            override fun roll(): Int = rollValue
        }
        val engine = YahtzeeGameEngine(deterministicRoller)
        val viewModel = YahtzeeViewModel(engine)

        // Roll via engine
        engine.rollDice()
        testDispatcher.scheduler.advanceUntilIdle()

        // Select FOURS category
        viewModel.onCategorySelect(YahtzeeCategory.FOURS)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedState = viewModel.uiState.value
        assertEquals(2, updatedState.gameState.currentRound)
        assertEquals(3, updatedState.gameState.rollsRemaining)
        assertEquals(20, updatedState.gameState.scorecard.scoreFor(YahtzeeCategory.FOURS))
    }

    @Test
    fun testResetGame() {
        val engine = YahtzeeGameEngine()
        val viewModel = YahtzeeViewModel(engine)

        engine.rollDice()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onResetGame()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.gameState.currentRound)
        assertEquals(3, state.gameState.rollsRemaining)
        assertFalse(state.gameState.hasRolledThisTurn)
    }
}
