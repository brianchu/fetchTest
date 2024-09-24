package com.example.fetchtest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchtest.model.Item
import com.example.fetchtest.model.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * View Model manages the data from the repo and remix, filter, etc if necessary and
 * allow the view to observe it. It also accepts function call that originate from the View (UI).
 *
 * Note we keep the data filtering in view model for separation of concern,
 * testability and performance. The downsize could be more memory usage if we keep
 * the processed list and increase the view model complexity.
 *
 * I do assume we want to minimize network fetching to 1 time. so we keep
 * the raw data.
 */
class ItemsViewModel : ViewModel() {
    // Maintain a state flow
    private var _state = MutableStateFlow<State>(State.Loading)

    // public state to be observed by UI
    val state: StateFlow<State> = _state.asStateFlow()

    // no one will observe me, pure internal structure to keep track of fetched data
    private var rawData: List<Item> = emptyList()

    init {
        fetch()
    }

    private fun fetch() {
        // launch a coroutine using non UI thread so it won't block main thread
        viewModelScope.launch(Dispatchers.IO) {
            try {
                rawData = RetrofitInstance.api.getItems()
                applyFilterAndSort(filterRule)
            } catch (e: Exception) {
                _state.value = State.Fail(e.message ?: "unknown error")
            }
        }
    }

    private fun applyFilterAndSort(filterAndSortRule: (List<Item>) -> List<Item>) {
        viewModelScope.launch {
            val processedList = filterAndSortRule(rawData)
            _state.value = if (processedList.isNotEmpty()) {
                State.Success(processedList)
            } else {
                State.Empty("No item after filtered")
            }
        }
    }

    // This is the rule to filter the data, if we want flexible, we could
    // put it in the View passing in or create a set of rules for different views.
    private var filterRule: (List<Item>) -> List<Item> = { items: List<Item> ->
        items
            .filter {
                !it.name.isNullOrBlank() // no blank names or null
            }.sortedWith(compareBy<Item> { it.listId }.thenBy { it.name })
    }
}