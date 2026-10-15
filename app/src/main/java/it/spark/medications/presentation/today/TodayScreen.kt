package it.spark.medications.presentation.today

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.spark.medications.R
import it.spark.medications.domain.model.DailyDoseItem
import it.spark.medications.domain.model.DoseStatus
import it.spark.medications.domain.model.NotificationPriority
import it.spark.medications.presentation.common.GroupedCard
import it.spark.medications.presentation.common.MedicineFormBadge
import it.spark.medications.presentation.common.PriorityBadge
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.remember
import it.spark.medications.presentation.theme.ExpressiveType
import it.spark.medications.presentation.theme.FlexHeadlineFont
import it.spark.medications.presentation.theme.FlexSubheadingFont
import java.time.LocalTime
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val currentHour = remember { LocalTime.now().hour }
    val greetingText = when (currentHour) {
        in 5..12 -> stringResource(R.string.greeting_morning)
        in 13..17 -> stringResource(R.string.greeting_afternoon)
        else -> stringResource(R.string.greeting_evening)
    }

    val todayFormattedSubtitle = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
        ).replaceFirstChar { it.uppercase() }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                TodayEffect.NavigateToSettings -> onNavigateToSettings()
                is TodayEffect.ShowSnackbar -> {}
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = greetingText,
                            style = ExpressiveType.greetingHeadline,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = todayFormattedSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onIntent(TodayIntent.OpenSettings) }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.settings_title),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Week Bar (Horizontal Strip) with Expressive Variable Font Numerals
            WeekDaysStrip(
                weekDays = uiState.weekDays,
                onSelectDate = { date -> viewModel.onIntent(TodayIntent.SelectDate(date)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Bento Hero Adherence Card with Giant Dynamic Numerals & Wavy Progress
            AdherenceBanner(
                takenCount = uiState.takenCount,
                totalCount = uiState.totalCount,
                progress = uiState.progress,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Daily Schedule List or Empty State
            if (uiState.totalCount == 0) {
                EmptyTodayDoses(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.doseItems,
                        key = { "${it.medicine.id}_${it.schedule.id}" }
                    ) { item ->
                        DoseScheduleCard(
                            item = item,
                            onTake = { viewModel.onIntent(TodayIntent.MarkTaken(item)) },
                            onSkip = { viewModel.onIntent(TodayIntent.MarkSkipped(item)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeekDaysStrip(
    weekDays: List<WeekDayItem>,
    onSelectDate: (java.time.LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDays.forEach { dayItem ->
            val dayLetter = when (dayItem.dayOfWeekNumber) {
                1 -> stringResource(R.string.day_mon)
                2 -> stringResource(R.string.day_tue)
                3 -> stringResource(R.string.day_wed)
                4 -> stringResource(R.string.day_thu)
                5 -> stringResource(R.string.day_fri)
                6 -> stringResource(R.string.day_sat)
                else -> stringResource(R.string.day_sun)
            }

            val isSelected = dayItem.isSelected
            val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceContainerLow
            val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurface

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = bgColor,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onSelectDate(dayItem.date) }
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayLetter.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = if (isSelected) 0.9f else 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dayItem.date.dayOfMonth.toString(),
                        style = if (isSelected) ExpressiveType.dateStripSelected else ExpressiveType.dateStripUnselected,
                        color = contentColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (dayItem.isToday) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    } else {
                        Spacer(modifier = Modifier.size(5.dp))
                    }
                }
            }
        }
    }
}

private fun Modifier.background(color: Color) = this.then(
    Modifier.padding(0.dp)
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AdherenceBanner(
    takenCount: Int,
    totalCount: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    GroupedCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        val statusMessage = when {
            totalCount == 0 -> stringResource(R.string.today_no_doses)
            takenCount == totalCount -> stringResource(R.string.today_progress_complete)
            takenCount == 0 -> stringResource(R.string.status_pending)
            else -> "$takenCount / $totalCount completati"
        }

        // Top Row: Section Label + Percentage Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.today_adherence_label),
                style = ExpressiveType.statLabel,
                color = MaterialTheme.colorScheme.primary
            )
            if (totalCount > 0) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (takenCount == totalCount) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = ExpressiveType.heroMedium,
                        color = if (takenCount == totalCount) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Hero Numbers Row: Giant Expressive Fraction
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = takenCount.toString(),
                style = ExpressiveType.heroGiant,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = " / $totalCount",
                style = ExpressiveType.heroMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Iconic Material 3 Expressive LinearWavyProgressIndicator
        LinearWavyProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    }
}

@Composable
fun DoseScheduleCard(
    item: DailyDoseItem,
    onTake: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val medicine = item.medicine
    val schedule = item.schedule
    val status = item.status

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                DoseStatus.TAKEN -> MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.85f)
                DoseStatus.SKIPPED -> MaterialTheme.colorScheme.surfaceContainerLowest
                else -> MaterialTheme.colorScheme.surfaceContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MedicineFormBadge(form = medicine.form)

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = medicine.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontFamily = FlexHeadlineFont),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (medicine.priority == NotificationPriority.HIGH) {
                            Spacer(modifier = Modifier.width(8.dp))
                            PriorityBadge(priority = NotificationPriority.HIGH)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = schedule.formattedTime,
                                    style = ExpressiveType.clockTag,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = medicine.dosage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (medicine.instructions.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = medicine.instructions,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status or Action Buttons
            when (status) {
                DoseStatus.PENDING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onSkip) {
                            Text(
                                text = stringResource(R.string.action_skip),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        FilledTonalButton(
                            onClick = onTake,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.action_take),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                DoseStatus.TAKEN -> {
                    val timeStr = if (item.log?.actionTimestamp != null) {
                        val instant = java.time.Instant.ofEpochMilli(item.log.actionTimestamp)
                        val localTime = instant.atZone(java.time.ZoneId.systemDefault()).toLocalTime()
                        "%02d:%02d".format(localTime.hour, localTime.minute)
                    } else {
                        schedule.formattedTime
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.status_taken, timeStr),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                DoseStatus.SKIPPED -> {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.status_skipped),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                DoseStatus.MISSED -> {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = stringResource(R.string.status_skipped),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTodayDoses(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.today_no_doses),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
