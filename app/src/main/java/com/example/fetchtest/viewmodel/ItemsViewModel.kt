package com.example.fetchtest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchtest.model.Item
import com.example.fetchtest.model.Items
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
 */
class ItemsViewModel : ViewModel() {
    // Maintain a state flow
    private var _state = MutableStateFlow<State>(State.Loading)
    // public state to be observed by UI
    val state: StateFlow<State> = _state.asStateFlow()

    fun fetch() {
        // launch a coroutine using non UI thread so it won't block main thread
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: List<Item> = RetrofitInstance.api.getItems()
                _state.value = State.Success(result)
            } catch (e: Exception) {
                _state.value = State.Fail(e.localizedMessage ?: "unknown error")
            }
        }
    }
}