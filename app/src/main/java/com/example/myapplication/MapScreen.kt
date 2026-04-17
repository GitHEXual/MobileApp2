package com.example.myapplication

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private sealed class MapSheetState {
    data class CatalogPick(val city: CityCatalogItem) : MapSheetState()
    data class SearchPick(val place: GeocodingPlace) : MapSheetState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier,
    cities: List<CityCatalogItem>,
    homeCityId: String,
    labels: AppStrings,
    language: AppLanguage,
    searchMapPlaces: suspend (String) -> List<GeocodingPlace>,
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit,
    onSelectHomeCatalog: (String) -> Unit,
    onSelectHomeCustom: (Double, Double, String, String) -> Unit
) {
    var sheetState by remember { mutableStateOf<MapSheetState?>(null) }
    var searchPin by remember { mutableStateOf<GeocodingPlace?>(null) }
    var searchCandidates by remember { mutableStateOf<List<GeocodingPlace>>(emptyList()) }
    var searchError by remember { mutableStateOf<String?>(null) }
    var searchLoading by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        ScreenTopBar(title = labels.map, onBack = onBack)
        MapSearchBar(
            labels = labels,
            searchLoading = searchLoading,
            onSearch = { query ->
                searchError = null
                searchCandidates = emptyList()
                searchLoading = true
                scope.launch {
                    val list = searchMapPlaces(query)
                    searchLoading = false
                    if (list.isEmpty()) {
                        searchError = labels.mapSearchNoResults
                    } else {
                        searchCandidates = list
                    }
                }
            }
        )
        if (searchCandidates.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp)
                ) {
                    itemsIndexed(
                        searchCandidates,
                        key = { _, p -> "${p.latitude}_${p.longitude}_${p.title}" }
                    ) { index, place ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    searchPin = place
                                    searchCandidates = emptyList()
                                    sheetState = MapSheetState.SearchPick(place)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = place.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (index < searchCandidates.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            )
                        }
                    }
                }
            }
        }
        searchError?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        OsmMapContent(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            cities = cities,
            homeCityId = homeCityId,
            searchPin = searchPin,
            language = language,
            onCatalogMarkerTap = { cityId ->
                val city = cities.firstOrNull { it.id == cityId } ?: return@OsmMapContent
                sheetState = MapSheetState.CatalogPick(city)
            },
            onSearchMarkerTap = {
                val pin = searchPin ?: return@OsmMapContent
                sheetState = MapSheetState.SearchPick(pin)
            }
        )
    }

    if (sheetState != null) {
        ModalBottomSheet(
            onDismissRequest = { sheetState = null },
            sheetState = bottomSheetState
        ) {
            when (val s = sheetState!!) {
                is MapSheetState.CatalogPick -> MapCatalogSheet(
                    city = s.city,
                    isHomeCity = s.city.id == homeCityId,
                    labels = labels,
                    language = language,
                    onSetHome = {
                        onSelectHomeCatalog(s.city.id)
                        sheetState = null
                    },
                    onDetails = {
                        onOpenDetail(s.city.id)
                        sheetState = null
                    },
                    onClose = { sheetState = null }
                )
                is MapSheetState.SearchPick -> MapSearchResultSheet(
                    place = s.place,
                    labels = labels,
                    onSetHome = {
                        onSelectHomeCustom(
                            s.place.latitude,
                            s.place.longitude,
                            s.place.title,
                            s.place.title
                        )
                        sheetState = null
                    },
                    onClose = { sheetState = null }
                )
            }
        }
    }
}

@Composable
private fun MapCatalogSheet(
    city: CityCatalogItem,
    isHomeCity: Boolean,
    labels: AppStrings,
    language: AppLanguage,
    onSetHome: () -> Unit,
    onDetails: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = localizedCity(city.weather, language),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = localizedCondition(city.weather, language),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = signedTemperature(city.weather.temperature, suffix = "°C"),
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (!isHomeCity) {
            Button(
                onClick = onSetHome,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = bluePrimary)
            ) {
                Text(labels.setAsHomeCity)
            }
        }
        OutlinedButton(onClick = onDetails, modifier = Modifier.fillMaxWidth()) {
            Text(labels.details)
        }
        OutlinedButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text(labels.close)
        }
    }
}

