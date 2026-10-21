package it.spark.app.presentation.common

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import it.spark.app.R
import it.spark.app.presentation.theme.ExpressiveType
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Medium FAB (68x68 dp, 20dp squircle, icon only, no text).
 * This is the mandatory default FAB size. See the spark-jetpack skill, section 7.
 */
@Composable
fun MediumFAB(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 380f),
        label = "fab_spring_scale"
    )

    MediumFloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(68.dp)
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(30.dp)
        )
    }
}

/**
 * Circular Back Button housed in a circular container (40x40 dp).
 */
@Composable
fun CircularBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier.size(40.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Grouped List Container standard (Unified Card Block).
 */
@Composable
fun GroupedCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )
    }
}

/**
 * Grouped List Card with optional section title.
 */
@Composable
fun GroupedListCard(
    modifier: Modifier = Modifier,
    sectionTitle: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!sectionTitle.isNullOrBlank()) {
            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = containerColor
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }
    }
}

/**
 * Grouped List Row with squircle icon badge and optional divider.
 */
@Composable
fun GroupedListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    iconContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    showDivider: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = iconContainerColor,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = iconContentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(12.dp))
                trailingContent()
            }
        }
        if (showDivider) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * Item specification for [ExpressiveImpactNavigationBar].
 */
data class ExpressiveNavItem(
    val route: Any,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String? = null
)

/**
 * Floating Pill Dock navigation bar with multi-stop progressive ease-in gradient overlay.
 * Content smoothly scrolls underneath without visible rectangular clipping.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveImpactNavigationBar(
    items: List<ExpressiveNavItem>,
    selectedRoute: Any,
    onItemSelected: (ExpressiveNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    require(items.size in 2..4) {
        "ExpressiveImpactNavigationBar supports a strict maximum of 4 elements (provided: ${items.size}). " +
            "Condense the information architecture into at most 4 sections."
    }

    val spatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<IntSize>()
    val surfaceColor = MaterialTheme.colorScheme.surface

    // Multi-stop progressive ease-in gradient: prevents hard rectangular lines above content
    val progressiveScrim = Brush.verticalGradient(
        0.00f to Color.Transparent,
        0.20f to surfaceColor.copy(alpha = 0.08f),
        0.40f to surfaceColor.copy(alpha = 0.25f),
        0.60f to surfaceColor.copy(alpha = 0.52f),
        0.80f to surfaceColor.copy(alpha = 0.82f),
        1.00f to surfaceColor.copy(alpha = 0.98f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(progressiveScrim)
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 6.dp)
                    .animateContentSize(animationSpec = spatialSpec),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = item.route == selectedRoute
                    ExpressiveImpactNavItemView(
                        item = item,
                        isSelected = isSelected,
                        onClick = { onItemSelected(item) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpressiveImpactNavItemView(
    item: ExpressiveNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Fast 80ms effects for snappy pill color transitions
    val fastEffectsColor = tween<Color>(durationMillis = 80)
    val fastEffectsFloat = tween<Float>(durationMillis = 80)
    val spatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<IntSize>()

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.surfaceContainerHighest
        } else {
            Color.Transparent
        },
        animationSpec = fastEffectsColor,
        label = "pillContainerColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = fastEffectsColor,
        label = "pillContentColor"
    )

    Surface(
        shape = CircleShape,
        color = containerColor,
        modifier = modifier
            .height(48.dp)
            .clip(CircleShape)
            .clickable(
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = if (isSelected) 18.dp else 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Icon appears on selection
            AnimatedVisibility(
                visible = isSelected,
                enter = expandHorizontally(animationSpec = spatialSpec) + fadeIn(animationSpec = fastEffectsFloat),
                exit = shrinkHorizontally(animationSpec = spatialSpec) + fadeOut(animationSpec = fastEffectsFloat)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.contentDescription,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Text(
                text = item.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

/**
 * Fixed Bottom Action Button with a multi-stop soft progressive ease-in gradient mask.
 */
