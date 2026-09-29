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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
  var selectedTab by rememberSaveable { mutableIntStateOf(0) }

  val upperFilledCount = YahtzeeCategory.upperCategories.count { scorecard.isCategoryFilled(it) }
  val lowerFilledCount = YahtzeeCategory.lowerCategories.count { scorecard.isCategoryFilled(it) }

  Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier.fillMaxWidth()) {
    // Section Selector Tabs
    PrimaryTabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.primary,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Text(
            text = "Upper ($upperFilledCount/6)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
          )
        },
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Text(
            text = "Lower ($lowerFilledCount/7)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
          )
        },
      )
    }

    // Active Section Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
        if (selectedTab == 0) {
          // Upper Section Header with bonus tracker
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
          ) {
            Text(
              text = "Upper Bonus (63 pts)",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
            )
            Text(
              text =
                "${scorecard.upperSectionSubtotal} / 63 (${if (scorecard.hasUpperBonus) "+35 bonus!" else "+35"})",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          LinearProgressIndicator(
            progress = { (scorecard.upperSectionSubtotal / 63f).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(5.dp),
            color =
              if (scorecard.hasUpperBonus) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
          )
          Spacer(modifier = Modifier.height(6.dp))

          YahtzeeCategory.upperCategories.forEachIndexed { index, category ->
            CategoryRow(
              category = category,
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

          HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

          // Upper Section Totals Summary Line
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "Subtotal: ${scorecard.upperSectionSubtotal}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
              text = "Bonus: ${if (scorecard.hasUpperBonus) "+35" else "0"}",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = if (scorecard.hasUpperBonus) FontWeight.Bold else FontWeight.Normal,
              color =
                if (scorecard.hasUpperBonus) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
              text = "Upper Total: ${scorecard.upperSectionTotal}",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
            )
          }
        } else {
          // Lower Section
          YahtzeeCategory.lowerCategories.forEachIndexed { index, category ->
            CategoryRow(
              category = category,
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

          HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "Lower Total",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
            )
            Text(
              text = "${scorecard.lowerSectionTotal}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
            )
          }
        }
      }
    }

    // Grand Total Overview Card (always visible)
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
              fontSize = 26.sp,
            ),
          color = MaterialTheme.colorScheme.primary,
        )
      }
    }
  }
}

@Composable
private fun CategoryRow(
  category: YahtzeeCategory,
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
        .padding(vertical = 5.dp, horizontal = 4.dp)
    } else {
      Modifier.fillMaxWidth().padding(vertical = 5.dp, horizontal = 4.dp)
    }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = rowModifier,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f).padding(end = 8.dp),
    ) {
      Text(
        text = category.displayName,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (isFilled) FontWeight.Bold else FontWeight.Medium,
        color =
          if (isFilled) {
            MaterialTheme.colorScheme.onSurface
          } else if (canSelect) {
            MaterialTheme.colorScheme.onSurface
          } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          },
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "• ${category.description}",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }

    when {
      isFilled -> {
        Text(
          text = "$actualScore",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }
      canSelect && potentialScore != null -> {
        Surface(
          shape = RoundedCornerShape(8.dp),
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
              MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
              ),
            color =
              if (potentialScore > 0) {
                MaterialTheme.colorScheme.onPrimaryContainer
              } else {
                MaterialTheme.colorScheme.onSurfaceVariant
              },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
          )
        }
      }
      else -> {
        Text(
          text = "—",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.outline,
        )
      }
    }
  }
}
