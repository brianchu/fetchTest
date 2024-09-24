package com.example.fetchtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fetchtest.model.Item
import com.example.fetchtest.ui.theme.FetchTestTheme
import com.example.fetchtest.viewmodel.ItemsViewModel
import com.example.fetchtest.viewmodel.State

/**
 * Display all the items grouped by "listId"
 * Sort the results first by "listId" then by "name" when displaying.
 * Filter out any items where "name" is blank or null.
 *
 * Assumption
 * - sort lexicographically (if not need to parse the item XXX as numerical )
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

    // Injects view model with the help from library
    val viewModel: ItemsViewModel = viewModel()

    // Observes the flow
    // Enhance: observing same flow but I only care about this filtered items
    val state = viewModel.state.collectAsStateWithLifecycle()

    // Sets group number bg color so user can see it clearly
    val colors = listOf(Color.Red, Color.Blue, Color.Yellow, Color.DarkGray, Color.LightGray)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Always a title
        Text(
            modifier = Modifier,
            text = "Fetch items"
        )

        // we show different thing according to each state
        when (val result = state.value) {
            is State.Fail -> {
                Text(
                    modifier = Modifier,
                    text = "Fetch fail ${result.reason}",
                )
            }

            State.Loading -> {
                CircularProgressIndicator()
            }

            is State.Empty -> {
                Text(
                    modifier = Modifier,
                    text = "Empty list: ${result.reason}",
                )
            }

            is State.Success -> {
                val items: List<Item> = result.items
                LazyColumn {
                    items(items) { item ->
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors[item.listId % colors.size].copy(alpha = 0.5f))
                            .padding(12.dp)
                        ) {
                            Text(text = "id    : ${item.id}")
                            Text(text = "listId: ${item.listId}")
                            Text(text = "name  : ${item.name ?:""}")
                        }
                    }
                }
            }
        }
    }
}
