package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.GeoObjectCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private data class SearchPin(
    val point: Point,
    val title: String
)

@Composable
fun MapScreen(
    modifier: Modifier,
    cities: List<CityCatalogItem>,
    favorites: List<FavoriteCity>,
    homeCityId: String,
    labels: AppStrings,
    language: AppLanguage,
    isDark: Boolean,
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit,
    onDeleteFavorite: (String) -> Unit,
    onAddToFavorites: (String, String?) -> Unit,
    onSelectHomeCatalog: (String) -> Unit,
    onSelectHomeCustom: (Double, Double, String, String) -> Unit
) {
    var selectedFavorite by remember { mutableStateOf<FavoriteCity?>(null) }
    var selectedAvailable by remember { mutableStateOf<CityCatalogItem?>(null) }
    var searchMarker by remember { mutableStateOf<SearchPin?>(null) }
    var searchDialogVisible by remember { mutableStateOf(false) }

    val favoritesById = remember(favorites) { favorites.associateBy(FavoriteCity::cityId) }

    Column(modifier = modifier.fillMaxSize()) {
        ScreenTopBar(title = labels.map, onBack = onBack)
        MapSearchBar(
            labels = labels,
            onSearchResult = { pin ->
                searchMarker = pin
                searchDialogVisible = true
            }
        )
        YandexMapContent(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            cities = cities,
            homeCityId = homeCityId,
            isDark = isDark,
            searchPin = searchMarker,
            onCatalogMarkerTap = { cityId ->
                val city = cities.firstOrNull { it.id == cityId } ?: return@YandexMapContent
                val fav = favoritesById[cityId]
                if (fav != null) {
                    selectedFavorite = fav
                } else {
                    selectedAvailable = city
                }
            },
            onSearchMarkerTap = { searchDialogVisible = true }
        )
    }

    if (searchDialogVisible && searchMarker != null) {
        val pin = searchMarker!!
        AlertDialog(
            onDismissRequest = { searchDialogVisible = false },
            title = { Text(pin.title) },
            text = {
                Text(
                    text = labels.mapSearchResultHint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { searchDialogVisible = false }) {
                        Text(labels.cancel)
                    }
                    Button(
                        onClick = {
                            onSelectHomeCustom(
                                pin.point.latitude,
                                pin.point.longitude,
                                pin.title,
                                pin.title
                            )
                            searchDialogVisible = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = bluePrimary)
                    ) {
                        Text(labels.setAsHomeCity)
                    }
                }
            }
        )
    }

    selectedFavorite?.let { favorite ->
        AlertDialog(
            onDismissRequest = { selectedFavorite = null },
            title = { Text(localizedCity(favorite.weather, language)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = signedTemperature(favorite.weather.temperature, suffix = "°C"),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Light
                        )
                        Text(
                            text = localizedCondition(favorite.weather, language),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!favorite.note.isNullOrBlank()) {
                        NotePreviewCard(
                            note = favorite.note,
                            label = labels.noteLabel,
                            isDark = isDark,
                            italic = true
                        )
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            onSelectHomeCatalog(favorite.cityId)
                            selectedFavorite = null
                        }
                    ) {
                        Text(labels.setAsHomeCity)
                    }
                    Button(
                        onClick = {
                            onOpenDetail(favorite.cityId)
                            selectedFavorite = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = bluePrimary)
                    ) {
                        Text(labels.openDetails)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        onDeleteFavorite(favorite.cityId)
                        selectedFavorite = null
                    }
                ) {
                    Text(labels.delete, color = destructive)
                }
            }
        )
    }

    selectedAvailable?.let { city ->
        NoteDialog(
            title = labels.addToFavorites,
            label = labels.noteLabel,
            placeholder = labels.notePlaceholder,
            initialValue = "",
            confirmText = labels.save,
            dismissText = labels.cancel,
            leadingContent = {
                AppCard(isDark = isDark, backgroundOverride = MaterialTheme.colorScheme.surfaceVariant) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(localizedCity(city.weather, language), fontWeight = FontWeight.Medium)
                            Text(
                                localizedCondition(city.weather, language),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            signedTemperature(city.weather.temperature, suffix = "°"),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            },
            footerContent = {
                OutlinedButton(
                    onClick = {
                        onSelectHomeCatalog(city.id)
                        selectedAvailable = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(labels.setAsHomeCity)
                }
            },
            onDismiss = { selectedAvailable = null },
            onConfirm = { value ->
                onAddToFavorites(city.id, value)
                selectedAvailable = null
            }
        )
    }
}

@Composable
private fun MapSearchBar(
    labels: AppStrings,
    onSearchResult: (SearchPin) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val searchManager = remember {
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    }
    var session: Session? by remember { mutableStateOf(null) }
    val worldGeometry = remember {
        Geometry.fromBoundingBox(
            BoundingBox(
                Point(-85.0, -180.0),
                Point(85.0, 180.0)
            )
        )
    }

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
        IconButton(
            onClick = {
                val q = query.trim()
                if (q.isBlank()) return@IconButton
                session?.cancel()
                val opts = SearchOptions()
                    .setSearchTypes(SearchType.GEO.value)
                    .setResultPageSize(5)
                session = searchManager.submit(
                    q,
                    worldGeometry,
                    opts,
                    object : Session.SearchListener {
                        override fun onSearchResponse(response: Response) {
                            scope.launch(Dispatchers.Main) {
                                val geo = firstGeoObject(response.collection) ?: return@launch
                                val pt = firstPointFromGeo(geo) ?: return@launch
                                val title = geo.name?.takeIf { it.isNotBlank() } ?: q
                                onSearchResult(SearchPin(pt, title))
                            }
                        }

                        override fun onSearchError(error: com.yandex.runtime.Error) {
                            /* ignore */
                        }
                    }
                )
            }
        ) {
            Icon(Icons.Default.Search, contentDescription = labels.mapSearchPlaceholder)
        }
    }
}

