package com.example.lanzadorstm32.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BluetoothService {

    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private var listening = false

    private val uuid: UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice): Boolean {
        return try {
            socket = device.createRfcommSocketToServiceRecord(uuid)
            BluetoothAdapter.getDefaultAdapter()?.cancelDiscovery()
            socket?.connect()

            outputStream = socket?.outputStream
            inputStream = socket?.inputStream
            true
        } catch (e: Exception) {
            e.printStackTrace()
            disconnect()
            false
        }
    }

    fun sendCommand(command: String): Boolean {
        return try {
            outputStream?.write(command.toByteArray())
            outputStream?.flush()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun startListening(onDataReceived: (String) -> Unit) {
        listening = true

        Thread {
            val buffer = ByteArray(1024)

            while (listening && socket?.isConnected == true) {
                try {
                    val bytes = inputStream?.read(buffer) ?: -1

                    if (bytes > 0) {
                        val data = String(buffer, 0, bytes)
                        onDataReceived(data)
                    }
                } catch (e: Exception) {
                    listening = false
                    break
                }
            }
        }.start()
    }

    fun disconnect() {
        listening = false

        try {
            inputStream?.close()
            outputStream?.close()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isConnected(): Boolean {
        return socket?.isConnected == true
    }
}