package com.example.fetchtest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.fetchtest.model.FetchApi
import com.example.fetchtest.model.Item
import com.example.fetchtest.viewmodel.ItemsViewModel
import com.example.fetchtest.viewmodel.State
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * ItemsViewModel Test
 */
@ExperimentalCoroutinesApi
class ItemsViewModelTest {

    // Use different executor
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ItemsViewModel

    private val mockApi = mockk<FetchApi>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test successful items retrieval`() = runTest {
        val mockItems = listOf(
            Item(id = 1, listId = 1, name = "Item 1"),
            Item(id = 2, listId = 2, name = "Item 2")
        )
        coEvery { mockApi.getItems() } returns mockItems
        viewModel = ItemsViewModel(mockApi)

        // Make sure all coroutine complete
        advanceUntilIdle()

        // Then suspends wait the Success event
        val state = viewModel.state.first { it is State.Success }
        assertEquals(State.Success(mockItems), state)
    }

    @Test
    fun `test show empty in fetching when items is empty`() = runTest {
        // given
        coEvery { mockApi.getItems() } returns emptyList()

        // when
        viewModel = ItemsViewModel(mockApi)

        // and make sure all coroutine complete
        advanceUntilIdle()

        // Then suspends wait the Success event
        val state = viewModel.state.first { it is State.Empty }

        assertTrue(state is State.Empty)
    }

    @Test
    fun `test failure in fetching items because network error`() = runTest {
        // given
        coEvery { mockApi.getItems() } throws Exception("network error")

        // when
        viewModel = ItemsViewModel(mockApi)

        // and make sure all coroutine complete
        advanceUntilIdle()

        // Then suspends wait the Success event
        val state = viewModel.state.first { it is State.Fail }
        assertTrue(state is State.Fail) // as long as there is one, don't care message since it could be updated error message
    }

}