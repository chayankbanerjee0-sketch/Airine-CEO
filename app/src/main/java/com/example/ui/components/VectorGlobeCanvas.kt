package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.data.model.ActiveFlight
import com.example.data.model.Airport
import com.example.data.repository.AirportDatabase
import com.example.simulation.FlightMath
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun VectorGlobeCanvas(
    activeFlights: List<ActiveFlight>,
    selectedFlight: ActiveFlight?,
    onFlightSelected: (ActiveFlight?) -> Unit,
    onAirportSelected: (Airport?) -> Unit,
    modifier: Modifier = Modifier
) {
    var centerLat by remember { mutableFloatStateOf(20f) }
    var centerLon by remember { mutableFloatStateOf(-30f) }
    var zoomScale by remember { mutableFloatStateOf(1f) }

    val majorAirports = remember { AirportDatabase.getMajorAirports() }

    // Predefined simplified continent landmass points for clean vector outlines
    val continentOutlines = remember {
        listOf(
            // North America
            listOf(Pair(70.0, -160.0), Pair(60.0, -140.0), Pair(50.0, -125.0), Pair(35.0, -120.0), Pair(25.0, -110.0), Pair(18.0, -100.0), Pair(15.0, -90.0), Pair(20.0, -85.0), Pair(30.0, -80.0), Pair(40.0, -74.0), Pair(50.0, -60.0), Pair(65.0, -65.0), Pair(75.0, -95.0), Pair(70.0, -160.0)),
            // South America
            listOf(Pair(12.0, -75.0), Pair(5.0, -50.0), Pair(-5.0, -35.0), Pair(-20.0, -40.0), Pair(-35.0, -55.0), Pair(-55.0, -68.0), Pair(-45.0, -75.0), Pair(-20.0, -70.0), Pair(-5.0, -80.0), Pair(12.0, -75.0)),
            // Europe
            listOf(Pair(70.0, 25.0), Pair(60.0, 30.0), Pair(55.0, 20.0), Pair(45.0, 15.0), Pair(38.0, 24.0), Pair(36.0, -5.0), Pair(43.0, -9.0), Pair(50.0, -4.0), Pair(58.0, 5.0), Pair(65.0, 12.0), Pair(70.0, 25.0)),
            // Africa
            listOf(Pair(37.0, 10.0), Pair(30.0, 32.0), Pair(12.0, 44.0), Pair(-5.0, 40.0), Pair(-25.0, 33.0), Pair(-34.0, 20.0), Pair(-20.0, 12.0), Pair(5.0, 1.0), Pair(15.0, -17.0), Pair(28.0, -12.0), Pair(37.0, 10.0)),
            // Asia
            listOf(Pair(75.0, 100.0), Pair(70.0, 170.0), Pair(55.0, 160.0), Pair(40.0, 130.0), Pair(30.0, 122.0), Pair(20.0, 110.0), Pair(10.0, 104.0), Pair(1.0, 104.0), Pair(15.0, 80.0), Pair(25.0, 60.0), Pair(40.0, 50.0), Pair(60.0, 60.0), Pair(75.0, 100.0)),
            // Australia
            listOf(Pair(-12.0, 130.0), Pair(-15.0, 145.0), Pair(-25.0, 152.0), Pair(-38.0, 145.0), Pair(-35.0, 115.0), Pair(-20.0, 115.0), Pair(-12.0, 130.0))
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    // Drag rotates the 3D globe smoothly
                    centerLon = (centerLon - dragAmount.x * 0.45f) % 360f
                    centerLat = (centerLat + dragAmount.y * 0.45f).coerceIn(-75f, 75f)
                }
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    zoomScale = (zoomScale * zoom).coerceIn(0.75f, 2.2f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth / 2f
            val centerY = canvasHeight / 2f
            val baseRadius = min(canvasWidth, canvasHeight) * 0.42f
            val globeRadius = baseRadius * zoomScale

            // 1. Draw atmospheric space background glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF0F2338), Color(0xFF0A0E17)),
                    center = Offset(centerX, centerY),
                    radius = globeRadius * 1.35f
                ),
                radius = globeRadius * 1.3f,
                center = Offset(centerX, centerY)
            )

            // 2. Draw globe ocean surface sphere with edge shading
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF0E1A2D), Color(0xFF060B14)),
                    center = Offset(centerX - globeRadius * 0.2f, centerY - globeRadius * 0.2f),
                    radius = globeRadius
                ),
                radius = globeRadius,
                center = Offset(centerX, centerY)
            )

            // Globe sphere boundary rim
            drawCircle(
                color = Color(0xFF00E5FF).copy(alpha = 0.5f),
                radius = globeRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.5f)
            )

            // 3. Draw lat/long radar grid wireframes (parallels and meridians)
            val gridColor = Color(0xFF1E3A5F).copy(alpha = 0.45f)
            val strokeGrid = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)))

            // Parallels (Equator, Tropics, Arctic)
            listOf(-60.0, -30.0, 0.0, 30.0, 60.0).forEach { lat ->
                val points = (0..360 step 10).map { lon ->
                    FlightMath.projectOrthographic(lat, lon.toDouble(), centerLat.toDouble(), centerLon.toDouble(), globeRadius, centerX, centerY)
                }
                drawProjectedPath(points, gridColor, strokeGrid)
            }

            // Meridians
            (-180..180 step 45).forEach { lon ->
                val points = (-80..80 step 10).map { lat ->
                    FlightMath.projectOrthographic(lat.toDouble(), lon.toDouble(), centerLat.toDouble(), centerLon.toDouble(), globeRadius, centerX, centerY)
                }
                drawProjectedPath(points, gridColor, strokeGrid)
            }

            // 4. Draw continent landmass outlines
            val continentStroke = Stroke(width = 1.8f)
            val landFillColor = Color(0xFF16253B).copy(alpha = 0.7f)
            val landBorderColor = Color(0xFF38BDF8).copy(alpha = 0.6f)

            continentOutlines.forEach { outline ->
                val projected = outline.map { (lat, lon) ->
                    FlightMath.projectOrthographic(lat, lon, centerLat.toDouble(), centerLon.toDouble(), globeRadius, centerX, centerY)
                }
                drawProjectedPolygon(projected, landFillColor, landBorderColor, continentStroke)
            }

            // 5. Draw Major Airport Nodes
            majorAirports.forEach { airport ->
                val (ax, ay, isVis) = FlightMath.projectOrthographic(
                    airport.latitude, airport.longitude,
                    centerLat.toDouble(), centerLon.toDouble(),
                    globeRadius, centerX, centerY
                )
                if (isVis) {
                    // Airport glowing radar node
                    drawCircle(
                        color = Color(0xFF00E5FF).copy(alpha = 0.3f),
                        radius = 8f,
                        center = Offset(ax, ay)
                    )
                    drawCircle(
                        color = Color(0xFF00E5FF),
                        radius = 3.5f,
                        center = Offset(ax, ay)
                    )
                }
            }

            // 6. Draw Active Flight Great-Circle Trajectories and Aircraft Blips
            activeFlights.forEach { flight ->
                val isSelected = selectedFlight?.flightNumber == flight.flightNumber

                // Sample geodesic arc points
                val arcCoords = FlightMath.sampleGreatCirclePoints(
                    flight.originLat, flight.originLon,
                    flight.destLat, flight.destLon,
                    steps = 20
                )

                val projectedArc = arcCoords.map { (lat, lon) ->
                    FlightMath.projectOrthographic(
                        lat, lon,
                        centerLat.toDouble(), centerLon.toDouble(),
                        globeRadius, centerX, centerY
                    )
                }

                // Draw flight corridor arc line
                val arcColor = if (isSelected) Color(0xFFF59E0B) else Color(0xFF00E5FF).copy(alpha = 0.7f)
                val arcStroke = Stroke(
                    width = if (isSelected) 3f else 1.8f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f))
                )
                drawProjectedPath(projectedArc, arcColor, arcStroke)

                // Current aircraft position
                val (px, py, isPlaneVis) = FlightMath.projectOrthographic(
                    flight.currentLat, flight.currentLon,
                    centerLat.toDouble(), centerLon.toDouble(),
                    globeRadius, centerX, centerY
                )

                if (isPlaneVis) {
                    // Pulsing radar blip
                    drawCircle(
                        color = (if (isSelected) Color(0xFFF59E0B) else Color(0xFF10B981)).copy(alpha = 0.4f),
                        radius = 12f,
                        center = Offset(px, py)
                    )
                    // Aircraft center beacon
                    drawCircle(
                        color = if (isSelected) Color(0xFFF59E0B) else Color(0xFF10B981),
                        radius = 5.5f,
                        center = Offset(px, py)
                    )

                    // Draw flight vector heading indicator
                    val headingRad = FlightMath.toRadians(flight.headingDegrees.toDouble())
                    val vectorLen = 16f
                    val vx = px + (sin(headingRad) * vectorLen).toFloat()
                    val vy = py - (cos(headingRad) * vectorLen).toFloat()
                    drawLine(
                        color = Color.White,
                        start = Offset(px, py),
                        end = Offset(vx, vy),
                        strokeWidth = 2.5f
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawProjectedPath(
    points: List<Triple<Float, Float, Boolean>>,
    color: Color,
    stroke: Stroke
) {
    val path = Path()
    var isDrawing = false

    for (i in points.indices) {
        val (x, y, isVis) = points[i]
        if (isVis) {
            if (!isDrawing) {
                path.moveTo(x, y)
                isDrawing = true
            } else {
                path.lineTo(x, y)
            }
        } else {
            isDrawing = false
        }
    }
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawProjectedPolygon(
    points: List<Triple<Float, Float, Boolean>>,
    fillColor: Color,
    borderColor: Color,
    stroke: Stroke
) {
    val path = Path()
    var firstVisible = true
    var anyVisible = false

    for ((x, y, isVis) in points) {
        if (isVis) {
            anyVisible = true
            if (firstVisible) {
                path.moveTo(x, y)
                firstVisible = false
            } else {
                path.lineTo(x, y)
            }
        }
    }
    if (anyVisible) {
        drawPath(path, fillColor)
        drawPath(path, borderColor, style = stroke)
    }
}
