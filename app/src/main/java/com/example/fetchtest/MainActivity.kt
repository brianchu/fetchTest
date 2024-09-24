package com.example.fetchtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fetchtest.model.Item
import com.example.fetchtest.ui.theme.FetchTestTheme
import com.example.fetchtest.viewmodel.ItemsViewModel
import com.example.fetchtest.viewmodel.State

/**
 * Display all the items grouped by "listId"
 * Sort the results first by "listId" then by "name" when displaying.
 * Filter out any items where "name" is blank or null.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FetchTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FetchListScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FetchListScreen(modifier: Modifier = Modifier) {

    // inject view model with the help from library
    val viewModel: ItemsViewModel = viewModel()

    // observing the flow
    val state = viewModel.state.collectAsState()

    viewModel.fetch()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // we show different thing according to each state

        // Always a title
        Text(
            modifier = Modifier,
            text = "Fetch items"
        )

        when (val result = state.value) {
            is State.Fail -> {
                Text(
                    modifier = Modifier,
                    text = "Fetch fail",
                )
                // todo probably retry auto or manually
            }

            State.Loading -> {
                CircularProgressIndicator()
            }

            is State.Success -> {
                val items: List<Item> = result.items
                LazyColumn {
                    items(items) { item ->
                        Row() {
                            Text(text = "${item.id}")
                            Text(text = "${item.listId}")
                            Text(text = item.name ?: "")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FetchTestTheme {
        FetchListScreen()
    }
}