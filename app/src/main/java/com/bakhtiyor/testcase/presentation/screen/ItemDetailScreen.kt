package com.bakhtiyor.testcase.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bakhtiyor.testcase.R
import com.bakhtiyor.testcase.domain.model.CatalogItem
import com.bakhtiyor.testcase.presentation.common.rememberConfidenceDisplayInfo
import com.bakhtiyor.testcase.presentation.component.ErrorScreen
import com.bakhtiyor.testcase.presentation.viewmodel.ItemDetailError
import com.bakhtiyor.testcase.presentation.viewmodel.ItemDetailViewModel
import com.bakhtiyor.testcase.ui.theme.Purple80
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemDetailViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState

    LaunchedEffect(itemId) {
        viewModel.loadItemDetail(itemId)
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.title_details),
                    fontWeight = FontWeight.Bold,
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.button_back),
                    )
                }
            },
        )

        // Content
        when {
            uiState.isLoading -> {
                LoadingContent(modifier = Modifier.fillMaxSize())
            }
            uiState.error != null -> {
                val errorMessage =
                    when (uiState.error) {
                        is ItemDetailError.ItemNotFound -> stringResource(R.string.error_item_not_found)
                        is ItemDetailError.NetworkError -> uiState.error.message ?: stringResource(R.string.error_unknown)
                    }
                ErrorScreen(
                    message = errorMessage,
                    onRetry = { viewModel.loadItemDetail(itemId) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            uiState.item != null -> {
                ItemDetailContent(
                    item = uiState.item,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun ItemDetailContent(
    item: CatalogItem,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Image Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.text,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Purple80.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                )
                ConfidenceCard(
                    confidence = item.confidence,
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                )
            }
        }

        // Details Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Text
                DetailRow(
                    label = stringResource(R.string.text_description),
                    value = item.text,
                )

                // ID
                DetailRow(
                    label = stringResource(R.string.text_id),
                    value = item.id,
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ConfidenceCard(
    confidence: Double,
    modifier: Modifier = Modifier,
) {
    val displayInfo = rememberConfidenceDisplayInfo(confidence = confidence, hasBorder = true)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = displayInfo.surfaceColor,
        border = displayInfo.borderStroke,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "${displayInfo.percentage}%",
                style = MaterialTheme.typography.headlineMedium,
                color = displayInfo.textColor,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Confidence",
                style = MaterialTheme.typography.labelMedium,
                color = displayInfo.textColor.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.text_loading_item_details),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ItemDetailContentPreview() {
    val item =
        CatalogItem(
            id = "123",
            text = "Sample Item Title - This is a long title that should wrap or ellipsis",
            imageUrl = "https://placehold.co/512x512?text=00.%20abcd",
            confidence = 0.85,
        )
    MaterialTheme {
        ItemDetailContent(item = item)
    }
}
