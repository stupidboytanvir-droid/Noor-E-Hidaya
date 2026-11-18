package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.location.NearbyMosque
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkEmeraldElevated
import com.example.ui.theme.DarkEmeraldSurface
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.LocalAppLanguage
import com.example.ui.theme.MintGlow
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SlateGrayMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.formatLocalizedInt
import com.example.ui.theme.localizedText
import com.example.ui.theme.uiText
import com.example.ui.viewmodel.NearbyMosqueViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * 100% Free Programmatic High-Contrast Dark Mode Filter for OpenStreetMap (MAPNIK).
 * Inverts and color-balances standard OSM tiles to seamlessly match Obsidian Midnight (#030707)
 * without requiring external dark tile endpoints, billing, or API keys.
 */
val DarkMapColorFilter = ColorMatrixColorFilter(
    ColorMatrix(
        floatArrayOf(
            -0.78f,  0.00f,  0.00f, 0.00f, 205f,  // Invert & dim Red
             0.00f, -0.78f,  0.00f, 0.00f, 210f,  // Invert & dim Green
             0.00f,  0.00f, -0.78f, 0.00f, 215f,  // Invert & dim Blue
             0.00f,  0.00f,  0.00f, 1.00f,   0f   // Preserve Alpha
        )
    )
)

/**
 * Free Satellite Tile Source: Esri World Imagery
 */
val EsriSatelliteTileSource = object : OnlineTileSourceBase(
    "Esri-Satellite",
    0, 19, 256, ".jpg",
    arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/"),
    "© Esri, Maxar, Earthstar Geographics"
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        return baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "/" +
                MapTileIndex.getY(pMapTileIndex) + "/" +
                MapTileIndex.getX(pMapTileIndex)
    }
}

/**
 * Generates a custom glowing mint green GPS user marker for osmdroid.
 */
private fun createPulsingUserMarker(context: Context): Drawable {
    val density = context.resources.displayMetrics.density
    val size = (32 * density).toInt().coerceAtLeast(24)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val center = size / 2f
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Outer subtle translucent pulse
    paint.style = Paint.Style.FILL
    paint.color = android.graphics.Color.parseColor("#3300E676")
    canvas.drawCircle(center, center, center * 0.95f, paint)

    // Middle halo
    paint.color = android.graphics.Color.parseColor("#6600E676")
    canvas.drawCircle(center, center, center * 0.7f, paint)

    // Solid core dot
    paint.color = android.graphics.Color.parseColor("#00E676")
    canvas.drawCircle(center, center, center * 0.45f, paint)

    // Crisp white border
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2f * density
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(center, center, center * 0.45f, paint)

    return BitmapDrawable(context.resources, bitmap)
}

/**
 * Generates a custom Brushed Champagne Gold mosque dome pin for osmdroid.
 */
private fun createGoldMosqueMarker(context: Context, isSelected: Boolean): Drawable {
    val density = context.resources.displayMetrics.density
    val width = (if (isSelected) 42 * density else 34 * density).toInt().coerceAtLeast(30)
    val height = (if (isSelected) 50 * density else 40 * density).toInt().coerceAtLeast(36)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val centerX = width / 2f
    val circleRadius = (width / 2f) - (3f * density)
    val centerY = circleRadius + (2f * density)

    // If selected, draw glowing halo ring
    if (isSelected) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3.5f * density
        paint.color = android.graphics.Color.parseColor("#55E5C158")
        canvas.drawCircle(centerX, centerY, circleRadius + (2f * density), paint)
    }

    // Teardrop pin tip
    val path = Path().apply {
        moveTo(centerX, height.toFloat() - (2f * density))
        lineTo(centerX - (7f * density), centerY + (circleRadius * 0.5f))
        lineTo(centerX + (7f * density), centerY + (circleRadius * 0.5f))
        close()
    }
    paint.style = Paint.Style.FILL
    paint.color = android.graphics.Color.parseColor(if (isSelected) "#E5C158" else "#0A1312")
    canvas.drawPath(path, paint)

    // Pin circle background
    paint.color = android.graphics.Color.parseColor(if (isSelected) "#112625" else "#0A1312")
    canvas.drawCircle(centerX, centerY, circleRadius, paint)

    // Golden border
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = if (isSelected) 2.5f * density else 1.8f * density
    paint.color = android.graphics.Color.parseColor("#E5C158")
    canvas.drawCircle(centerX, centerY, circleRadius, paint)

    // Golden Mosque Dome inside
    val domePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#E5C158")
        style = Paint.Style.FILL
    }
    val domeRect = RectF(
        centerX - (circleRadius * 0.52f),
        centerY - (circleRadius * 0.42f),
        centerX + (circleRadius * 0.52f),
        centerY + (circleRadius * 0.42f)
    )
    canvas.drawArc(domeRect, 180f, 180f, true, domePaint)

    // Minaret finial on dome top
    paint.style = Paint.Style.FILL
    paint.color = android.graphics.Color.parseColor("#E5C158")
    canvas.drawCircle(centerX, centerY - (circleRadius * 0.46f), 1.6f * density, paint)

    return BitmapDrawable(context.resources, bitmap)
}

