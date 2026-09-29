package com.yatzkt.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
    // Two-Column Side-by-Side Scoring Grid
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
      // Left Column: Upper Section Card
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.weight(1f),
      ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
          // Upper Header & Bonus Progress
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
          ) {
            Text(
              text = "Upper",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
            )
            Text(
              text = "${scorecard.upperSectionSubtotal}/63",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              color =
                if (scorecard.hasUpperBonus) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          Spacer(modifier = Modifier.height(3.dp))
          LinearProgressIndicator(
            progress = { (scorecard.upperSectionSubtotal / 63f).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color =
              if (scorecard.hasUpperBonus) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
          )
          Spacer(modifier = Modifier.height(6.dp))

          // 6 Upper Categories
          YahtzeeCategory.upperCategories.forEachIndexed { index, category ->
            CompactCategoryRow(
              displayName = category.displayName,
              actualScore = scorecard.scoreFor(category),
              potentialScore = potentialScores[category],
              canSelect = canSelectCategory && !scorecard.isCategoryFilled(category),
              onClick = { onCategoryClick(category) },
            )
            if (index < YahtzeeCategory.upperCategories.lastIndex) {
              HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                thickness = 0.5.dp,
              )
            }
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

          // Upper Summary Lines
          CompactSummaryRow(label = "Subtotal", value = "${scorecard.upperSectionSubtotal}")
          CompactSummaryRow(
            label = "Bonus (+35)",
            value = if (scorecard.hasUpperBonus) "+35" else "0",
            highlight = scorecard.hasUpperBonus,
          )
          CompactSummaryRow(
            label = "Upper Total",
            value = "${scorecard.upperSectionTotal}",
            isTotal = true,
          )
        }
      }

      // Right Column: Lower Section Card
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.weight(1f),
      ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
          // Lower Header
          val lowerFilled = YahtzeeCategory.lowerCategories.count { scorecard.isCategoryFilled(it) }
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
          ) {
            Text(
              text = "Lower",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
            )
            Text(
              text = "$lowerFilled/7 done",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          Spacer(modifier = Modifier.height(13.dp))

          // 7 Lower Categories
          YahtzeeCategory.lowerCategories.forEachIndexed { index, category ->
            val shortName =
              when (category) {
                YahtzeeCategory.THREE_OF_A_KIND -> "3 of a Kind"
                YahtzeeCategory.FOUR_OF_A_KIND -> "4 of a Kind"
                YahtzeeCategory.FULL_HOUSE -> "Full House"
                YahtzeeCategory.SMALL_STRAIGHT -> "Sm Straight"
                YahtzeeCategory.LARGE_STRAIGHT -> "Lg Straight"
                YahtzeeCategory.YAHTZEE -> "Yahtzee"
                YahtzeeCategory.CHANCE -> "Chance"
                else -> category.displayName
              }

            CompactCategoryRow(
              displayName = shortName,
              actualScore = scorecard.scoreFor(category),
              potentialScore = potentialScores[category],
              canSelect = canSelectCategory && !scorecard.isCategoryFilled(category),
              onClick = { onCategoryClick(category) },
            )
            if (index < YahtzeeCategory.lowerCategories.lastIndex) {
              HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                thickness = 0.5.dp,
              )
            }
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

          // Align vertical height with left column's 3 summary rows
          Spacer(modifier = Modifier.height(16.dp))
          CompactSummaryRow(
            label = "Lower Total",
            value = "${scorecard.lowerSectionTotal}",
            isTotal = true,
          )
        }
      }
    }

    // Grand Total Overview Card (Full Width at Bottom)
    Card(
      shape = RoundedCornerShape(14.dp),
      colors =
        CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
      ) {
        Column {
          Text(
            text = "Grand Total",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text =
              "Upper: ${scorecard.upperSectionTotal}  •  Lower: ${scorecard.lowerSectionTotal}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f),
          )
        }
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
  }
}

@Composable
private fun CompactCategoryRow(
  displayName: String,
  actualScore: Int?,
  potentialScore: Int?,
  canSelect: Boolean,
  onClick: () -> Unit,
) {
  val isFilled = actualScore != null
  val rowModifier =
    if (canSelect) {
      Modifier.fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(vertical = 5.dp, horizontal = 2.dp)
    } else {
      Modifier.fillMaxWidth().padding(vertical = 5.dp, horizontal = 2.dp)
    }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = rowModifier,
  ) {
    Text(
      text = displayName,
      style = MaterialTheme.typography.bodySmall,
      fontWeight = if (isFilled) FontWeight.Bold else FontWeight.Medium,
      color =
        if (isFilled) {
          MaterialTheme.colorScheme.onSurface
        } else if (canSelect) {
          MaterialTheme.colorScheme.onSurface
        } else {
          MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        },
      modifier = Modifier.weight(1f),
    )

    when {
      isFilled -> {
        Text(
          text = "$actualScore",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }
      canSelect && potentialScore != null -> {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color =
            if (potentialScore > 0) {
              MaterialTheme.colorScheme.primaryContainer
            } else {
              MaterialTheme.colorScheme.surfaceVariant
            },
          border =
            BorderStroke(
              1.dp,
              if (potentialScore > 0) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.outline,
            ),
        ) {
          Text(
            text = "+$potentialScore",
            style =
              MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
              ),
            color =
              if (potentialScore > 0) {
                MaterialTheme.colorScheme.onPrimaryContainer
              } else {
                MaterialTheme.colorScheme.onSurfaceVariant
              },
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

@Composable
private fun CompactSummaryRow(
  label: String,
  value: String,
  isTotal: Boolean = false,
  highlight: Boolean = false,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp, horizontal = 2.dp),
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
      color =
        if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
    )
    Text(
      text = value,
      style =
        if (isTotal) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
      fontWeight = if (isTotal) FontWeight.ExtraBold else FontWeight.SemiBold,
      color =
        if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
    )
  }
}
