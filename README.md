Torreta STM32 + Android

Proyecto académico de Sistemas Embebidos desarrollado en la Universidad Iberoamericana Ciudad de México.

El sistema integra una aplicación Android con un microcontrolador STM32 para controlar y supervisar una torreta física mediante comunicación Bluetooth. La aplicación permite gestionar la conexión, enviar comandos de control y visualizar telemetría recibida desde el sistema embebido.

Capturas

Telemetría y estado del sistema



Bluetooth y control manual



Funcionalidades

Conexión Bluetooth con el sistema embebido.

Selección y conexión de dispositivos compatibles.

Control manual de movimiento.

Cambio entre modos manual y automático.

Envío de comandos desde la aplicación Android.

Recepción y visualización de telemetría.

Lectura de distancia obtenida mediante sensor ultrasónico.

Visualización del estado de conexión y log del sistema.

Herramientas de simulación para pruebas de interfaz.

Tecnologías

Aplicación Android

Kotlin

Jetpack Compose

Android SDK

Android Studio

Bluetooth

ViewModel

Material Design

Gradle Kotlin DSL

Sistema embebido

STM32 Blue Pill

C

UART

PWM

HC-05 Bluetooth

HC-SR04

Servomotor

Motor DC

Driver L298N

Arquitectura general

Aplicación Android
      ↓
Bluetooth / HC-05
      ↓
UART
      ↓
STM32
      ↓
Sensores y actuadores

La aplicación envía comandos al STM32 y recibe información de telemetría a través del enlace Bluetooth.

Estructura principal de la aplicación

app/src/main/java/com/example/lanzadorstm32/

├── MainActivity.kt
├── bluetooth/
│   └── BluetoothService.kt
├── ui/
│   └── ControlScreen.kt
└── viewmodel/
    └── MainViewModel.kt

Mi contribución

Jorge Emmanuel Roldán Márquez

Me encargué principalmente del desarrollo de software y de la integración entre la aplicación Android y el hardware.

Responsabilidades principales:

Desarrollo de la aplicación Android en Kotlin.

Diseño e implementación de la interfaz.

Implementación de la comunicación Bluetooth.

Programación de comandos y recepción de telemetría.

Integración del software con el STM32.

Conexión e integración de los componentes electrónicos.

Configuración y pruebas de comunicación entre Android, HC-05 y STM32.

Pruebas y depuración del sistema completo.

Colaborador

Jorge Olaf Quijas Pérez

Su participación se enfocó principalmente en:

Diseño y construcción física de la torreta.

Apoyo en la selección de componentes de hardware.

Construcción y adaptación de la estructura mecánica.

Colaboración durante las pruebas e integración del sistema.

Documentación

El reporte técnico completo del proyecto se encuentra en:

Ver reporte técnico

El documento incluye la descripción general del sistema, componentes utilizados, protocolo de comunicación y documentación académica del proyecto.

Ejecución de la aplicación Android

Clonar el repositorio.

git clone https://github.com/rzxldz/Torreta-STM32-Android.git

Abrir el proyecto en Android Studio.

Sincronizar las dependencias de Gradle.

Ejecutar la aplicación en un dispositivo o emulador Android compatible.

Para utilizar las funciones Bluetooth con el sistema físico es necesario disponer del hardware correspondiente.

Estado del proyecto

El proyecto cuenta con una aplicación Android funcional para control, conexión Bluetooth y visualización de telemetría del sistema embebido.

Autores

Jorge Emmanuel Roldán Márquez
Desarrollo de software e integración hardware/software

Jorge Olaf Quijas Pérez
Construcción física y apoyo en selección de hardware

Universidad Iberoamericana Ciudad de México
Sistemas Embebidos · 2025
