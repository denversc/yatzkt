package com.yatzkt.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatzkt.core.Die

@Composable
fun DieView(die: Die, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
  val shape = RoundedCornerShape(12.dp)
  val isBlank = die.value == null

  val cardColors =
    if (die.isHeld) {
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
      )
    } else {
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

  val borderStroke =
    if (die.isHeld) {
      BorderStroke(2.5.dp, MaterialTheme.colorScheme.primary)
    } else if (isBlank) {
      BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    } else {
      BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    }

  val accessibilityDescription =
    if (isBlank) {
      "Unrolled die"
    } else {
      "Die showing ${die.value}${if (die.isHeld) ", held" else ""}"
    }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp),
    modifier = modifier.semantics { contentDescription = accessibilityDescription },
  ) {
    Card(
      shape = shape,
      colors = cardColors,
      border = borderStroke,
      elevation = CardDefaults.cardElevation(defaultElevation = if (die.isHeld) 6.dp else 2.dp),
      modifier = Modifier.size(58.dp).clickable(enabled = enabled, onClick = onClick),
    ) {
      Box(contentAlignment = Alignment.Center, modifier = Modifier.size(58.dp)) {
        val value = die.value
        if (value != null) {
          DieFace(
            value = value,
            pipColor =
              if (die.isHeld) {
                MaterialTheme.colorScheme.primary
              } else {
                MaterialTheme.colorScheme.onSurfaceVariant
              },
            modifier = Modifier.size(44.dp),
          )
        }
      }
    }

    // Held status indicator
    Surface(
      shape = RoundedCornerShape(4.dp),
      color = if (die.isHeld) MaterialTheme.colorScheme.primary else Color.Transparent,
      modifier = Modifier.padding(horizontal = 2.dp),
    ) {
      Text(
        text = if (die.isHeld) "HELD" else " ",
        style =
          MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
        color = if (die.isHeld) MaterialTheme.colorScheme.onPrimary else Color.Transparent,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
      )
    }
  }
}

@Composable
fun DieFace(value: Int, pipColor: Color, modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val width = size.width
    val height = size.height
    val pipRadius = width * 0.095f

    val left = width * 0.25f
    val center = width * 0.5f
    val right = width * 0.75f

    val top = height * 0.25f
    val middle = height * 0.5f
    val bottom = height * 0.75f

    fun drawPip(x: Float, y: Float) {
      drawCircle(color = pipColor, radius = pipRadius, center = Offset(x, y))
    }

    when (value) {
      1 -> {
        drawPip(center, middle)
      }
      2 -> {
        drawPip(left, top)
        drawPip(right, bottom)
      }
      3 -> {
        drawPip(left, top)
        drawPip(center, middle)
        drawPip(right, bottom)
      }
      4 -> {
        drawPip(left, top)
        drawPip(right, top)
        drawPip(left, bottom)
        drawPip(right, bottom)
      }
      5 -> {
        drawPip(left, top)
        drawPip(right, top)
        drawPip(center, middle)
        drawPip(left, bottom)
        drawPip(right, bottom)
      }
      6 -> {
        drawPip(left, top)
        drawPip(right, top)
        drawPip(left, middle)
        drawPip(right, middle)
        drawPip(left, bottom)
        drawPip(right, bottom)
      }
    }
  }
}