/**
 * 100% Free API-Key Free Nearby Mosque Module
 * Rendered using osmdroid and public Overpass API.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyMosqueScreen(
    onBack: () -> Unit,
    viewModel: NearbyMosqueViewModel = viewModel()
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val uiLang = LocalAppLanguage.current
    val uiState by viewModel.uiState.collectAsState()

    // Location Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            viewModel.onPermissionGranted()
        }
    }

    var osmdroidMapRef by remember { mutableStateOf<MapView?>(null) }
    var isMapExpanded by remember { mutableStateOf(false) }

    // Center map on selected mosque
    LaunchedEffect(uiState.selectedMosque) {
        val selected = uiState.selectedMosque
        if (selected != null && osmdroidMapRef != null) {
            osmdroidMapRef?.controller?.animateTo(
                GeoPoint(selected.latitude, selected.longitude),
                16.5,
                700L
            )
        }
    }

    Scaffold(
        containerColor = ObsidianBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBackground
                ),
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiText("কাছাকাছি মসজিদ", "Nearby Mosques"),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MintGlow.copy(alpha = 0.15f))
                                    .border(0.5.dp, MintGlow, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (uiState.userLocation.isLiveLocation) {
                                        uiText("লাইভ জিপিএস", "Live GPS")
                                    } else {
                                        uiText("ডিফল্ট এলাকা", "Default Area")
                                    },
                                    color = MintGlow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = uiState.userLocation.cityName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("nearby_mosque_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = uiText("ফিরে যান", "Back"),
                            tint = ChampagneGold
                        )
                    }
                },
                actions = {
                    // Map Layer Toggle (Dark CartoDB vs Satellite)
                    IconButton(
                        onClick = { viewModel.toggleMapTileStyle() },
                        modifier = Modifier.testTag("map_layer_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = uiText("ম্যাপ স্টাইল", "Map Style"),
                            tint = when (uiState.mapTileStyle) {
                                "satellite" -> MintGlow
                                "osm" -> ChampagneGold
                                else -> SlateGrayMuted
                            }
                        )
                    }

                    // Radar Refresh Button
                    val infiniteTransition = rememberInfiniteTransition(label = "spin")
                    val angle by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "spin_angle"
                    )

                    IconButton(
                        onClick = { viewModel.refreshMosques() },
                        modifier = Modifier.testTag("radar_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = uiText("রাডার রিফ্রেশ", "Refresh Radar"),
                            tint = ChampagneGold,
                            modifier = if (uiState.isLoading) Modifier.rotate(angle) else Modifier
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Permission Banner (if not granted)
            if (!uiState.hasPermission) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkEmeraldElevated)
                        .border(1.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = uiText("লাইভ জিপিএস লোকেশন চালু করুন", "Enable Live GPS Location"),
                                    color = ChampagneGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = uiText(
                                        "সঠিক দূরত্বের মসজিদ ও ওয়াক-টাইম দেখতে অনুমতি দিন",
                                        "Allow location access for accurate mosque distances & walking time"
                                    ),
                                    color = TextPrimary.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ChampagneGold,
                                contentColor = ObsidianBackground
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = uiText("অনুমতি দিন", "Allow"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Interactive Native OSMDroid Map View
            val mapHeight = if (isMapExpanded) 380.dp else 220.dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(mapHeight)
                    .background(ObsidianBackground)
            ) {
                OsmDroidMapView(
                    userLat = uiState.userLocation.latitude,
                    userLng = uiState.userLocation.longitude,
                    tileStyle = uiState.mapTileStyle,
                    mosques = uiState.allMosques,
                    selectedMosque = uiState.selectedMosque,
                    uiLang = uiLang,
                    onSelectMosque = { mosque -> viewModel.selectMosque(mosque) },
                    onMapViewCreated = { map -> osmdroidMapRef = map }
                )

                // Top Floating Gradient overlay for smooth blending
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(ObsidianBackground, Color.Transparent)
                            )
                        )
                        .align(Alignment.TopCenter)
                )

                // Bottom Floating Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, ObsidianBackground)
                            )
                        )
                        .align(Alignment.BottomCenter)
                )

                // Floating Map Controls (Zoom & Recenter)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Zoom In Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkEmeraldSurface)
                            .border(1.dp, HairlineBorder, CircleShape)
                            .clickable { osmdroidMapRef?.controller?.zoomIn() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = uiText("জুম ইন", "Zoom In"), tint = ChampagneGold, modifier = Modifier.size(18.dp))
                    }

                    // Zoom Out Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkEmeraldSurface)
                            .border(1.dp, HairlineBorder, CircleShape)
                            .clickable { osmdroidMapRef?.controller?.zoomOut() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = uiText("জুম আউট", "Zoom Out"), tint = ChampagneGold, modifier = Modifier.size(18.dp))
                    }

                    // Recenter on User Location FAB
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(DarkEmeraldSurface)
                            .border(1.dp, ChampagneGold, CircleShape)
                            .clickable {
                                osmdroidMapRef?.controller?.animateTo(
                                    GeoPoint(uiState.userLocation.latitude, uiState.userLocation.longitude),
                                    16.0,
                                    800L
                                )
                            }
                            .testTag("recenter_user_fab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = uiText("আমার লোকেশন", "My Location"),
                            tint = ChampagneGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Map Expand/Collapse Toggle
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(DarkEmeraldSurface)
                            .border(1.dp, HairlineBorder, CircleShape)
                            .clickable { isMapExpanded = !isMapExpanded }
                            .testTag("expand_map_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = uiText("ম্যাপ বড়/ছোট", "Expand/Collapse Map"),
                            tint = MintGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Loading Radar Indicator
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkEmeraldElevated.copy(alpha = 0.9f))
                            .border(0.5.dp, MintGlow, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = MintGlow,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = uiText("ওভারপাস রাডার স্ক্যান হচ্ছে...", "Scanning Overpass Radar..."),
                                color = MintGlow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Draggable / Expandable List of Mosques (Bottom Sheet Section)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(DarkEmeraldSurface)
                    .border(
                        width = 0.5.dp,
                        color = HairlineBorder,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Drag handle bar
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(ChampagneGold.copy(alpha = 0.4f))
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Header & Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = uiText("নিকটবর্তী মসজিদসমূহ", "Nearby Mosques"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkEmeraldElevated)
                                .border(0.5.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (com.example.data.util.SupportedLanguage.normalizeCode(uiLang) == "bn") {
                                    "${NearbyMosque.toBengaliNumerals(uiState.filteredMosques.size.toString())}টি"
                                } else {
                                    com.example.data.util.SupportedLanguage.formatInt(uiState.filteredMosques.size, uiLang)
                                },
                                color = ChampagneGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // OpenStreetMap attribution tag
                    Text(
                        text = "OSMDroid • Overpass",
                        color = SlateGrayMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Radius Filter Selector
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val radiusOptions = listOf(
                        Pair(1000, localizedText(uiLang, "১ কিমি", "1 km")),
                        Pair(3000, localizedText(uiLang, "৩ কিমি", "3 km")),
                        Pair(5000, localizedText(uiLang, "৫ কিমি", "5 km")),
                        Pair(10000, localizedText(uiLang, "১০ কিমি", "10 km"))
                    )
                    items(radiusOptions) { (radius, label) ->
                        val isSelected = uiState.selectedRadiusMeters == radius
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ChampagneGold else DarkEmeraldElevated)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ChampagneGold else HairlineBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.setRadius(radius) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) ObsidianBackground else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("mosque_search_input"),
                    placeholder = {
                        Text(
                            text = uiText("মসজিদ বা এলাকার নাম দিয়ে খুঁজুন...", "Search by mosque or area name..."),
                            color = SlateGrayMuted,
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = uiText("মুছুন", "Clear"),
                                    tint = SlateGrayMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkEmeraldElevated,
                        unfocusedContainerColor = DarkEmeraldElevated,
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = HairlineBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Selected Mosque Highlight Card (if selected)
                AnimatedVisibility(visible = uiState.selectedMosque != null) {
                    uiState.selectedMosque?.let { selected ->
                        SelectedMosqueDetailCard(
                            mosque = selected,
                            onClose = { viewModel.selectMosque(null) },
                            onNavigate = { viewModel.openGoogleMapsNavigation(context, selected, uiLang) },
                            onShare = { viewModel.shareMosqueLocation(context, selected, uiLang) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Scrollable List of Mosques
                if (uiState.filteredMosques.isEmpty() && !uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Mosque,
                                contentDescription = null,
                                tint = SlateGrayMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiText("এই ব্যাসার্ধে কোনো মসজিদ পাওয়া যায়নি", "No mosques found within this radius"),
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiText(
                                    "অনুসন্ধান পরিধি বৃদ্ধি করুন অথবা অন্য এলাকা খুঁজুন",
                                    "Try expanding the search radius or searching another area"
                                ),
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.filteredMosques, key = { it.id }) { mosque ->
                            val isSelected = uiState.selectedMosque?.id == mosque.id
                            MosqueListItemCard(
                                mosque = mosque,
                                isSelected = isSelected,
                                onClick = {
                                    viewModel.selectMosque(mosque)
                                    osmdroidMapRef?.controller?.animateTo(
                                        GeoPoint(mosque.latitude, mosque.longitude),
                                        16.5,
                                        700L
                                    )
                                },
                                onNavigate = { viewModel.openGoogleMapsNavigation(context, mosque, uiLang) },
                                onShare = { viewModel.shareMosqueLocation(context, mosque, uiLang) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Native OSMDroid AndroidView for Jetpack Compose.
 * Displays CartoDB Dark Matter tiles, user location beacon, and gold dome markers.
 */