@Composable
private fun MapSearchResultSheet(
    place: GeocodingPlace,
    labels: AppStrings,
    onSetHome: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = place.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = labels.mapSearchResultHint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            onClick = onSetHome,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = bluePrimary)
        ) {
            Text(labels.setAsHomeCity)
        }
        OutlinedButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text(labels.close)
        }
    }
}

@Composable
private fun MapSearchBar(
    labels: AppStrings,
    searchLoading: Boolean,
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text(labels.mapSearchPlaceholder) },
            singleLine = true
        )
        if (searchLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        } else {
            IconButton(
                onClick = {
                    val q = query.trim()
                    if (q.isNotBlank()) onSearch(q)
                }
            ) {
                Icon(Icons.Default.Search, contentDescription = labels.mapSearchPlaceholder)
            }
        }
    }
}

private fun pinDrawable(context: Context, colorArgb: Int, scale: Float): Drawable? {
    val base = ContextCompat.getDrawable(context, org.osmdroid.library.R.drawable.marker_default)?.mutate()
        ?: return null
    base.setTint(colorArgb)
    val w = (base.intrinsicWidth * scale).toInt().coerceAtLeast(8)
    val h = (base.intrinsicHeight * scale).toInt().coerceAtLeast(8)
    base.setBounds(0, 0, w, h)
    return base
}

@Composable
private fun OsmMapContent(
    modifier: Modifier,
    cities: List<CityCatalogItem>,
    homeCityId: String,
    searchPin: GeocodingPlace?,
    language: AppLanguage,
    onCatalogMarkerTap: (String) -> Unit,
    onSearchMarkerTap: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val blueArgb = bluePrimary.toArgb()
    val mapView = remember {
        MapView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setMultiTouchControls(true)
            setTileSource(TileSourceFactory.MAPNIK)
            minZoomLevel = 3.0
            maxZoomLevel = 19.0
            isTilesScaledToDpi = true
            setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
            clipToOutline = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            mapView.onResume()
        }
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(obs)
            mapView.onPause()
        }
    }

    LaunchedEffect(cities, homeCityId, searchPin, language, blueArgb) {
        mapView.overlays.removeAll { it is Marker }
        val catalogScaleOther = 0.62f
        cities.forEach { city ->
            val isHome = city.id == homeCityId
            val icon = pinDrawable(
                context,
                if (isHome) android.graphics.Color.RED else blueArgb,
                if (isHome) 1f else catalogScaleOther
            ) ?: return@forEach
            val marker = Marker(mapView).apply {
                position = GeoPoint(city.latitude, city.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = localizedCity(city.weather, language)
                relatedObject = city.id
                this.icon = icon
                setOnMarkerClickListener { m, _ ->
                    val id = m.relatedObject as? String ?: return@setOnMarkerClickListener false
                    onCatalogMarkerTap(id)
                    true
                }
            }
            mapView.overlays.add(marker)
        }
        searchPin?.let { pin ->
            val icon = pinDrawable(context, blueArgb, catalogScaleOther) ?: return@let
            val marker = Marker(mapView).apply {
                position = GeoPoint(pin.latitude, pin.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = pin.title
                relatedObject = "search"
                this.icon = icon
                setOnMarkerClickListener { _, _ ->
                    onSearchMarkerTap()
                    true
                }
            }
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    val focusHome = cities.firstOrNull { it.id == homeCityId }
    LaunchedEffect(searchPin, focusHome?.latitude, focusHome?.longitude, homeCityId) {
        val gp = searchPin?.let { GeoPoint(it.latitude, it.longitude) }
            ?: focusHome?.let { GeoPoint(it.latitude, it.longitude) }
            ?: GeoPoint(55.75, 37.62)
        val zoom = when {
            searchPin != null -> 11.0
            focusHome != null -> 6.0
            else -> 4.5
        }
        mapView.controller.animateTo(gp, zoom, 1000L)
    }

    Box(modifier = modifier.clipToBounds()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
