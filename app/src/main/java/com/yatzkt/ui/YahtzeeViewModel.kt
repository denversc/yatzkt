package com.yatzkt.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatzkt.accessibility.AccessibilityUtils
import com.yatzkt.core.Die
import com.yatzkt.core.YahtzeeCategory
import com.yatzkt.core.YahtzeeGameEngine
import com.yatzkt.core.YahtzeeGameState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class YahtzeeUiState(
    val gameState: YahtzeeGameState = YahtzeeGameState.initial(),
    val displayDice: List<Die> = YahtzeeGameState.initial().dice,
    val isRollingAnimationActive: Boolean = false
)

class YahtzeeViewModel(
    private val engine: YahtzeeGameEngine = YahtzeeGameEngine()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        YahtzeeUiState(
            gameState = engine.state.value,
            displayDice = engine.state.value.dice
        )
    )
    val uiState: StateFlow<YahtzeeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            engine.state.collect { gameState ->
                _uiState.update { current ->
                    if (!current.isRollingAnimationActive) {
                        current.copy(
                            gameState = gameState,
                            displayDice = gameState.dice
                        )
                    } else {
                        current.copy(gameState = gameState)
                    }
                }
            }
        }
    }

    fun onRollClick(context: Context) {
        if (!engine.state.value.canRoll || _uiState.value.isRollingAnimationActive) return

        val reducedMotion = AccessibilityUtils.isReducedMotionEnabled(context)

        if (reducedMotion) {
            // Immediate roll, no animations, no haptics
            engine.rollDice()
            _uiState.update { it.copy(displayDice = engine.state.value.dice) }
        } else {
            // Animated roll with haptic feedback
            performHapticFeedback(context, VibrationEffect.EFFECT_CLICK)

            viewModelScope.launch {
                _uiState.update { it.copy(isRollingAnimationActive = true) }

                // Brief tumbling animation: 7 frames over ~350ms
                val currentDice = engine.state.value.dice
                repeat(7) {
                    val animatedDice = currentDice.map { die ->
                        if (die.isHeld) die else die.copy(value = Random.nextInt(1, 7))
                    }
                    _uiState.update { it.copy(displayDice = animatedDice) }
                    delay(50)
                }

                engine.rollDice()
                _uiState.update {
                    it.copy(
                        isRollingAnimationActive = false,
                        displayDice = engine.state.value.dice
                    )
                }
                performHapticFeedback(context, VibrationEffect.EFFECT_TICK)
            }
        }
    }

    fun onToggleHold(dieIndex: Int, context: Context) {
        if (_uiState.value.isRollingAnimationActive) return
        val didToggle = engine.toggleHold(dieIndex)
        if (didToggle) {
            val reducedMotion = AccessibilityUtils.isReducedMotionEnabled(context)
            if (!reducedMotion) {
                performHapticFeedback(context, VibrationEffect.EFFECT_TICK)
            }
            _uiState.update { it.copy(displayDice = engine.state.value.dice) }
        }
    }

    fun onCategorySelect(category: YahtzeeCategory) {
        if (_uiState.value.isRollingAnimationActive) return
        engine.selectCategory(category)
        _uiState.update { it.copy(displayDice = engine.state.value.dice) }
    }

    fun onResetGame() {
        engine.resetGame()
        _uiState.update {
            it.copy(
                isRollingAnimationActive = false,
                gameState = engine.state.value,
                displayDice = engine.state.value.dice
            )
        }
    }

    private fun performHapticFeedback(context: Context, effectId: Int) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let {
                if (it.hasVibrator()) {
                    it.vibrate(VibrationEffect.createPredefined(effectId))
                }
            }
        } catch (_: Exception) {
            // Silently ignore if vibration service is unavailable or permissions are restricted
        }
    }
}
