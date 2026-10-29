package com.example.simulation

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object FlightMath {

    private const val EARTH_RADIUS_KM = 6371.0

    fun toRadians(deg: Double): Double = deg * PI / 180.0
    fun toDegrees(rad: Double): Double = rad * 180.0 / PI

    /**
     * Great-circle distance between two coordinates in kilometers using Haversine formula
     */
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val dLat = toRadians(lat2 - lat1)
        val dLon = toRadians(lon2 - lon1)
        val rLat1 = toRadians(lat1)
        val rLat2 = toRadians(lat2)

        val a = sin(dLat / 2).pow(2.0) +
                sin(dLon / 2).pow(2.0) * cos(rLat1) * cos(rLat2)
        val c = 2 * asin(sqrt(a).coerceIn(-1.0, 1.0))
        return (EARTH_RADIUS_KM * c).toInt().coerceAtLeast(50)
    }

    /**
     * Slerp (Spherical Linear Interpolation) to find the exact (lat, lon) along the great circle
     * at fraction `t` (0.0 <= t <= 1.0).
     */
    fun interpolateGreatCircle(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
        t: Float
    ): Pair<Double, Double> {
        val p = t.coerceIn(0f, 1f).toDouble()
        val rLat1 = toRadians(lat1)
        val rLon1 = toRadians(lon1)
        val rLat2 = toRadians(lat2)
        val rLon2 = toRadians(lon2)

        val dLon = rLon2 - rLon1
        val angularDist = 2 * asin(
            sqrt(
                sin((rLat2 - rLat1) / 2).pow(2.0) +
                        cos(rLat1) * cos(rLat2) * sin(dLon / 2).pow(2.0)
            ).coerceIn(-1.0, 1.0)
        )

        if (angularDist < 1e-6) {
            return Pair(lat1, lon1)
        }

        val a = sin((1 - p) * angularDist) / sin(angularDist)
        val b = sin(p * angularDist) / sin(angularDist)

        val x = a * cos(rLat1) * cos(rLon1) + b * cos(rLat2) * cos(rLon2)
        val y = a * cos(rLat1) * sin(rLon1) + b * cos(rLat2) * sin(rLon2)
        val z = a * sin(rLat1) + b * sin(rLat2)

        val interLat = atan2(z, sqrt(x * x + y * y))
        val interLon = atan2(y, x)

        return Pair(toDegrees(interLat), toDegrees(interLon))
    }

    /**
     * Initial compass bearing from point 1 to point 2 (0 - 360 degrees)
     */
    fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val rLat1 = toRadians(lat1)
        val rLat2 = toRadians(lat2)
        val dLon = toRadians(lon2 - lon1)

        val y = sin(dLon) * cos(rLat2)
        val x = cos(rLat1) * sin(rLat2) - sin(rLat1) * cos(rLat2) * cos(dLon)
        val initialBearing = toDegrees(atan2(y, x))
        return ((initialBearing + 360.0) % 360.0).toFloat()
    }

    /**
     * 3D Spherical Orthographic Projection:
     * Projects spherical (lat, lon) onto 2D screen coords (x, y) with z depth.
     * If z > 0, the point is on the visible front hemisphere.
     */
    fun projectOrthographic(
        lat: Double,
        lon: Double,
        centerLat: Double,
        centerLon: Double,
        globeRadius: Float,
        centerX: Float,
        centerY: Float
    ): Triple<Float, Float, Boolean> {
        val phi = toRadians(lat)
        val lambda = toRadians(lon)
        val phi0 = toRadians(centerLat)
        val lambda0 = toRadians(centerLon)

        val cosC = sin(phi0) * sin(phi) + cos(phi0) * cos(phi) * cos(lambda - lambda0)
        val isVisible = cosC > 0 // On front hemisphere

        val x = centerX + globeRadius * (cos(phi) * sin(lambda - lambda0)).toFloat()
        val y = centerY - globeRadius * (cos(phi0) * sin(phi) - sin(phi0) * cos(phi) * cos(lambda - lambda0)).toFloat()

        return Triple(x, y, isVisible)
    }

    /**
     * Sample great circle into multiple screen points for smooth arc curve drawing
     */
    fun sampleGreatCirclePoints(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
        steps: Int = 24
    ): List<Pair<Double, Double>> {
        val points = mutableListOf<Pair<Double, Double>>()
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            points.add(interpolateGreatCircle(lat1, lon1, lat2, lon2, t))
        }
        return points
    }
}
