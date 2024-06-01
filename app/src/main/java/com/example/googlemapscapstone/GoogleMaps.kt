package com.example.googlemapscapstone

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleMaps(
    state: MapState,
    searchedLocation: LatLng?,
) {
    var mapTypeToRemember by remember { mutableStateOf(MapType.TERRAIN) }
    val markerState = rememberMarkerState(position = LatLng(0.0, 0.0))
    var isMarkerVisible by remember { mutableStateOf(false) }

    val mapProperties =
        MapProperties(
            isMyLocationEnabled = state.lastKnownLocation != null,
            mapType = mapTypeToRemember
        )

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(searchedLocation) {
        searchedLocation?.let {
            markerState.position = it
            isMarkerVisible = true
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(it, 15f)
            val animationDuration = 2000
            cameraPositionState.animate(cameraUpdate, animationDuration)
        }
    }

    Box(modifier = Modifier.fillMaxSize())
    {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                if (isMarkerVisible) {
                    isMarkerVisible = false
                } else {
                    markerState.position = latLng
                    isMarkerVisible = true
                }
            }
        ) {
            if (isMarkerVisible) {
                Marker(
                    state = markerState,
                    title = "Marker",
                    snippet = "Lat: ${markerState.position.latitude}, Lng: ${markerState.position.longitude}"
                )
            }
        }
    }

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
    ) {
        FloatingActionButton(
            onClick =
            { isSheetOpen = true },
            Modifier
                .size(40.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
                .background(Color.Transparent),
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(
                painterResource(id = R.drawable.stack_icon),
                contentDescription = "Sheet button",
            )
        }
        if (isSheetOpen) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { isSheetOpen = false },
                dragHandle = null
            ) {
                SheetContent(onMapTypeChange = { mapType ->
                    mapTypeToRemember = mapType
                    isSheetOpen = false
                })
            }
        }
    }
}

@Composable
fun SheetContent(onMapTypeChange: (MapType) -> Unit) {
    Spacer(modifier = Modifier.size(40.dp))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row {
            MapTypeButton(MapType.NORMAL, onMapTypeChange, R.drawable.ic_launcher_foreground, "Normal")
            MapTypeButton(MapType.HYBRID, onMapTypeChange, R.drawable.ic_launcher_foreground, "Hybrid")
            MapTypeButton(MapType.SATELLITE, onMapTypeChange, R.drawable.ic_launcher_foreground, "Satellite")
            MapTypeButton(MapType.TERRAIN, onMapTypeChange, R.drawable.ic_launcher_foreground, "Terrain")
        }

        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
fun MapTypeButton(mapType: MapType, onMapTypeChange: (MapType) -> Unit, iconResId: Int, label: String) {
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
