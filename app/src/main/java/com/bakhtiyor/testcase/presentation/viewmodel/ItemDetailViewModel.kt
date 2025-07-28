package com.bakhtiyor.testcase.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakhtiyor.testcase.domain.model.CatalogItem
import com.bakhtiyor.testcase.domain.usecase.GetItemDetailUseCase
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val getItemDetailUseCase: GetItemDetailUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(ItemDetailUiState())
        private set

    fun loadItemDetail(itemId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val item = getItemDetailUseCase(itemId)
                uiState =
                    uiState.copy(
                        isLoading = false,
                        item = item,
                        error = if (item == null) ItemDetailError.ItemNotFound else null,
                    )
            } catch (exception: Exception) {
                uiState =
                    uiState.copy(
                        isLoading = false,
                        error = ItemDetailError.NetworkError(exception.message),
                    )
            }
        }
    }
}

data class ItemDetailUiState(
    val isLoading: Boolean = false,
    val item: CatalogItem? = null,
    val error: ItemDetailError? = null,
)

sealed class ItemDetailError {
    object ItemNotFound : ItemDetailError()

    data class NetworkError(val message: String?) : ItemDetailError()
} 
