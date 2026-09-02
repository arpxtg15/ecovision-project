package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CompletedChallengeEntity
import com.example.data.model.EcoChallenge
import com.example.data.model.EcoTip
import com.example.data.model.TipTimeContext
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoGreenPrimaryDark
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.EcoMintBorder
import com.example.ui.theme.EcoMintContainer
import com.example.ui.theme.EcoMintContainerLow
import com.example.ui.theme.LightCreamGreen

@Composable
fun EcoTipsAndChallengesScreen(
    tips: List<EcoTip>,
    challenges: List<EcoChallenge>,
    selectedContext: TipTimeContext,
    challengeProgress: List<CompletedChallengeEntity>,
    feedbackMessage: String?,
    onSelectContext: (TipTimeContext) -> Unit,
    onCompleteTip: (EcoTip) -> Unit,
    onAdvanceChallenge: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("tips_habits_screen"),
        contentPadding = PaddingValues(bottom = 84.dp)
    ) {
        // 1. Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Daily Eco Habits",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Actionable tips and habit challenges for everyday living",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. Feedback Banner in Light Green
        if (feedbackMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = EcoMintContainer,
                    border = BorderStroke(1.dp, EcoMintBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feedbackMessage,
                            color = EcoGreenPrimaryDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 3. Time Context Filters
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(TipTimeContext.values()) { ctx ->
                        val isSelected = selectedContext == ctx
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectContext(ctx) },
                            label = { Text("${ctx.iconEmoji} ${ctx.label}", fontSize = 12.sp) },
                            shape = RoundedCornerShape(12.dp),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = EcoMintBorder,
                                selectedBorderColor = EcoGreenPrimary
                            ),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EcoMintContainer,
                                selectedLabelColor = EcoGreenPrimaryDark,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }

        // 4. Contextual Tips List
        item {
            Text(
                text = "Contextual Tips",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 4.dp)
            )
        }

        items(tips) { tip ->
            CleanTipCard(
                tip = tip,
                onComplete = { onCompleteTip(tip) },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        // 5. Challenges Section
        item {
            Text(
                text = "Eco Challenges",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 4.dp)
            )
        }

        items(challenges) { challenge ->
            val progress = challengeProgress.firstOrNull { it.challengeId == challenge.id }
            val completedDays = progress?.completedDays ?: 0
            val isFinished = progress?.isFullyCompleted == true || completedDays >= challenge.durationDays
            val progressFraction = (completedDays.toFloat() / challenge.durationDays.toFloat()).coerceIn(0f, 1f)

            CleanChallengeCard(
                challenge = challenge,
                completedDays = completedDays,
                isFinished = isFinished,
                progressFraction = progressFraction,
                onAdvance = { onAdvanceChallenge(challenge.id) },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun CleanTipCard(
    tip: EcoTip,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("eco_tip_card_${tip.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, EcoMintBorder.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EcoMintContainer,
                    border = BorderStroke(1.dp, EcoMintBorder)
                ) {
                    Text(
                        text = "${tip.context.iconEmoji} ${tip.category}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimaryDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = tip.impactSnippet,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = EcoGreenSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tip.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = tip.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("complete_tip_button_${tip.id}")
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = LightCreamGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tip.actionableButtonText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightCreamGreen
                )
            }
        }
    }
}

@Composable
private fun CleanChallengeCard(
    challenge: EcoChallenge,
    completedDays: Int,
    isFinished: Boolean,
    progressFraction: Float,
    onAdvance: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("challenge_card_${challenge.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (isFinished) EcoGreenPrimary else EcoMintBorder.copy(alpha = 0.8f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(EcoMintContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = challenge.iconEmoji, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = challenge.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${challenge.durationDays}-Day Habit Sprint",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (isFinished) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EcoMintContainer,
                        border = BorderStroke(1.dp, EcoMintBorder)
                    ) {
                        Text(
                            text = "COMPLETED 🏆",
                            color = EcoGreenPrimaryDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = challenge.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Day $completedDays of ${challenge.durationDays}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(progressFraction * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenPrimary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = EcoGreenPrimary,
                trackColor = EcoMintContainer
            )

            if (!isFinished) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAdvance,
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("advance_challenge_${challenge.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Flag,
                        contentDescription = null,
                        tint = LightCreamGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Complete Day ${completedDays + 1}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightCreamGreen
                    )
                }
            }
        }
    }
}
