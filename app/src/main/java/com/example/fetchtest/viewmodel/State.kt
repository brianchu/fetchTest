package com.example.fetchtest.viewmodel

import com.example.fetchtest.model.Item

/**
 * State holds the result data for UI to consume while they're observing
 * the View Model.
 */
sealed class State {
    data class Success(val items: List<Item>) : State()
    data class Fail(val reason: String) : State()
    data object Loading : State()
}