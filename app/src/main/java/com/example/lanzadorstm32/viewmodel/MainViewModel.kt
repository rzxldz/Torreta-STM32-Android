package com.example.lanzadorstm32.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lanzadorstm32.bluetooth.BluetoothService
import kotlin.math.abs

data class TelemetryData(
    val distanceCm: Int = 0,
    val angleDeg: Int = 0,
    val pwmPercent: Int = 0,
    val mode: String = "MANUAL"
)

class MainViewModel : ViewModel() {

    private val bluetoothService = BluetoothService()

    var isConnected by mutableStateOf(false)
        private set

    var connectedDeviceName by mutableStateOf("Sin conectar")
        private set

    var telemetry by mutableStateOf(TelemetryData())
        private set

    var logText by mutableStateOf("Esperando conexión...")
        private set

    var pairedDevices by mutableStateOf(listOf<BluetoothDevice>())
        private set

    var selectedDevice by mutableStateOf<BluetoothDevice?>(null)
        private set

    fun appendLog(message: String) {
        logText = "$message\n$logText"
    }

    @SuppressLint("MissingPermission")
    fun loadPairedDevices() {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        pairedDevices = adapter?.bondedDevices?.toList() ?: emptyList()
        appendLog("Dispositivos cargados: ${pairedDevices.size}")
    }

    fun selectDevice(device: BluetoothDevice) {
        selectedDevice = device
        appendLog("Seleccionado: ${device.name}")
    }

    @SuppressLint("MissingPermission")
    fun connectSelectedDevice() {
        val device = selectedDevice
        if (device == null) {
            appendLog("No has seleccionado ningún dispositivo")
            return
        }

        val success = bluetoothService.connect(device)
        if (success) {
            isConnected = true
            connectedDeviceName = device.name ?: "Dispositivo Bluetooth"
            appendLog("Conectado a ${device.name}")

            bluetoothService.startListening { data ->
                parseTelemetry(data)
            }

        } else {
            isConnected = false
            connectedDeviceName = "Sin conectar"
            appendLog("Error al conectar con ${device.name}")
        }
    }

    fun sendCommand(command: String) {
        if (bluetoothService.isConnected()) {
            val success = bluetoothService.sendCommand(command)
            if (success) {
                appendLog("ENVIADO: ${command.trim()}")
            } else {
                appendLog("Error al enviar comando")
            }
        } else {
            appendLog("No hay conexión Bluetooth")
        }
    }

    fun disconnectBluetooth() {
        bluetoothService.disconnect()
        isConnected = false
        connectedDeviceName = "Sin conectar"
        appendLog("Bluetooth desconectado")
    }

    fun simulateTelemetry(distance: Int = 125) {
        telemetry = telemetry.copy(
            distanceCm = distance,
            angleDeg = 0,
            pwmPercent = 0,
            mode = "SIM"
        )
        appendLog("[SIM] Distancia simulada: $distance cm")
    }

    fun calculateAutoShot() {
        val d = telemetry.distanceCm

        if (d <= 0) {
            appendLog("[AUTO] No hay distancia válida")
            return
        }

        appendLog("[SISTEMA] Objetivo detectado")
        appendLog("[SISTEMA] Calculando trayectoria...")

        val calibrationTable = listOf(
            Triple(50, 22, 35),
            Triple(75, 26, 42),
            Triple(100, 30, 50),
            Triple(125, 35, 60),
            Triple(150, 40, 70),
            Triple(175, 45, 80),
            Triple(200, 50, 90)
        )

        val closest = calibrationTable.minByOrNull { abs(it.first - d) }

        if (closest != null) {
            val angle = closest.second
            val force = closest.third

            telemetry = telemetry.copy(
                angleDeg = angle,
                pwmPercent = force,
                mode = "AUTO"
            )

            appendLog("[SISTEMA] Disparo listo")
            appendLog("[AUTO] Distancia=$d cm -> Ángulo=$angle°, Fuerza=$force%")
        } else {
            appendLog("[AUTO] No se encontró calibración")
        }
    }

    fun autoFire() {
        val d = telemetry.distanceCm

        if (d <= 0) {
            appendLog("[AUTO] No hay distancia válida")
            return
        }

        appendLog("[SISTEMA] Objetivo detectado")
        appendLog("[SISTEMA] Calculando trayectoria...")

        val calibrationTable = listOf(
            Triple(50, 22, 35),
            Triple(75, 26, 42),
            Triple(100, 30, 50),
            Triple(125, 35, 60),
            Triple(150, 40, 70),
            Triple(175, 45, 80),
            Triple(200, 50, 90)
        )

        val closest = calibrationTable.minByOrNull { abs(it.first - d) }

        if (closest != null) {
            val angle = closest.second
            val force = closest.third

            telemetry = telemetry.copy(
                angleDeg = angle,
                pwmPercent = force,
                mode = "AUTO"
            )

            appendLog("[SISTEMA] Disparo listo")
            appendLog("[AUTO] Distancia=$d cm -> Ángulo=$angle°, Fuerza=$force%")

            if (isConnected) {
                sendCommand("A\n")
                sendCommand("F:$force\n")
                sendCommand("S\n")
                appendLog("[AUTO] DISPARO EJECUTADO")
            } else {
                appendLog("[AUTO] No hay conexión Bluetooth para disparar")
            }
        } else {
            appendLog("[AUTO] No se encontró calibración")
        }
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothService.disconnect()
    }

    fun parseTelemetry(data: String) {
        val cleanData = data.replace("\r", "")
        val lines = cleanData.split("\n")

        for (line in lines) {
            val text = line.trim()

            when {
                text.startsWith("D:") -> {
                    val value = text.removePrefix("D:").toIntOrNull()
                    if (value != null) {
                        telemetry = telemetry.copy(distanceCm = value)
                    }
                }

                text.startsWith("A:") -> {
                    val value = text.removePrefix("A:").toIntOrNull()
                    if (value != null) {
                        telemetry = telemetry.copy(angleDeg = value)
                    }
                }

                text.startsWith("F:") -> {
                    val value = text.removePrefix("F:").toIntOrNull()
                    if (value != null) {
                        telemetry = telemetry.copy(pwmPercent = value)
                    }
                }

                text.startsWith("MODE:") -> {
                    val value = text.removePrefix("MODE:")
                    telemetry = telemetry.copy(mode = value)
                }

                text.startsWith("SHOT") -> {
                    appendLog("[RX] Disparo ejecutado")
                }

                text.startsWith("READY") -> {
                    appendLog("[RX] Sistema listo")
                }
            }
        }
    }

}