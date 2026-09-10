package com.example.lanzadorstm32.ui

import android.bluetooth.BluetoothDevice
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lanzadorstm32.viewmodel.TelemetryData

private val BgTop = Color(0xFF0A0F1F)
private val BgBottom = Color(0xFF151D36)
private val CardBg = Color(0xFF161D33)
private val CardBgSoft = Color(0xFF1C2644)
private val BorderColor = Color(0xFF2D375A)
private val TextPrimary = Color(0xFFF5F7FF)
private val TextSecondary = Color(0xFFB2BEDD)
private val AccentBlue = Color(0xFF4DA3FF)
private val AccentPurple = Color(0xFF8B5CF6)
private val AccentGreen = Color(0xFF22C55E)
private val AccentRed = Color(0xFFEF4444)
private val AccentOrange = Color(0xFFF59E0B)
private val AccentCyan = Color(0xFF22D3EE)

@Composable
fun ControlScreen(
    isConnected: Boolean,
    deviceName: String,
    telemetry: TelemetryData,
    logText: String,
    pairedDevices: List<BluetoothDevice>,
    selectedDevice: BluetoothDevice?,
    onRefreshDevices: () -> Unit,
    onSelectDevice: (BluetoothDevice) -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onSendCommand: (String) -> Unit,
    onSimulateData: () -> Unit,
    onAutoCalculate: () -> Unit,
    onSimulateDistance: (Int) -> Unit,
    onAutoFire: () -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(50f) }

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val statusColor = if (isConnected) AccentGreen else AccentRed
    val modeColor = when (telemetry.mode.uppercase()) {
        "AUTO" -> AccentBlue
        "SIM" -> AccentOrange
        else -> AccentPurple
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(BgTop, BgBottom))
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderSection(
            isConnected = isConnected,
            deviceName = deviceName,
            statusColor = statusColor,
            pulseAlpha = pulseAlpha
        )

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.97f),
            exit = fadeOut() + scaleOut()
        ) {
            DashboardCard {
                SectionTitle("Telemetría", Icons.Default.Radar)

                Spacer(modifier = Modifier.height(14.dp))

                LiveReadingCard(telemetry.distanceCm)

                Spacer(modifier = Modifier.height(14.dp))

                TelemetryRow("Distancia capturada", "${telemetry.distanceCm} cm", AccentCyan, Icons.Default.GpsFixed)
                TelemetryRow("Ángulo calculado", "${telemetry.angleDeg}°", AccentPurple, Icons.Default.Tune)
                TelemetryRow("Potencia", "${telemetry.pwmPercent}%", AccentOrange, Icons.Default.Speed)
                TelemetryRow("Modo", telemetry.mode, modeColor, Icons.Default.SettingsInputAntenna)

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedActionButton(
                    text = "Simular datos",
                    icon = Icons.Default.PlayArrow,
                    onClick = onSimulateData
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Pruebas rápidas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickDistanceChip("50") { onSimulateDistance(50) }
                    QuickDistanceChip("100") { onSimulateDistance(100) }
                    QuickDistanceChip("150") { onSimulateDistance(150) }
                    QuickDistanceChip("200") { onSimulateDistance(200) }
                }

                Spacer(modifier = Modifier.height(14.dp))

                ProButton(
                    text = "Calcular disparo automático",
                    icon = Icons.Default.Calculate,
                    enabled = telemetry.distanceCm > 0,
                    color = AccentBlue,
                    onClick = onAutoCalculate
                )

                Spacer(modifier = Modifier.height(10.dp))

                ProButton(
                    text = "AUTO DISPARO",
                    icon = Icons.Default.FlashOn,
                    enabled = isConnected && telemetry.distanceCm > 0,
                    color = AccentRed,
                    onClick = onAutoFire
                )
            }
        }

        DashboardCard {
            SectionTitle("Bluetooth", Icons.Default.Bluetooth)

            Spacer(modifier = Modifier.height(12.dp))

            ProButton(
                text = "Cargar dispositivos",
                icon = Icons.Default.Bluetooth,
                enabled = true,
                color = AccentPurple,
                onClick = onRefreshDevices
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Seleccionado: ${selectedDevice?.name ?: "Ninguno"}",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                pairedDevices.forEach { device ->
                    val isSelected = selectedDevice?.address == device.address

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectDevice(device) }
                            .border(
                                width = if (isSelected) 1.6.dp else 1.dp,
                                color = if (isSelected) AccentBlue else BorderColor,
                                shape = RoundedCornerShape(18.dp)
                            ),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) CardBgSoft else CardBg
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = device.name ?: "Sin nombre",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = device.address,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            AnimatedVisibility(visible = isSelected) {
                                Text(
                                    text = "Seleccionado",
                                    color = AccentBlue,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProButton(
                    text = "Conectar",
                    icon = Icons.Default.PowerSettingsNew,
                    enabled = !isConnected && selectedDevice != null,
                    color = AccentGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onConnect
                )

                ProButton(
                    text = "Desconectar",
                    icon = Icons.Default.PowerSettingsNew,
                    enabled = isConnected,
                    color = AccentRed,
                    modifier = Modifier.weight(1f),
                    onClick = onDisconnect
                )
            }
        }

        DashboardCard {
            SectionTitle("Control manual", Icons.Default.DirectionsRun)

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProButton(
                    text = "Izquierda",
                    icon = Icons.Default.Send,
                    enabled = isConnected,
                    color = AccentPurple,
                    modifier = Modifier.weight(1f),
                    onClick = { onSendCommand("L\n") }
                )

                ProButton(
                    text = "Derecha",
                    icon = Icons.Default.Send,
                    enabled = isConnected,
                    color = AccentPurple,
                    modifier = Modifier.weight(1f),
                    onClick = { onSendCommand("R\n") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProButton(
                    text = "Manual",
                    icon = Icons.Default.Tune,
                    enabled = isConnected,
                    color = AccentOrange,
                    modifier = Modifier.weight(1f),
                    onClick = { onSendCommand("M\n") }
                )

                ProButton(
                    text = "Automático",
                    icon = Icons.Default.Calculate,
                    enabled = isConnected,
                    color = AccentBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { onSendCommand("A\n") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Fuerza manual: ${sliderValue.toInt()}%",
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 0f..100f,
                enabled = isConnected,
                colors = SliderDefaults.colors(
                    thumbColor = AccentBlue,
                    activeTrackColor = AccentBlue,
                    inactiveTrackColor = BorderColor
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            ProButton(
                text = "Enviar fuerza",
                icon = Icons.Default.Speed,
                enabled = isConnected,
                color = AccentCyan,
                onClick = { onSendCommand("F:${sliderValue.toInt()}\n") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            ProButton(
                text = "DISPARAR",
                icon = Icons.Default.FlashOn,
                enabled = isConnected,
                color = AccentRed,
                onClick = { onSendCommand("S\n") }
            )
        }

        DashboardCard {
            SectionTitle("Log del sistema", Icons.Default.SettingsInputAntenna)

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(10.dp))

            AnimatedContent(
                targetState = logText.ifBlank { "Sin eventos todavía." },
                label = "logAnimation"
            ) { text ->
                Text(
                    text = text,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    isConnected: Boolean,
    deviceName: String,
    statusColor: Color,
    pulseAlpha: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF18213D), Color(0xFF121A31))
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "Lanzador STM32",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .alpha(pulseAlpha)
                            .clip(CircleShape)
                            .background(statusColor)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (isConnected) "Conectado" else "Desconectado",
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (isConnected) deviceName else "Sin conexión Bluetooth",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun SectionTitle(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentBlue
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
private fun TelemetryRow(
    label: String,
    value: String,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(accent, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun QuickDistanceChip(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CardBgSoft,
            contentColor = TextPrimary
        )
    ) {
        Text(text)
    }
}

@Composable
private fun ProButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF2A3048),
            disabledContentColor = Color(0xFF7E89AD)
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun OutlinedActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextPrimary,
            containerColor = CardBgSoft
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
private fun DistanceBar(distanceCm: Int) {
    val progress = (distanceCm.coerceIn(0, 200) / 200f)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0 cm", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            Text("Distancia objetivo", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            Text("200 cm", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFF25304F))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(14.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(AccentCyan, AccentBlue, AccentPurple)
                        )
                    )
            )
        }
    }
}

@Composable
private fun LiveReadingCard(distanceCm: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            AccentCyan
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Lectura en vivo",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$distanceCm cm",
                color = AccentCyan,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Distancia capturada por el HC-SR04",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}