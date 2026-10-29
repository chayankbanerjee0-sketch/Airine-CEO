package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.example.data.model.CheatlineStyle
import com.example.data.model.LiveryDesign
import com.example.data.model.TailLogoType

@Composable
fun LiveryPainterCanvas(
    livery: LiveryDesign,
    airlineName: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Scale aircraft drawing to fit comfortably
            val scale = (w / 800f).coerceIn(0.6f, 1.4f)
            val originX = w * 0.10f
            val originY = h * 0.50f

            val fuselageColor = Color(livery.fuselageColor)
            val bellyColor = Color(livery.bellyColor)
            val tailColor = Color(livery.tailFinColor)
            val cowlingColor = Color(livery.engineCowlingColor)
            val cheatlineColor = Color(livery.cheatlineColor)

            // 1. Draw Tail Fin (Behind and on top of empennage)
            val tailPath = Path().apply {
                moveTo(originX + 480f * scale, originY - 10f * scale)
                lineTo(originX + 540f * scale, originY - 140f * scale)
                lineTo(originX + 580f * scale, originY - 140f * scale)
                lineTo(originX + 560f * scale, originY + 15f * scale)
                close()
            }
            drawPath(tailPath, tailColor)
            drawPath(tailPath, Color(0xFF1E293B), style = Stroke(width = 2f))

            // Draw Tail Logo Emblem
            drawTailLogo(
                logoType = livery.tailLogo,
                centerX = originX + 548f * scale,
                centerY = originY - 95f * scale,
                size = 28f * scale
            )

            // Horizontal stabilizer
            val horizStab = Path().apply {
                moveTo(originX + 510f * scale, originY + 5f * scale)
                lineTo(originX + 580f * scale, originY - 20f * scale)
                lineTo(originX + 590f * scale, originY - 10f * scale)
                lineTo(originX + 530f * scale, originY + 15f * scale)
                close()
            }
            drawPath(horizStab, tailColor)
            drawPath(horizStab, Color(0xFF1E293B), style = Stroke(width = 1.5f))

            // 2. Draw Main Fuselage Body
            val fuselageTop = Path().apply {
                // Nose radome
                moveTo(originX + 20f * scale, originY + 25f * scale)
                cubicTo(
                    originX + 30f * scale, originY - 5f * scale,
                    originX + 70f * scale, originY - 25f * scale,
                    originX + 130f * scale, originY - 25f * scale
                )
                // Fuselage top roofline
                lineTo(originX + 480f * scale, originY - 25f * scale)
                // Taper to tail cone
                cubicTo(
                    originX + 530f * scale, originY - 20f * scale,
                    originX + 570f * scale, originY - 5f * scale,
                    originX + 590f * scale, originY + 10f * scale
                )
                // Bottom tail cone
                lineTo(originX + 560f * scale, originY + 25f * scale)
                // Middle floor line
                lineTo(originX + 20f * scale, originY + 25f * scale)
                close()
            }
            drawPath(fuselageTop, fuselageColor)

            // 3. Draw Belly Section (Lower Fuselage)
            val fuselageBelly = Path().apply {
                moveTo(originX + 20f * scale, originY + 25f * scale)
                lineTo(originX + 560f * scale, originY + 25f * scale)
                cubicTo(
                    originX + 520f * scale, originY + 45f * scale,
                    originX + 460f * scale, originY + 55f * scale,
                    originX + 350f * scale, originY + 55f * scale
                )
                lineTo(originX + 120f * scale, originY + 55f * scale)
                cubicTo(
                    originX + 60f * scale, originY + 50f * scale,
                    originX + 25f * scale, originY + 40f * scale,
                    originX + 20f * scale, originY + 25f * scale
                )
                close()
            }
            drawPath(fuselageBelly, bellyColor)

            // 4. Draw Selected Cheatline Accent Pattern
            when (livery.cheatlineStyle) {
                CheatlineStyle.CLASSIC_STRIPE -> {
                    // Straight sharp racing stripe
                    drawRect(
                        color = cheatlineColor,
                        topLeft = Offset(originX + 55f * scale, originY + 20f * scale),
                        size = androidx.compose.ui.geometry.Size(480f * scale, 8f * scale)
                    )
                }
                CheatlineStyle.MODERN_SWOOP -> {
                    val swoop = Path().apply {
                        moveTo(originX + 50f * scale, originY + 30f * scale)
                        cubicTo(
                            originX + 200f * scale, originY + 28f * scale,
                            originX + 380f * scale, originY + 8f * scale,
                            originX + 520f * scale, originY - 15f * scale
                        )
                        lineTo(originX + 530f * scale, originY - 5f * scale)
                        cubicTo(
                            originX + 380f * scale, originY + 20f * scale,
                            originX + 200f * scale, originY + 38f * scale,
                            originX + 50f * scale, originY + 36f * scale
                        )
                        close()
                    }
                    drawPath(swoop, cheatlineColor)
                }
                CheatlineStyle.BILLBOARD_BOLD -> {
                    // Bold lower stripe
                    drawRect(
                        color = cheatlineColor,
                        topLeft = Offset(originX + 70f * scale, originY + 14f * scale),
                        size = androidx.compose.ui.geometry.Size(430f * scale, 14f * scale)
                    )
                }
                CheatlineStyle.GRADIENT_TWILIGHT -> {
                    val wave = Path().apply {
                        moveTo(originX + 40f * scale, originY + 15f * scale)
                        cubicTo(
                            originX + 180f * scale, originY - 5f * scale,
                            originX + 320f * scale, originY + 40f * scale,
                            originX + 500f * scale, originY + 5f * scale
                        )
                        lineTo(originX + 505f * scale, originY + 15f * scale)
                        cubicTo(
                            originX + 320f * scale, originY + 48f * scale,
                            originX + 180f * scale, originY + 5f * scale,
                            originX + 40f * scale, originY + 25f * scale
                        )
                        close()
                    }
                    drawPath(wave, cheatlineColor)
                }
                CheatlineStyle.EUROWHITE_MINIMAL -> {
                    // Sleek thin accent line near cockpit
                    drawRect(
                        color = cheatlineColor,
                        topLeft = Offset(originX + 50f * scale, originY + 18f * scale),
                        size = androidx.compose.ui.geometry.Size(120f * scale, 4f * scale)
                    )
                }
            }

            // 5. Draw Cockpit Windshield & Passenger Windows
            // Cockpit windshield
            val cockpitWindow = Path().apply {
                moveTo(originX + 45f * scale, originY + 2f * scale)
                lineTo(originX + 68f * scale, originY - 8f * scale)
                lineTo(originX + 74f * scale, originY + 2f * scale)
                lineTo(originX + 48f * scale, originY + 8f * scale)
                close()
            }
            drawPath(cockpitWindow, Color(0xFF0F172A))

            // Row of passenger cabin windows
            val windowCount = 26
            val startWinX = originX + 90f * scale
            val winY = originY - 5f * scale
            val winSpacing = 15f * scale
            for (i in 0 until windowCount) {
                drawRoundRect(
                    color = Color(0xFF0F172A),
                    topLeft = Offset(startWinX + i * winSpacing, winY),
                    size = androidx.compose.ui.geometry.Size(6f * scale, 9f * scale),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                )
            }

            // 6. Draw Main Swept Wing & Jet Engine Cowling
            val wing = Path().apply {
                moveTo(originX + 220f * scale, originY + 20f * scale)
                lineTo(originX + 280f * scale, originY + 115f * scale)
                lineTo(originX + 310f * scale, originY + 115f * scale)
                lineTo(originX + 320f * scale, originY + 20f * scale)
                close()
            }
            drawPath(wing, Color(0xFF94A3B8))
            drawPath(wing, Color(0xFF334155), style = Stroke(width = 1.5f))

            // Jet Engine Nacelle & Pylon
            val enginePylon = Path().apply {
                moveTo(originX + 245f * scale, originY + 45f * scale)
                lineTo(originX + 245f * scale, originY + 75f * scale)
                lineTo(originX + 265f * scale, originY + 75f * scale)
                lineTo(originX + 265f * scale, originY + 45f * scale)
                close()
            }
            drawPath(enginePylon, Color(0xFF64748B))

            // Engine Cowling
            drawRoundRect(
                color = cowlingColor,
                topLeft = Offset(originX + 225f * scale, originY + 68f * scale),
                size = androidx.compose.ui.geometry.Size(65f * scale, 28f * scale),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f * scale, 10f * scale)
            )
            // Engine Intake spinner ring
            drawOval(
                color = Color(0xFF0F172A),
                topLeft = Offset(originX + 220f * scale, originY + 70f * scale),
                size = androidx.compose.ui.geometry.Size(12f * scale, 24f * scale)
            )

            // Outline of complete aircraft
            val fuselageOutline = Path().apply {
                moveTo(originX + 20f * scale, originY + 25f * scale)
                cubicTo(
                    originX + 30f * scale, originY - 5f * scale,
                    originX + 70f * scale, originY - 25f * scale,
                    originX + 130f * scale, originY - 25f * scale
                )
                lineTo(originX + 480f * scale, originY - 25f * scale)
                cubicTo(
                    originX + 530f * scale, originY - 20f * scale,
                    originX + 570f * scale, originY - 5f * scale,
                    originX + 590f * scale, originY + 10f * scale
                )
                lineTo(originX + 560f * scale, originY + 25f * scale)
                cubicTo(
                    originX + 520f * scale, originY + 45f * scale,
                    originX + 460f * scale, originY + 55f * scale,
                    originX + 350f * scale, originY + 55f * scale
                )
                lineTo(originX + 120f * scale, originY + 55f * scale)
                cubicTo(
                    originX + 60f * scale, originY + 50f * scale,
                    originX + 25f * scale, originY + 40f * scale,
                    originX + 20f * scale, originY + 25f * scale
                )
                close()
            }
            drawPath(fuselageOutline, Color(0xFF334155), style = Stroke(width = 2.5f))
        }
    }
}

