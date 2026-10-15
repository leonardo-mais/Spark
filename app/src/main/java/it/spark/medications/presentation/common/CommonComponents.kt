package it.spark.medications.presentation.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Healing
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Vaccines
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.spark.medications.R
import it.spark.medications.domain.model.MedicineForm
import it.spark.medications.domain.model.NotificationPriority

/**
 * Standard Medium FAB (68x68 dp, squircle 20dp, NO TEXT).
 * Regola del default medio obbligatorio da standard di sviluppo.
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

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(68.dp)
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 3.dp,
            pressedElevation = 6.dp
        ),
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
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.cancel),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Unified Card Block standard (16.dp rounded corner, surfaceContainerLow).
 */
@Composable
fun GroupedCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
 * Form Icon badge in squircle container.
 */
@Composable
fun MedicineFormBadge(
    form: MedicineForm,
    modifier: Modifier = Modifier
) {
    val (icon, tint, bg) = when (form) {
        MedicineForm.PILL -> Triple(Icons.Rounded.Medication, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
        MedicineForm.CAPSULE -> Triple(Icons.Rounded.Healing, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer)
        MedicineForm.DROPS -> Triple(Icons.Rounded.WaterDrop, MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.tertiaryContainer)
        MedicineForm.SYRUP -> Triple(Icons.Rounded.LocalDrink, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
        MedicineForm.INJECTION -> Triple(Icons.Rounded.Vaccines, MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
        MedicineForm.SACHET -> Triple(Icons.AutoMirrored.Rounded.ReceiptLong, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer)
        MedicineForm.INHALER -> Triple(Icons.Rounded.Air, MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.tertiaryContainer)
        MedicineForm.OTHER -> Triple(Icons.Rounded.MedicalServices, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surfaceContainerHigh)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg,
        modifier = modifier.size(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = form.name,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Priority Chip / Badge.
 */
@Composable
fun PriorityBadge(
    priority: NotificationPriority,
    modifier: Modifier = Modifier
) {
    val (textRes, bg, fg) = when (priority) {
        NotificationPriority.LOW -> Triple(
            R.string.editor_priority_low,
            MaterialTheme.colorScheme.surfaceContainerHigh,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        NotificationPriority.MEDIUM -> Triple(
            R.string.editor_priority_medium,
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
        NotificationPriority.HIGH -> Triple(
            R.string.editor_priority_high,
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        modifier = modifier
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
