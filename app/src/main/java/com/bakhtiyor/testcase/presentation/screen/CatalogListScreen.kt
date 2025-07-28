package com.bakhtiyor.testcase.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bakhtiyor.testcase.R
import com.bakhtiyor.testcase.domain.model.CatalogItem
import com.bakhtiyor.testcase.presentation.component.CatalogItemCard
import com.bakhtiyor.testcase.presentation.component.ErrorScreen
import com.bakhtiyor.testcase.presentation.viewmodel.CatalogListViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogListScreen(
    onItemClick: (CatalogItem) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CatalogListViewModel = koinViewModel(),
) {
    val catalogItems = viewModel.catalogItems.collectAsLazyPagingItems()

    val isRefreshing = catalogItems.loadState.refresh is LoadState.Loading

    Column(modifier = modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.title_catalog),
                    fontWeight = FontWeight.Bold,
                )
            },
        )

        // Content with Pull-to-Refresh
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { catalogItems.refresh() },
            modifier = Modifier.fillMaxSize(),
        ) {
            CatalogContent(
                catalogItems = catalogItems,
                onItemClick = onItemClick,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun CatalogContent(
    catalogItems: LazyPagingItems<CatalogItem>,
    onItemClick: (CatalogItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(catalogItems.itemCount) { index ->
                catalogItems[index]?.let { item ->
                    CatalogItemCard(
                        item = item,
                        onClick = { onItemClick(item) },
                    )
                }
            }

            // Loading state for append
            catalogItems.apply {
                when (loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            LoadingItem()
                        }
                    }

                    is LoadState.Error -> {
                        item {
                            ErrorScreen(
                                message = stringResource(R.string.error_failed_to_load_more_items),
                                onRetry = { retry() },
                            )
                        }
                    }

                    is LoadState.NotLoading -> {}
                }
            }
        }

        // Initial loading state (only show when no items are loaded yet)
        if (catalogItems.loadState.refresh is LoadState.Loading && catalogItems.itemCount == 0) {
            LoadingScreen(modifier = Modifier.align(Alignment.Center))
        }

        // Error state (only show when no items are loaded and there's an error)
        if (catalogItems.loadState.refresh is LoadState.Error && catalogItems.itemCount == 0) {
            ErrorScreen(
                message = stringResource(R.string.error_failed_to_load_items),
                onRetry = { catalogItems.retry() },
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun LoadingItem(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CircularProgressIndicator()
        Text(
            text = stringResource(R.string.text_loading_catalog_items),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
