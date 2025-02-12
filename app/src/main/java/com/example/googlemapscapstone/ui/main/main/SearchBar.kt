package com.example.googlemapscapstone.ui.main.main

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
import com.example.googlemapscapstone.api.fetchGeocode
import com.google.android.gms.maps.model.LatLng

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    context: Context,
    onSearch: (LatLng?, String?) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val items = remember {
        mutableStateListOf(
            "Amsterdam",
            "Voorschoten"
        )
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.material3.SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = text,
            onQueryChange = {
                text = it
            },
            onSearch = {

                if (text.isNotEmpty() && !items.contains(text)) {
                    items.add(text)
                }
                active = false
                fetchGeocode(context, text) { location, address ->
                    if (location != null) {
                        onSearch(location, address)

                    }
                }
            },
            active = active,
            onActiveChange = {
                active = it
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search Icon",
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
                            if (text.isNotEmpty()) {
                                text = ""
                            } else {
                                active = false
                            }

                        },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon"
                    )
                }
            }
        ) {
            items.forEach {
                Row(modifier = Modifier.padding(all = 14.dp)) {
                    Box(modifier = Modifier
                        .clickable {
                            text = it
                            active = false
                            fetchGeocode(context, text) { location, address ->
                                if (location != null) {
                                    onSearch(location, address)
                                }

                            }
                        }
                        .fillMaxWidth()) {
                        Icon(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .align(Alignment.CenterStart),
                            imageVector = Icons.Default.History,
                            contentDescription = "History Icon"
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
}