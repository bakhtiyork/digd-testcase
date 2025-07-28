package com.bakhtiyor.testcase.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bakhtiyor.testcase.presentation.screen.CatalogListScreen
import com.bakhtiyor.testcase.presentation.screen.ItemDetailScreen

object NavDestinations {
    const val CATALOG_LIST = "catalog_list"
    const val ITEM_DETAIL = "item_detail/{itemId}"

    fun itemDetail(itemId: String) = "item_detail/$itemId"
}

@Composable
fun CatalogNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = NavDestinations.CATALOG_LIST,
    ) {
        composable(NavDestinations.CATALOG_LIST) {
            CatalogListScreen(
                onItemClick = { item ->
                    navController.navigate(NavDestinations.itemDetail(item.id))
                },
            )
        }

        composable(NavDestinations.ITEM_DETAIL) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            ItemDetailScreen(
                itemId = itemId,
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
} 