@Composable
fun OsmDroidMapView(
    userLat: Double,
    userLng: Double,
    tileStyle: String,
    mosques: List<NearbyMosque>,
    selectedMosque: NearbyMosque?,
    uiLang: String = "bn",
    onSelectMosque: (NearbyMosque) -> Unit,
    modifier: Modifier = Modifier,
    onMapViewCreated: (MapView) -> Unit
) {
    val context = LocalContext.current
    var mapViewInstance by remember { mutableStateOf<MapView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            try {
                mapViewInstance?.onDetach()
            } catch (_: Exception) {}
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            Configuration.getInstance().userAgentValue = ctx.packageName
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

            MapView(ctx).apply {
                // Strictly 100% Free OpenStreetMap MAPNIK or Esri Satellite
                val isSatellite = (tileStyle == "satellite")
                setTileSource(if (isSatellite) EsriSatelliteTileSource else TileSourceFactory.MAPNIK)

                // Apply Dark Mode ColorMatrix filter over MAPNIK to match Obsidian Midnight
                if (tileStyle == "dark") {
                    overlayManager.tilesOverlay.setColorFilter(DarkMapColorFilter)
                } else {
                    overlayManager.tilesOverlay.setColorFilter(null)
                }

                setMultiTouchControls(true)
                isTilesScaledToDpi = true
                minZoomLevel = 4.0
                maxZoomLevel = 20.0
                controller.setZoom(15.5)
                controller.setCenter(GeoPoint(userLat, userLng))
                zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)

                // Wipe any old cached watermarked tiles from device storage & memory
                tileProvider.clearTileCache()
                invalidate()

                mapViewInstance = this
                onMapViewCreated(this)
            }
        },
        update = { mapView ->
            val isSatellite = (tileStyle == "satellite")
            val desiredTileSource = if (isSatellite) EsriSatelliteTileSource else TileSourceFactory.MAPNIK
            if (mapView.tileProvider.tileSource.name() != desiredTileSource.name()) {
                mapView.setTileSource(desiredTileSource)
                mapView.tileProvider.clearTileCache()
            }

            if (tileStyle == "dark") {
                mapView.overlayManager.tilesOverlay.setColorFilter(DarkMapColorFilter)
            } else {
                mapView.overlayManager.tilesOverlay.setColorFilter(null)
            }

            // Rebuild overlays
            mapView.overlays.clear()

            // 1. User Live GPS Marker
            val userMarker = Marker(mapView).apply {
                position = GeoPoint(userLat, userLng)
                title = localizedText(uiLang, "আমার বর্তমান অবস্থান", "My Current Location")
                snippet = localizedText(uiLang, "জিপিএস লাইভ কোঅর্ডিনেট", "Live GPS Coordinates")
                icon = createPulsingUserMarker(context)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            }
            mapView.overlays.add(userMarker)

            // 2. Discovered Mosque Gold Markers
            mosques.forEach { mosque ->
                val isSelected = selectedMosque?.id == mosque.id
                val mosqueMarker = Marker(mapView).apply {
                    id = mosque.id
                    position = GeoPoint(mosque.latitude, mosque.longitude)
                    title = mosque.localizedName(uiLang)
                    snippet = "${mosque.localizedDistance(uiLang)} • ${mosque.localizedWalkingTime(uiLang)}"
                    icon = createGoldMosqueMarker(context, isSelected)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    setOnMarkerClickListener { _, _ ->
                        onSelectMosque(mosque)
                        true
                    }
                }
                mapView.overlays.add(mosqueMarker)
            }

            mapView.invalidate()
        }
    )
}

