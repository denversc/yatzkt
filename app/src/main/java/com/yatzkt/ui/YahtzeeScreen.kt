package com.yatzkt.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatzkt.ui.components.DieView
import com.yatzkt.ui.components.GameOverCard
import com.yatzkt.ui.components.ScorecardView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YahtzeeScreen(viewModel: YahtzeeViewModel = viewModel(), modifier: Modifier = Modifier) {
  val uiState by viewModel.uiState.collectAsState()
  val gameState = uiState.gameState
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Text(text = "🎲 Yahtzee", fontWeight = FontWeight.Bold)
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.primaryContainer,
            ) {
              Text(
                text = "Round ${gameState.currentRound}/${gameState.totalRounds}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              )
            }
          }
        },
        actions = {
          TextButton(onClick = { viewModel.onResetGame() }) {
            Text(text = "Reset", fontWeight = FontWeight.SemiBold)
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
      )
    },
    bottomBar = {
      DiceControlsBottomPanel(
        uiState = uiState,
        onToggleHold = { index -> viewModel.onToggleHold(index, context) },
        onRollClick = { viewModel.onRollClick(context) },
      )
    },
    modifier = modifier.fillMaxSize(),
  ) { innerPadding ->
    Column(
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier =
        Modifier.fillMaxSize().padding(innerPadding).verticalScroll(scrollState).padding(16.dp),
    ) {
      // Scorecard
      ScorecardView(
        scorecard = gameState.scorecard,
        potentialScores = gameState.potentialScores,
        canSelectCategory = gameState.canSelectCategory && !uiState.isRollingAnimationActive,
        onCategoryClick = { category -> viewModel.onCategorySelect(category) },
      )

      // Game Over Card (inline prompt at the bottom as requested)
      if (gameState.isGameOver) {
        GameOverCard(
          scorecard = gameState.scorecard,
          onPlayAgainClick = { viewModel.onResetGame() },
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun DiceControlsBottomPanel(
  uiState: YahtzeeUiState,
  onToggleHold: (Int) -> Unit,
  onRollClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val gameState = uiState.gameState

  Surface(
    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 3.dp,
    shadowElevation = 8.dp,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
    modifier = modifier.fillMaxWidth(),
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
    ) {
      // Dice row
      Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        uiState.displayDice.forEachIndexed { index, die ->
          DieView(
            die = die,
            enabled = gameState.canToggleHold && !uiState.isRollingAnimationActive,
            onClick = { onToggleHold(index) },
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Controls row
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column {
          Text(
            text = "Rolls Remaining",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 2.dp),
          ) {
            repeat(3) { rollIndex ->
              val isAvailable = rollIndex < gameState.rollsRemaining
              Surface(
                shape = RoundedCornerShape(4.dp),
                color =
                  if (isAvailable) {
                    MaterialTheme.colorScheme.primary
                  } else {
                    MaterialTheme.colorScheme.surfaceVariant
                  },
                modifier = Modifier.size(width = 16.dp, height = 8.dp),
              ) {}
            }
            Text(
              text = " (${gameState.rollsRemaining})",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Bold,
            )
          }
        }

        Button(
          onClick = onRollClick,
          enabled = gameState.canRoll && !uiState.isRollingAnimationActive,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          modifier = Modifier.height(44.dp),
        ) {
          Text(
            text = if (uiState.isRollingAnimationActive) "Rolling..." else "Roll Dice",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
          )
        }
      }
    }
  }
}
