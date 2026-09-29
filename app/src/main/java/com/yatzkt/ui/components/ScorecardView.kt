package com.yatzkt.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatzkt.core.Scorecard
import com.yatzkt.core.YahtzeeCategory

@Composable
fun ScorecardView(
  scorecard: Scorecard,
  potentialScores: Map<YahtzeeCategory, Int>,
  canSelectCategory: Boolean,
  onCategoryClick: (YahtzeeCategory) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier.fillMaxWidth()) {
    // Top Score Dashboard Card
    Card(
      shape = RoundedCornerShape(14.dp),
      colors =
        CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          // Upper section overview
          Column {
            Text(
              text = "Upper: ${scorecard.upperSectionTotal}",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
            )
            Text(
              text =
                "Bonus: ${scorecard.upperSectionSubtotal}/63 (${if (scorecard.hasUpperBonus) "+35 earned!" else "+35"})",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f),
            )
          }

          // Lower section overview
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "Lower",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f),
            )
            Text(
              text = "${scorecard.lowerSectionTotal}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
            )
          }

          // Grand total
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "Grand Total",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f),
            )
            Text(
              text = "${scorecard.grandTotal}",
              style =
                MaterialTheme.typography.headlineMedium.copy(
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 28.sp,
                ),
              color = MaterialTheme.colorScheme.primary,
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
          progress = { (scorecard.upperSectionSubtotal / 63f).coerceIn(0f, 1f) },
          modifier = Modifier.fillMaxWidth().height(4.dp),
          color =
            if (scorecard.hasUpperBonus) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary,
          trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        )
      }
    }

    // Headers for the two tile columns
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "UPPER SECTION",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.weight(1f).padding(start = 4.dp),
      )
      Text(
        text = "LOWER SECTION",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.weight(1f).padding(start = 4.dp),
      )
    }

    // 2-Column Matrix of Scoring Tiles
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
      // Left Column: Upper categories + Bonus Tile
      Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.weight(1f)) {
        YahtzeeCategory.upperCategories.forEach { category ->
          val icon =
            when (category) {
              YahtzeeCategory.ONES -> "⚀"
              YahtzeeCategory.TWOS -> "⚁"
              YahtzeeCategory.THREES -> "⚂"
              YahtzeeCategory.FOURS -> "⚃"
              YahtzeeCategory.FIVES -> "⚄"
              YahtzeeCategory.SIXES -> "⚅"
              else -> ""
            }

          ScoreTile(
            icon = icon,
            title = category.displayName,
            actualScore = scorecard.scoreFor(category),
            potentialScore = potentialScores[category],
            canSelect = canSelectCategory && !scorecard.isCategoryFilled(category),
            onClick = { onCategoryClick(category) },
          )
        }

        // Bonus Status Tile
        BonusStatusTile(
          subtotal = scorecard.upperSectionSubtotal,
          hasBonus = scorecard.hasUpperBonus,
          bonusScore = scorecard.upperBonusScore,
        )
      }

      // Right Column: Lower categories (7 tiles)
      Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.weight(1f)) {
        YahtzeeCategory.lowerCategories.forEach { category ->
          val (icon, title) =
            when (category) {
              YahtzeeCategory.THREE_OF_A_KIND -> "3×" to "3 of Kind"
              YahtzeeCategory.FOUR_OF_A_KIND -> "4×" to "4 of Kind"
              YahtzeeCategory.FULL_HOUSE -> "🏠" to "Full House"
              YahtzeeCategory.SMALL_STRAIGHT -> "1-4" to "Sm Straight"
              YahtzeeCategory.LARGE_STRAIGHT -> "1-5" to "Lg Straight"
              YahtzeeCategory.YAHTZEE -> "★" to "Yahtzee"
              YahtzeeCategory.CHANCE -> "🎲" to "Chance"
              else -> "" to category.displayName
            }

          ScoreTile(
            icon = icon,
            title = title,
            actualScore = scorecard.scoreFor(category),
            potentialScore = potentialScores[category],
            canSelect = canSelectCategory && !scorecard.isCategoryFilled(category),
            onClick = { onCategoryClick(category) },
          )
        }
      }
    }
  }
}

@Composable
private fun ScoreTile(
  icon: String,
  title: String,
  actualScore: Int?,
  potentialScore: Int?,
  canSelect: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val isFilled = actualScore != null
  val isHighlight = canSelect && potentialScore != null && potentialScore > 0

  Surface(
    shape = RoundedCornerShape(10.dp),
    color =
      when {
        isFilled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        isHighlight -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.surface
      },
    border =
      BorderStroke(
        1.dp,
        when {
          isHighlight -> MaterialTheme.colorScheme.primary
          isFilled -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
          else -> MaterialTheme.colorScheme.outlineVariant
        },
      ),
    modifier =
      modifier
        .fillMaxWidth()
        .height(39.dp)
        .then(if (canSelect) Modifier.clickable(onClick = onClick) else Modifier),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Text(
          text = icon,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.bodySmall,
          fontWeight = if (isFilled) FontWeight.Bold else FontWeight.Medium,
          color =
            if (isFilled) MaterialTheme.colorScheme.onSurface
            else if (canSelect) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
      }

      when {
        isFilled -> {
          Text(
            text = "$actualScore",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
        canSelect && potentialScore != null -> {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color =
              if (potentialScore > 0) MaterialTheme.colorScheme.primaryContainer
              else MaterialTheme.colorScheme.surfaceVariant,
          ) {
            Text(
              text = "+$potentialScore",
              style =
                MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  fontStyle = FontStyle.Italic,
                ),
              color =
                if (potentialScore > 0) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
          }
        }
        else -> {
          Text(
            text = "—",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
          )
        }
      }
    }
  }
}

@Composable
private fun BonusStatusTile(
  subtotal: Int,
  hasBonus: Boolean,
  bonusScore: Int,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color =
      if (hasBonus) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
      else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
    border =
      BorderStroke(
        1.dp,
        if (hasBonus) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
      ),
    modifier = modifier.fillMaxWidth().height(39.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "🎁", fontSize = 12.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (hasBonus) "Bonus Won!" else "Bonus (63)",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Medium,
          color =
            if (hasBonus) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Text(
        text = if (hasBonus) "+$bonusScore" else "${subtotal}/63",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color =
          if (hasBonus) MaterialTheme.colorScheme.primary
          else MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