/**
 * Highlight card for the currently selected mosque with quick directions & amenities.
 */
@Composable
fun SelectedMosqueDetailCard(
    mosque: NearbyMosque,
    onClose: () -> Unit,
    onNavigate: () -> Unit,
    onShare: () -> Unit
) {
    val uiLang = LocalAppLanguage.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkEmeraldElevated)
            .border(1.5.dp, ChampagneGold, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mosque.localizedName(uiLang),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                    val secondaryTitle = if (com.example.data.util.SupportedLanguage.normalizeCode(uiLang) == "bn") mosque.name else ""
                    if (secondaryTitle.isNotBlank() && secondaryTitle != mosque.localizedName(uiLang)) {
                        Text(
                            text = secondaryTitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = uiText("বন্ধ করুন", "Close"),
                        tint = SlateGrayMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Metrics row: Distance and walking time
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MintGlow.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = mosque.localizedDistance(uiLang),
                        color = MintGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ChampagneGold.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = mosque.localizedWalkingTime(uiLang),
                        color = ChampagneGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = mosque.localizedAddress(uiLang),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary.copy(alpha = 0.85f),
                    fontSize = 11.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Amenities
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AmenityBadge(label = uiText("ওযু সুবিধা", "Wudu Area"), isAvailable = mosque.hasWuduFacility)
                AmenityBadge(label = uiText("মহিলাদের জায়গা", "Women's Area"), isAvailable = mosque.hasWomenPrayerArea)
                AmenityBadge(label = uiText("শীতাতপ নিয়ন্ত্রিত", "Air Conditioned"), isAvailable = mosque.hasAirConditioning)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigate,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("selected_mosque_navigate_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiText("নেভিগেশন শুরু করুন", "Start Navigation"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onShare,
                    modifier = Modifier.width(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkEmeraldSurface,
                        contentColor = ChampagneGold
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = uiText("শেয়ার করুন", "Share"),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Standard Mosque List Item Card
 */
@Composable
fun MosqueListItemCard(
    mosque: NearbyMosque,
    isSelected: Boolean,
    onClick: () -> Unit,
    onNavigate: () -> Unit,
    onShare: () -> Unit
) {
    val uiLang = LocalAppLanguage.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) DarkEmeraldElevated else DarkEmeraldSurface)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) ChampagneGold else HairlineBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("mosque_card_${mosque.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mosque Dome Icon Box
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) ChampagneGold.copy(alpha = 0.2f) else DarkEmeraldElevated)
                        .border(1.dp, if (isSelected) ChampagneGold else HairlineBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Title & Subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mosque.localizedName(uiLang),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = if (isSelected) ChampagneGold else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val secondaryName = if (com.example.data.util.SupportedLanguage.normalizeCode(uiLang) == "bn") mosque.name else ""
                    if (secondaryName.isNotBlank() && secondaryName != mosque.localizedName(uiLang)) {
                        Text(
                            text = secondaryName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Distance Badge
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = mosque.localizedDistance(uiLang),
                        color = MintGlow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                            contentDescription = null,
                            tint = SlateGrayMuted,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = mosque.localizedWalkingTime(uiLang).replace("~ ", ""),
                            color = SlateGrayMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Address & quick actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mosque.localizedAddress(uiLang),
                    color = SlateGrayMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Turn-by-Turn Navigation Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ChampagneGold.copy(alpha = 0.15f))
                            .border(0.5.dp, ChampagneGold, RoundedCornerShape(8.dp))
                            .clickable { onNavigate() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("navigate_google_maps_btn_${mosque.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = uiText("পথনির্দেশ", "Navigate"),
                                tint = ChampagneGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = uiText("পথনির্দেশ", "Directions"),
                                color = ChampagneGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Share Location Button
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(DarkEmeraldElevated)
                            .border(0.5.dp, HairlineBorder, CircleShape)
                            .clickable { onShare() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = uiText("শেয়ার করুন", "Share"),
                            tint = ChampagneGold,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AmenityBadge(label: String, isAvailable: Boolean) {
    if (!isAvailable) return
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DarkEmeraldElevated)
            .border(0.5.dp, HairlineBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "✓ $label",
            color = ChampagneGold.copy(alpha = 0.9f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
