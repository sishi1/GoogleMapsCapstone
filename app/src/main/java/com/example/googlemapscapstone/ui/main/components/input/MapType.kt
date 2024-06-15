package com.example.googlemapscapstone.ui.main.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.googlemapscapstone.R
import com.google.maps.android.compose.MapType

@Composable
fun SheetContent(onMapTypeChange: (MapType) -> Unit) {
    Spacer(modifier = Modifier.size(40.dp))
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row {
            MapTypeButton(
                MapType.NORMAL,
                onMapTypeChange,
                R.drawable.ic_launcher_foreground,
                "Normal"
            )
            MapTypeButton(
                MapType.HYBRID,
                onMapTypeChange,
                R.drawable.ic_launcher_foreground,
                "Hybrid"
            )
            MapTypeButton(
                MapType.SATELLITE,
                onMapTypeChange,
                R.drawable.ic_launcher_foreground,
                "Satellite"
            )
            MapTypeButton(
                MapType.TERRAIN,
                onMapTypeChange,
                R.drawable.ic_launcher_foreground,
                "Terrain"
            )
        }

        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
fun MapTypeButton(
    mapType: MapType,
    onMapTypeChange: (MapType) -> Unit,
    iconResId: Int,
    label: String
) {
    ShowButton(
        mapType = mapType,
        onMapTypeChange = onMapTypeChange,
        icon = painterResource(id = iconResId),
        contentDescription = label,
        label = label
    )
}

@Composable
fun ShowButton(
    mapType: MapType,
    onMapTypeChange: (MapType) -> Unit,
    icon: Painter,
    contentDescription: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .background(Color.LightGray, shape = MaterialTheme.shapes.small)
            .padding(8.dp) // Inner padding to avoid content touching the edges
    ) {
        IconButton(onClick = { onMapTypeChange(mapType) }) {
            Icon(painter = icon, contentDescription = contentDescription)
        }
        Text(text = label)
    }
}