private fun DrawScope.drawTailLogo(logoType: TailLogoType, centerX: Float, centerY: Float, size: Float) {
    val gold = Color(0xFFF59E0B)
    val cyan = Color(0xFF00E5FF)
    val white = Color.White

    when (logoType) {
        TailLogoType.GOLDEN_WINGS -> {
            // Swept golden wing emblem
            val wing1 = Path().apply {
                moveTo(centerX, centerY + size * 0.4f)
                cubicTo(centerX - size * 0.8f, centerY, centerX - size, centerY - size * 0.8f, centerX - size * 0.2f, centerY - size * 0.5f)
                close()
            }
            val wing2 = Path().apply {
                moveTo(centerX, centerY + size * 0.4f)
                cubicTo(centerX + size * 0.8f, centerY, centerX + size, centerY - size * 0.8f, centerX + size * 0.2f, centerY - size * 0.5f)
                close()
            }
            drawPath(wing1, gold)
            drawPath(wing2, gold)
            drawCircle(white, radius = size * 0.18f, center = Offset(centerX, centerY + size * 0.1f))
        }
        TailLogoType.RADAR_STAR -> {
            // 4-point aerodynamic compass star
            val star = Path().apply {
                moveTo(centerX, centerY - size * 0.9f)
                lineTo(centerX + size * 0.25f, centerY - size * 0.25f)
                lineTo(centerX + size * 0.9f, centerY)
                lineTo(centerX + size * 0.25f, centerY + size * 0.25f)
                lineTo(centerX, centerY + size * 0.9f)
                lineTo(centerX - size * 0.25f, centerY + size * 0.25f)
                lineTo(centerX - size * 0.9f, centerY)
                lineTo(centerX - size * 0.25f, centerY - size * 0.25f)
                close()
            }
            drawPath(star, cyan)
            drawCircle(white, radius = size * 0.2f, center = Offset(centerX, centerY))
        }
        TailLogoType.AERO_CHEVRON -> {
            // High-speed dual chevrons
            val ch1 = Path().apply {
                moveTo(centerX - size * 0.7f, centerY + size * 0.3f)
                lineTo(centerX, centerY - size * 0.6f)
                lineTo(centerX + size * 0.7f, centerY + size * 0.3f)
                lineTo(centerX, centerY - size * 0.1f)
                close()
            }
            drawPath(ch1, cyan)
        }
        TailLogoType.GLOBAL_COMPASS -> {
            // Globe circle with ring
            drawCircle(cyan, radius = size * 0.6f, center = Offset(centerX, centerY), style = Stroke(width = 3f))
            drawCircle(gold, radius = size * 0.35f, center = Offset(centerX, centerY))
        }
        TailLogoType.SPEED_BIRD -> {
            val bird = Path().apply {
                moveTo(centerX - size * 0.8f, centerY + size * 0.3f)
                lineTo(centerX + size * 0.8f, centerY - size * 0.5f)
                lineTo(centerX + size * 0.2f, centerY)
                lineTo(centerX - size * 0.3f, centerY + size * 0.1f)
                close()
            }
            drawPath(bird, white)
        }
    }
}
