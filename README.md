# Busify - Gestión de Transporte 

Aplicación móvil para la administración de cooperativas de transporte, flotas de buses, conductores y programación de viajes en tiempo real. Construida con Kotlin, Jetpack Compose y Clean Architecture.

## Requisitos de instalación

* Android Studio Ladybug (2024.2.1) o superior.
* JDK 17 o superior.
* Android SDK 24+ (Compilación con API 34).
* Gradle 8.7.

## Configuración de la URL base

La URL base se define en el archivo local.properties del proyecto:

```properties
API_BASE_URL=https://busify.daelabs.tech/api/
```

## Usuario y contraseña de prueba (ejemplo)

Para acceder al sistema, puede usar las siguientes credenciales de ejemplo:

* Usuario: admin@ejemplo.com
* Contraseña: Password123*

## Entidades implementadas

1. User: Gestión de identidad y roles de acceso (Administrador o Cliente).
2. Bus: Registro de unidades físicas, placas y capacidad de pasajeros.
3. Chofer: Información de conductores, licencias y disponibilidad.
4. Ruta: Definición de trayectos con origen, destino y distancia.
5. Parada: Puntos geográficos intermedios con secuencia dentro de una ruta.
6. Viaje (Despacho): Operación que vincula bus, chofer y ruta en un horario.
7. Cooperativa: Organización que agrupa los recursos y servicios de transporte.

## Listado de pantallas

* Autenticación: Login, Registro, Verificación de cuenta.
* Administración: Dashboard informativo, Gestión de Usuarios, Buses, Choferes, Rutas y Monitoreo de Despachos.
* Cliente: Perfil de usuario y consulta de viajes programados.

## Consumo de API con token

Se utiliza un interceptor de OkHttp para incluir el JWT almacenado en DataStore en el header Authorization: Bearer <token>.

```kotlin
interface ViajeApi {
    @GET("viajes/activos/")
    suspend fun getViajesActivos(@Query("page") page: Int): Response<ViajeListResponse>
}
```

## Instrucciones para ejecutar la app

1. Clonar el repositorio.
2. Abrir en Android Studio y sincronizar con Gradle.
3. Configurar la URL base en local.properties.
4. Ejecutar en un dispositivo o emulador (API 26+) con el botón Run o ./gradlew installDebug.
### Recursos y Agradecimientos
* Basado en las guías y tutoriales de desarrollo de [Ing. Francisco Higuera](https://franciscohiguera.site/about).
---
**Versión:** 1.0.0