@Composable
fun ExpressivePrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = Icons.Rounded.PlayArrow,
    enabled: Boolean = true
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val progressiveScrim = Brush.verticalGradient(
        0.00f to Color.Transparent,
        0.20f to surfaceColor.copy(alpha = 0.08f),
        0.40f to surfaceColor.copy(alpha = 0.25f),
        0.60f to surfaceColor.copy(alpha = 0.52f),
        0.80f to surfaceColor.copy(alpha = 0.82f),
        1.00f to surfaceColor.copy(alpha = 0.98f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(progressiveScrim)
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 * Pill Search Top Bar (72dp container with 56dp inner pill).
 */
@Composable
fun AppSearchTopBar(
    placeholderText: String,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    profileContentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(72.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceBright,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(CircleShape)
                .clickable(onClick = onSearchClick)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = placeholderText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = profileContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Full-screen Search View with automatic keyboard focus on open and doubled-speed (80ms) fade.
 */
@Composable
fun AppSearchView(
    expanded: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    placeholderText: String,
    backContentDescription: String,
    clearContentDescription: String,
    recentSearches: List<String> = emptyList(),
    onRecentSearchClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    resultsContent: @Composable ColumnScope.() -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Auto-focus the search field and display the soft keyboard on the first click
    LaunchedEffect(expanded) {
        if (expanded) {
            focusRequester.requestFocus()
            keyboardController?.show()
        } else {
            keyboardController?.hide()
        }
    }

    BackHandler(enabled = expanded) {
        onExpandedChange(false)
    }

    // Doubled-speed fade animation (80ms)
    val fastFadeSpec = tween<Float>(durationMillis = 80)

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(animationSpec = fastFadeSpec),
        exit = fadeOut(animationSpec = fastFadeSpec)
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onExpandedChange(false) },
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = backContentDescription
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = placeholderText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                        )
                    }

                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = clearContentDescription,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                if (query.isBlank() && recentSearches.isNotEmpty()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        GroupedListCard {
                            recentSearches.forEachIndexed { index, item ->
                                GroupedListRow(
                                    title = item,
                                    leadingIcon = Icons.Rounded.History,
                                    iconContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    iconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    showDivider = index < recentSearches.lastIndex,
                                    onClick = { onRecentSearchClick(item) }
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        content = resultsContent
                    )
                }
            }
        }
    }
}

/**
 * Bento Hero Card with large metrics and LinearWavyProgressIndicator.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BentoHeroCard(
    metricTitle: String,
    primaryValue: Int,
    totalValue: Int,
    percentage: Float,
    statusText: String,
    modifier: Modifier = Modifier
) {
    val isCompleted = primaryValue == totalValue && totalValue > 0

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = metricTitle.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHighest
                    }
                ) {
                    Text(
                        text = "${(percentage.coerceIn(0f, 1f) * 100).toInt()}%",
                        style = ExpressiveType.subtitleExpressive,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = primaryValue.toString(),
                    style = ExpressiveType.heroGiant,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = " / $totalValue",
                    style = ExpressiveType.subtitleExpressive,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearWavyProgressIndicator(
                progress = { percentage.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
    }
}

/**
 * 3-Column Bento strip with quick KPI metrics.
 */
@Composable
fun QuickMetricsBentoStrip(
    totalItems: Int,
    healthyItems: Int,
    alertItems: Int,
    totalLabel: String,
    healthyLabel: String,
    alertLabel: String,
    modifier: Modifier = Modifier
) {
    val hasAlerts = alertItems > 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = totalLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = totalItems.toString(),
                    style = ExpressiveType.heroLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = healthyLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = healthyItems.toString(),
                    style = ExpressiveType.heroLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (hasAlerts) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = alertLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (hasAlerts) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = alertItems.toString(),
                    style = ExpressiveType.heroLarge,
                    color = if (hasAlerts) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

/**
 * Contextual greeting header based on current system time.
 */
@Composable
fun ContextualGreetingHeader(
    morningGreeting: String,
    afternoonGreeting: String,
    eveningGreeting: String,
    modifier: Modifier = Modifier
) {
    val currentHour = remember { LocalTime.now().hour }
    val greeting = when (currentHour) {
        in 5..12 -> morningGreeting
        in 13..17 -> afternoonGreeting
        else -> eveningGreeting
    }

    val todayFormatted = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
        ).replaceFirstChar { it.uppercase() }
    }

    Column(modifier = modifier) {
        Text(
            text = greeting,
            style = ExpressiveType.titleExpressive,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = todayFormatted,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
