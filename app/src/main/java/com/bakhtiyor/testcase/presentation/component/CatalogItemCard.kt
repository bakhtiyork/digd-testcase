package com.bakhtiyor.testcase.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bakhtiyor.testcase.R
import com.bakhtiyor.testcase.domain.model.CatalogItem
import com.bakhtiyor.testcase.presentation.common.rememberConfidenceDisplayInfo
import com.bakhtiyor.testcase.ui.theme.Purple80

@Composable
fun CatalogItemCard(
    item: CatalogItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Image
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.text,
                    modifier =
                        Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Purple80.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.broken_image),
                )

                // Content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = item.text,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.text_id) + item.id,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        ConfidenceBadge(confidence = item.confidence)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfidenceBadge(
    confidence: Double,
    modifier: Modifier = Modifier,
) {
    val displayInfo = rememberConfidenceDisplayInfo(confidence = confidence, hasBorder = false)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = displayInfo.surfaceColor,
        border = displayInfo.borderStroke,
    ) {
        Text(
            text = "${displayInfo.percentage}%",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = displayInfo.textColor,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogItemCardPreview() {
    CatalogItemCard(
        item =
            CatalogItem(
                id = "123",
                text = "Sample Item Title - This is a long title that should wrap or ellipsis",
                imageUrl = "https://placehold.co/512x512?text=00.%20abcd",
                confidence = 0.85,
            ),
        onClick = {},
    )
}
