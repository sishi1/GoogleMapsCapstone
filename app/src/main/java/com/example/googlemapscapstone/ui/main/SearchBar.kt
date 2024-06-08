package com.example.googlemapscapstone.ui.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onSearch: (String) -> Unit,
    enabled: Boolean
) {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val items = remember {
        mutableStateListOf(
            "Amsterdam",
            "Voorschoten"
        )
    }
    // Need the entire namespace otherwise it breaks the entire function
    androidx.compose.material3.SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (!enabled) it.alpha(0.5f) else it
            }
            .then(
                if (enabled) Modifier else Modifier.pointerInput(Unit) {
                    detectTapGestures(onTap = {})
                }
            ),
        query = text,
        onQueryChange = {
            if (enabled) text = it
        },
        onSearch = {
            if (enabled) {
                if (text.isNotEmpty() && !items.contains(text)) {
                    items.add(text)
                }
                active = false
                onSearch(text) // Pass the search query to the parent composable
            }
        },
        active = active,
        onActiveChange = {
            if (enabled) active = it
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(16.dp)
                    .size(24.dp)
            )
        },
        placeholder = {
            Text(text = "Search")
        },
        trailingIcon = {
            if (active) {
                Icon(
                    modifier = Modifier.clickable {
                        if (enabled) {
                            if (text.isNotEmpty()) {
                                text = ""
                            } else {
                                active = false
                            }
                        }
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close icon"
                )
            }
        }
    ) {
        items.forEach {
            Row(modifier = Modifier.padding(all = 14.dp)) {
                Box(modifier = Modifier
                    .clickable {
                        if (enabled) {
                            text = it
                            active = false
                            onSearch(text)
                        }
                    }
                    .fillMaxWidth()) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .align(Alignment.CenterStart),
                        imageVector = Icons.Default.History,
                        contentDescription = "History icon"
                    )
                    Text(
                        text = it,
                        modifier = Modifier
                            .padding(start = 35.dp)
                            .align(Alignment.CenterStart),
                    )
                }
            }
        }
    }
}