@Composable
private fun YandexMapContent(
    modifier: Modifier,
    cities: List<CityCatalogItem>,
    homeCityId: String,
    isDark: Boolean,
    searchPin: SearchPin?,
    onCatalogMarkerTap: (String) -> Unit,
    onSearchMarkerTap: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context) }

    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(obs)
            mapView.onStop()
        }
    }

    LaunchedEffect(isDark) {
        mapView.mapWindow.map.isNightModeEnabled = isDark
    }

    LaunchedEffect(cities, homeCityId, searchPin) {
        val map = mapView.mapWindow.map
        map.mapObjects.clear()
        cities.forEach { city ->
            val pt = Point(city.latitude, city.longitude)
            val pm = map.mapObjects.addPlacemark(pt)
            pm.userData = city.id
            pm.zIndex = if (city.id == homeCityId) 2f else 0f
            pm.addTapListener(MapObjectTapListener { obj, _ ->
                val id = obj.userData as? String ?: return@MapObjectTapListener false
                onCatalogMarkerTap(id)
                true
            })
        }
        searchPin?.let { pin ->
            val pm = map.mapObjects.addPlacemark(pin.point)
            pm.userData = "search"
            pm.zIndex = 3f
            pm.addTapListener(MapObjectTapListener { _, _ ->
                onSearchMarkerTap()
                true
            })
        }
        val focus = searchPin?.point
            ?: cities.firstOrNull { it.id == homeCityId }?.let { Point(it.latitude, it.longitude) }
            ?: Point(55.75, 37.62)
        val zoom = if (searchPin != null) 11f else 5f
        map.move(
            CameraPosition(focus, zoom, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 0.35f),
            null
        )
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { _ -> mapView },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun firstGeoObject(collection: GeoObjectCollection): GeoObject? {
    for (item in collection.children) {
        item.obj?.let { return it }
        item.collection?.let { sub ->
            firstGeoObject(sub)?.let { return it }
        }
    }
    return null
}

private fun firstPointFromGeo(geo: GeoObject): Point? {
    for (g in geo.geometry) {
        g.point?.let { return it }
        g.boundingBox?.let { bb ->
            val sw = bb.southWest
            val ne = bb.northEast
            return Point(
                (sw.latitude + ne.latitude) / 2.0,
                (sw.longitude + ne.longitude) / 2.0
            )
        }
        g.circle?.center?.let { return it }
    }
    return null
}
