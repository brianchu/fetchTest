@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.example.fetchtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fetchtest.model.Item
import com.example.fetchtest.ui.theme.FetchTestTheme
import com.example.fetchtest.ui.theme.onPrimaryContainerLight
import com.example.fetchtest.ui.theme.primaryContainerLight
import com.example.fetchtest.ui.theme.secondaryContainerLight
import com.example.fetchtest.ui.theme.tertiaryContainerLight
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

/**
 * note we pass in viewModel easy for testing, we could also use other inject lib
 */
@Composable
fun FetchListScreen(modifier: Modifier = Modifier, viewModel: ItemsViewModel = viewModel()) {

    // Observes the flow
    // Enhance: observing same flow but I only care about this filtered items
    val state = viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Always a title
        Text(
            modifier = Modifier.testTag("title"),
            text = "Fetch items"
        )

        // we show different thing according to each state
        when (val result = state.value) {
            is State.Fail -> {
                Text(
                    modifier = Modifier.testTag("fail-message"),
                    text = "Fetch fail ${result.reason}",
                )
            }

            State.Loading -> {
                CircularProgressIndicator(modifier = Modifier.testTag("loading"))
            }

            is State.Empty -> {
                Text(
                    modifier = Modifier.testTag("empty-message"),
                    text = "Empty list: ${result.reason}",
                )
            }

            is State.Success -> {
                ItemsScreen(result.items)
            }
        }
    }
}

@Composable
fun ItemsScreen(items: List<Item>) {

    // Sets group number bg color so user can see it clearly
    val colors = listOf(primaryContainerLight, secondaryContainerLight, tertiaryContainerLight)

    // convert the items to a group (by listId) and their items
    val grouped: Map<Int, List<Item>> = remember(items) { items.groupBy { it.listId } }

    CompositionLocalProvider(LocalContentColor provides onPrimaryContainerLight) {
        LazyColumn(modifier = Modifier.testTag("main-column")) {
            // now give each group (and its items) to header and items row
            grouped.forEach { (listId, items) ->
                // picks a color from out list according the list id
                val color = colors[listId % colors.size]
                // render the header
                stickyHeader {
                    Header(listId, color)
                }
                // render the items
                items(items) { item: Item ->
                    ItemRow(item, color)
                }
            }
        }
    }
}

/**
 * This is the little sticky header.
 */
@Composable
fun Header(listId: Int, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
    ) {
        Text(
            modifier = Modifier
                .padding(5.dp),
            text = "LIST ID GROUP: $listId",
            fontStyle = FontStyle.Italic
        )
    }
}


@Composable
fun ItemRow(item: Item, color: Color) {
    Column(
        modifier = Modifier
            .testTag("item-row-${item.id}")
            .fillMaxWidth()
            .padding(6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .padding(12.dp)
    ) {
        Text(text = "id    : ${item.id}")
        Text(text = "listId: ${item.listId}")
        Text(text = "name  : ${item.name ?: ""}")
    }
}