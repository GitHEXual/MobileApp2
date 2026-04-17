package com.example.myapplication

import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.TextButton
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
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.MapType
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.SuggestItem
import com.yandex.mapkit.search.SuggestOptions
import com.yandex.mapkit.search.SuggestResponse
import com.yandex.mapkit.search.SuggestSession
import com.yandex.mapkit.search.SuggestType
import kotlinx.coroutines.CoroutineScope
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
    onSelectHomeCatalog: (String) -> Unit,
    onSelectHomeCustom: (Double, Double, String, String) -> Unit
) {
    var selectedFavorite by remember { mutableStateOf<FavoriteCity?>(null) }
    var mapPickCity by remember { mutableStateOf<CityCatalogItem?>(null) }
    var searchMarker by remember { mutableStateOf<SearchPin?>(null) }
    var searchDialogVisible by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }

    val favoritesById = remember(favorites) { favorites.associateBy(FavoriteCity::cityId) }

    val searchManager = remember {
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    }
    val suggestSession = remember(searchManager) { searchManager.createSuggestSession() }
    var searchSession by remember { mutableStateOf<Session?>(null) }
    var resolveSession by remember { mutableStateOf<Session?>(null) }

    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        ScreenTopBar(title = labels.map, onBack = onBack)
        MapSearchBar(
            labels = labels,
            onSearch = { query ->
                searchError = null
                suggestSession.reset()
                resolveSession?.cancel()
                searchSession?.cancel()
                val bbox = BoundingBox(Point(-85.0, -180.0), Point(85.0, 180.0))
                val suggestOpts = SuggestOptions()
                    .setSuggestTypes(SuggestType.GEO.value)
                    .setStrictBounds(false)
                suggestSession.suggest(
                    query,
                    bbox,
                    suggestOpts,
                    object : SuggestSession.SuggestListener {
                        override fun onResponse(response: SuggestResponse) {
                            scope.launch(Dispatchers.Main) {
                                val direct = pinFromSuggest(response, query)
                                if (direct != null) {
                                    searchMarker = direct
                                    searchDialogVisible = true
                                    return@launch
                                }
                                val top = response.items.firstOrNull { it.type == SuggestItem.Type.TOPONYM }
                                    ?: response.items.firstOrNull()
                                val uri = top?.uri
                                if (!uri.isNullOrBlank()) {
                                    val titleHint = top.displayText?.takeIf { it.isNotBlank() }
                                        ?: top.title?.text?.takeIf { it.isNotBlank() }
                                        ?: top.searchText?.takeIf { it.isNotBlank() }
                                        ?: query
                                    resolveSession = searchManager.resolveURI(
                                        uri,
                                        SearchOptions().setSearchTypes(SearchType.GEO.value),
                                        object : Session.SearchListener {
                                            override fun onSearchResponse(response: Response) {
                                                scope.launch(Dispatchers.Main) {
                                                    val geo = firstGeoObject(response.collection)
                                                    val pt = geo?.let { firstPointFromGeo(it) }
                                                    if (geo != null && pt != null) {
                                                        val t = geo.name?.takeIf { it.isNotBlank() } ?: titleHint
                                                        searchMarker = SearchPin(pt, t)
                                                        searchDialogVisible = true
                                                    } else {
                                                        trySubmitSearch(
                                                            scope = scope,
                                                            searchManager = searchManager,
                                                            query = query,
                                                            onSession = { searchSession = it },
                                                            onPin = { p ->
                                                                searchMarker = p
                                                                searchDialogVisible = true
                                                            },
                                                            onEmpty = { searchError = labels.mapSearchNoResults }
                                                        )
                                                    }
                                                }
                                            }

                                            override fun onSearchError(error: com.yandex.runtime.Error) {
                                                scope.launch(Dispatchers.Main) {
                                                    trySubmitSearch(
                                                        scope = scope,
                                                        searchManager = searchManager,
                                                        query = query,
                                                        onSession = { searchSession = it },
                                                        onPin = { p ->
                                                            searchMarker = p
                                                            searchDialogVisible = true
                                                        },
                                                        onEmpty = { searchError = labels.mapSearchNoResults }
                                                    )
                                                }
                                            }
                                        }
                                    )
                                    return@launch
                                }
                                trySubmitSearch(
                                    scope = scope,
                                    searchManager = searchManager,
                                    query = query,
                                    onSession = { searchSession = it },
                                    onPin = { p ->
                                        searchMarker = p
                                        searchDialogVisible = true
                                    },
                                    onEmpty = { searchError = labels.mapSearchNoResults }
                                )
                            }
                        }

                        override fun onError(error: com.yandex.runtime.Error) {
                            scope.launch(Dispatchers.Main) {
                                trySubmitSearch(
                                    scope = scope,
                                    searchManager = searchManager,
                                    query = query,
                                    onSession = { searchSession = it },
                                    onPin = { p ->
                                        searchMarker = p
                                        searchDialogVisible = true
                                    },
                                    onEmpty = { searchError = labels.mapSearchNoResults }
                                )
                            }
                        }
                    }
                )
            }
        )
        searchError?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        YandexMapContent(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            cities = cities,
            homeCityId = homeCityId,
            searchPin = searchMarker,
            onCityMarkerTap = { cityId ->
                val city = cities.firstOrNull { it.id == cityId } ?: return@YandexMapContent
                val fav = favoritesById[cityId]
                if (fav != null) {
                    selectedFavorite = fav
                } else {
                    mapPickCity = city
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

    mapPickCity?.let { city ->
        AlertDialog(
            onDismissRequest = { mapPickCity = null },
            title = { Text(localizedCity(city.weather, language)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = localizedCondition(city.weather, language),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = signedTemperature(city.weather.temperature, suffix = "°C"),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Button(
                        onClick = {
                            onSelectHomeCatalog(city.id)
                            mapPickCity = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = bluePrimary)
                    ) {
                        Text(labels.setAsHomeCity)
                    }
                    OutlinedButton(
                        onClick = {
                            onOpenDetail(city.id)
                            mapPickCity = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(labels.details)
                    }
                    TextButton(
                        onClick = { mapPickCity = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(labels.cancel)
                    }
                }
            },
            confirmButton = {}
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

}

private fun pinFromSuggest(response: SuggestResponse, query: String): SearchPin? {
    val items = response.items
    if (items.isEmpty()) return null
    val top = items.firstOrNull { it.type == SuggestItem.Type.TOPONYM }
        ?: items.firstOrNull()
        ?: return null
    top.center?.let { pt ->
        val title = top.title?.text?.takeIf { it.isNotBlank() } ?: query
        return SearchPin(pt, title)
    }
    return null
}

private fun trySubmitSearch(
    scope: CoroutineScope,
    searchManager: SearchManager,
    query: String,
    onSession: (Session) -> Unit,
    onPin: (SearchPin) -> Unit,
    onEmpty: () -> Unit
) {
    val worldGeometry = Geometry.fromBoundingBox(
        BoundingBox(
            Point(-85.0, -180.0),
            Point(85.0, 180.0)
        )
    )
    val types = SearchType.GEO.value or SearchType.BIZ.value
    val opts = SearchOptions()
        .setSearchTypes(types)
        .setResultPageSize(10)
    val session = searchManager.submit(
        query,
        worldGeometry,
        opts,
        object : Session.SearchListener {
            override fun onSearchResponse(response: Response) {
                val geo = firstGeoObject(response.collection)
                val pt = geo?.let { firstPointFromGeo(it) }
                scope.launch(Dispatchers.Main) {
                    if (geo != null && pt != null) {
                        val title = geo.name?.takeIf { it.isNotBlank() } ?: query
                        onPin(SearchPin(pt, title))
                    } else {
                        onEmpty()
                    }
                }
            }

            override fun onSearchError(error: com.yandex.runtime.Error) {
                scope.launch(Dispatchers.Main) { onEmpty() }
            }
        }
    )
    onSession(session)
}

@Composable
private fun MapSearchBar(
    labels: AppStrings,
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
        IconButton(
            onClick = {
                val q = query.trim()
                if (q.isBlank()) return@IconButton
                onSearch(q)
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
    searchPin: SearchPin?,
    onCityMarkerTap: (String) -> Unit,
    onSearchMarkerTap: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember {
        MapView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    fun startMapKit() {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    fun stopMapKit() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> startMapKit()
                Lifecycle.Event.ON_STOP -> stopMapKit()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            startMapKit()
        }
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(obs)
            stopMapKit()
        }
    }

    LaunchedEffect(Unit) {
        val map = mapView.mapWindow.map
        map.mapType = MapType.MAP
        // Векторная тёмная тема на части устройств даёт «чёрный экран» — оставляем дневной стиль карты.
        map.isNightModeEnabled = false
    }

    LaunchedEffect(cities, homeCityId, searchPin) {
        val map = mapView.mapWindow.map
        map.mapObjects.clear()
        cities.forEach { city ->
            val pt = Point(city.latitude, city.longitude)
            val pm = map.mapObjects.addPlacemark(pt)
            pm.userData = city.id
            pm.zIndex = if (city.id == homeCityId) 2f else 1f
            pm.addTapListener(MapObjectTapListener { obj, _ ->
                val id = obj.userData as? String ?: return@MapObjectTapListener false
                onCityMarkerTap(id)
                true
            })
        }
        searchPin?.let { pin ->
            val pm = map.mapObjects.addPlacemark(pin.point)
            pm.userData = "search"
            pm.zIndex = 4f
            pm.addTapListener(MapObjectTapListener { _, _ ->
                onSearchMarkerTap()
                true
            })
        }
    }

    val focusHome = cities.firstOrNull { it.id == homeCityId }
    LaunchedEffect(searchPin, focusHome?.latitude, focusHome?.longitude, homeCityId) {
        val map = mapView.mapWindow.map
        val focus = searchPin?.point
            ?: focusHome?.let { Point(it.latitude, it.longitude) }
            ?: Point(55.75, 37.62)
        val zoom = when {
            searchPin != null -> 11f
            focusHome != null -> 6f
            else -> 4.5f
        }
        map.move(CameraPosition(focus, zoom, 0f, 0f))
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
