package com.example.lanzadorstm32

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lanzadorstm32.ui.ControlScreen
import com.example.lanzadorstm32.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestBluetoothPermissions()

        setContent {
            val vm: MainViewModel = viewModel()

            ControlScreen(
                isConnected = vm.isConnected,
                deviceName = vm.connectedDeviceName,
                telemetry = vm.telemetry,
                logText = vm.logText,
                pairedDevices = vm.pairedDevices,
                selectedDevice = vm.selectedDevice,
                onRefreshDevices = { vm.loadPairedDevices() },
                onSelectDevice = { device -> vm.selectDevice(device) },
                onConnect = { vm.connectSelectedDevice() },
                onDisconnect = { vm.disconnectBluetooth() },
                onSendCommand = { command -> vm.sendCommand(command) },
                onSimulateData = { vm.simulateTelemetry(125) },
                onAutoCalculate = { vm.calculateAutoShot() },
                onSimulateDistance = { distance -> vm.simulateTelemetry(distance) },
                onAutoFire = { vm.autoFire() }
            )
        }
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bluetoothPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            )
        }
    }
}