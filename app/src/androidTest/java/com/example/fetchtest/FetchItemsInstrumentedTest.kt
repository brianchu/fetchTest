package com.example.fetchtest

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fetchtest.model.FetchApi
import com.example.fetchtest.model.Item
import com.example.fetchtest.viewmodel.ItemsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented UI test, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4::class)
class FetchItemsInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val mockApi = mockk<FetchApi>()

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.fetchtest", appContext.packageName)
    }

    @Test
    fun testLoadingState() {
        coEvery { mockApi.getItems() } coAnswers { emptyList() } // No items yet, so loading will be shown

        // When
        val viewModel = ItemsViewModel(mockApi)

        // Sets the content with the mocked view model
        composeTestRule.setContent {
           FetchListScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Fetch items").assertIsDisplayed()
    }

    @Test
    fun testFailState() {
        // Mock the API to throw an exception
        coEvery { mockApi.getItems() } throws Exception("Network error")

        // When
        val viewModel = ItemsViewModel(mockApi)

        // Sets the content with the mocked view model
        composeTestRule.setContent {
            FetchListScreen(viewModel = viewModel)
        }

        // Let the coroutine finish
        composeTestRule.waitForIdle()

        // Assert that the fail state message is displayed
        composeTestRule.onNodeWithTag("fail-message").assertIsDisplayed()
    }

    @Test
    fun testSimpleBaseCaseWithData() {
        // Mocks the API to return a simple items
        coEvery { mockApi.getItems() } returns listOf(Item(id = 1, name = "name1", listId = 1))

        // When
        val viewModel = ItemsViewModel(mockApi)

        // Sets the content with the mocked view model
        composeTestRule.setContent {
            FetchListScreen(viewModel = viewModel)
        }

        // Let the coroutine finish
        composeTestRule.waitForIdle()

        // Assert that the view is displayed
        composeTestRule.onNodeWithTag("main-column").assertIsDisplayed()
    }
}