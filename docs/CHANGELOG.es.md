This document is also available in English: [CHANGELOG.md](docs/CHANGELOG.md)

# Registro de Cambios

Todos los cambios notables en ImagePickerKMP serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto sigue [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Sin Publicar]

### Añadido

- **🗜️ Compresión Automática de Imágenes**: Sistema completo de compresión para cámara y galería
  - **Niveles de Compresión Configurables**: LOW (95% calidad, 2560px), MEDIUM (75% calidad, 1920px), HIGH (50% calidad, 1280px)
  - **Soporte Multi-formato**: Compresión JPEG, PNG, HEIC, HEIF, WebP, GIF, BMP
  - **Procesamiento Asíncrono**: UI no bloqueante con integración de Kotlin Coroutines
  - **Optimización Inteligente**: Combina escalado de dimensiones + compresión de calidad
  - **Eficiencia de Memoria**: Reciclaje automático de bitmaps y limpieza
  - **API Unificada**: Misma lógica de compresión para captura de cámara y selección de galería
  - **Multiplataforma**: Funciona en Android e iOS
  - **Optimizado para Rendimiento**: Procesamiento en segundo plano con manejo adecuado de hilos

- **🔄 Gestión Automática de Contexto**: La función `applyCrop` ahora maneja automáticamente la gestión de contexto de Android
  - **Integración @Composable**: La función ahora es `@Composable` y usa `LocalContext.current` internamente
  - **API Simplificada**: Los desarrolladores ya no necesitan proporcionar manualmente el contexto de Android
  - **Consistencia Multiplataforma**: Misma signatura de API para implementaciones de Android e iOS

### Cambiado

- Actualizado `CameraCaptureConfig` con nuevo parámetro `compressionLevel: CompressionLevel?`
- Mejorado `GalleryPickerLauncher` para soportar compresión a través de `cameraCaptureConfig`
- Mejorado pipeline de procesamiento de imágenes con arquitectura unificada de compresión
- Actualizada documentación con ejemplos y guías completas de compresión
- **Función `applyCrop` convertida a @Composable**: Signatura de función actualizada para eliminar requisito de parámetro de contexto manual
- **Cálculos mejorados de relación de aspecto de crop**: Mejor manejo de relaciones de aspecto verticales (como 9:16) con mejor gestión de espacio

### Corregido

- Corregida lógica de compresión invertida (HIGH compresión ahora produce archivos más pequeños como se esperaba)
- Corregido algoritmo de escalado de imagen para calidad consistente entre niveles de compresión
- Resueltas fallas de pruebas CompressionConfig excluyendo comodín IMAGE_ALL de formatos soportados
- **🖼️ Corregidos Cálculos de Coordenadas de Crop en iOS**: Resueltos problemas de recorte de imagen en iOS donde las imágenes recortadas aparecían incorrectamente centradas
  - **Comportamiento Multiplataforma Consistente**: iOS ahora usa la misma lógica de cálculo de coordenadas que Android
  - **Posicionamiento Preciso de Imagen**: Corregidos cálculos de `displayedImageSize` e `imageOffset` para escalado y centrado adecuado de imagen
  - **Mapeo Corregido de Rectángulo de Crop**: Implementado cálculo adecuado de `adjustedCropRect` con factores de escalado precisos
- **🎯 Corregidos Conflictos de Z-Index en Layout**: Resueltos problemas donde los controles de crop aparecían en orden de capa incorrecto
  - **Eliminado zIndex Problemático**: Eliminados modificadores `zIndex` que causaban que el área de crop apareciera debajo de los controles del header
  - **Mejor Apilamiento de Componentes**: El flujo natural de layout ahora maneja correctamente la disposición en capas de componentes
  - **Mejor Soporte para Relación de Aspecto 9:16**: El rectángulo de crop ahora se ajusta correctamente dentro del espacio disponible del canvas para relaciones de aspecto verticales
- **📱 Corregidos Problemas de Superposición de Zoom**: Resuelto problema donde las imágenes con zoom aparecían sobre los controles del header de crop
  - **Agregado Recorte de Límites**: Implementado `clipToBounds()` para contener contenido con zoom dentro del área designada
  - **Mantenida Jerarquía de UI**: La funcionalidad de zoom ahora respeta los límites del layout y no interfiere con los controles del header
  - **Experiencia de Usuario Mejorada**: Los controles de crop permanecen accesibles y visibles durante las operaciones de zoom

## [1.0.22] - 2024-12-XX

### Añadido

- **Composables de Diálogos de Permisos Personalizados**: Nuevos parámetros `customDeniedDialog` y `customSettingsDialog` en `PermissionAndConfirmationConfig`
  - `customDeniedDialog`: Composable personalizado para cuando se deniega el permiso (permite reintentar)
  - `customSettingsDialog`: Composable personalizado para cuando el permiso es denegado permanentemente (abre configuración)
- **Control Completo de UI**: Personalización total de diálogos de permisos con tus propios composables
- **Soporte Multiplataforma**: Los diálogos de permisos personalizados funcionan en Android e iOS
- **Experiencia de Desarrollador Mejorada**: API fácil de usar para personalización de diálogos de permisos

### Cambiado

- Actualizada la clase de datos `PermissionAndConfirmationConfig` con nuevos parámetros opcionales
- Mejorado el flujo de manejo de permisos para soportar diálogos composables personalizados
- Mejorada la documentación con ejemplos completos de diálogos de permisos personalizados

### Corregido

- El flujo de diálogos de permisos ahora maneja correctamente composables personalizados en ambas plataformas

### Añadido

- Diálogos de permisos personalizados
- Vistas de confirmación personalizadas
- Opción de captura de fotos de alta calidad
- Mejoras en optimización de memoria
- Mejor manejo de errores y recuperación
- Soporte para selección de galería en Android e iOS
- Textos de diálogo personalizables para iOS y Android
- Mejoras de accesibilidad: contentDescription y tamaños de botones configurables
- Configuración de calidad de compresión JPEG para procesamiento de imágenes
- Interfaz Logger para logging configurable
- Test unitario para PhotoResult
- Configuración de linter y análisis estático (ktlint, detekt)
- Ejemplo y documentación para internacionalización
- Badges de CI y cobertura en README
- **Soporte para internacionalización (i18n)**: Sistema completo multi-idioma con recursos de strings type-safe
- **Detección automática de idioma**: Los strings se adaptan automáticamente al idioma del dispositivo
- **Soporte para inglés, español y francés**: Traducciones listas para usar para los tres idiomas
- **Sistema de idiomas extensible**: Fácil añadir nuevos idiomas sin dependencias externas
- **Traducciones automáticas**: Los diálogos de permisos y textos de UI ahora se traducen automáticamente por defecto
- **Corrección de orientación de cámara frontal**: Corrección automática de la orientación de imágenes de cámara frontal para arreglar fotos espejadas/rotadas
- Manejo automático de permisos de galería: `GalleryPickerLauncher` ahora gestiona los permisos de galería automáticamente en Android e iOS, sin necesidad de solicitud manual.
- Diálogo personalizado de permisos de galería (Android): Se añadió un diálogo específico y localizable para permisos de galería, separado del de cámara.
- Selección múltiple de imágenes en iOS: Implementada usando `PHPickerViewController` (iOS 14+), reemplazando el picker antiguo de una sola imagen.
- Nuevos tests instrumentados y unitarios: Se añadieron y reactivaron pruebas para cubrir los nuevos flujos de permisos y selección múltiple.
- Mejoras de localización: Nuevos textos y traducciones para los diálogos de permisos de galería.
- **Límite de selección configurable para el picker de galería**: Se añadió el parámetro `selectionLimit` a `GalleryConfig` para controlar el número máximo de imágenes que se pueden seleccionar en el picker de galería. El límite se aplica en tiempo de compilación con un valor máximo de 30 imágenes para prevenir problemas de rendimiento y crashes al seleccionar demasiadas imágenes en iOS.

### Cambiado
- Mejorado el flujo de manejo de permisos
- Componentes de UI mejorados
- Mejor compatibilidad multiplataforma
- La imagen de preview en confirmación ahora usa FIT_CENTER para evitar zoom en imágenes de galería
- Procesamiento de imágenes mejorado con corrección automática de orientación para fotos de cámara frontal
- Flujo de permisos en iOS: Ahora el permiso de galería se solicita directamente desde el sistema, sin diálogos previos personalizados, siguiendo el comportamiento nativo.
- Refactor de lógica de permisos: El manejo de permisos de galería y cámara ahora es más consistente y multiplataforma.
- **Límite de selección de galería**: Cambiado de selección ilimitada (0) a límite configurable con máximo de 30 imágenes para prevenir problemas de rendimiento y crashes en iOS al seleccionar demasiadas imágenes.

### Arreglado
- Flujo de denegación de permisos en iOS
- Problemas de inicialización de cámara en Android
- Memory leaks en procesamiento de fotos
- Problemas de visualización de diálogos de permisos
- Disparador (botón de captura) siempre centrado, botón de galería no lo empuja
- Fotos de cámara frontal apareciendo espejadas o incorrectamente orientadas
- Errores de texto en diálogos: El diálogo de galería ya no muestra textos de cámara.
- Bugs de selección múltiple: Corregidos errores de rendimiento y crashes al seleccionar muchas imágenes en iOS.
- Errores de threading y casting: Solucionados problemas de concurrencia y conversiones de tipos en la selección de imágenes.
- **Problemas de rendimiento en galería de iOS**: Corregidos crashes y degradación de rendimiento al seleccionar más de 30 imágenes implementando un límite de selección configurable con validación en tiempo de compilación.

### Documentación

- Actualización exhaustiva de todos los archivos de documentación en Markdown para asegurar que todos los ejemplos de uso de `ImagePickerLauncher`, `GalleryPickerLauncher` y componentes relacionados reflejen la API real y actual.
- Se reemplazaron todos los ejemplos obsoletos que usaban la API antigua (callbacks y handlers como parámetros sueltos) por el patrón correcto: toda la configuración y handlers ahora se muestran anidados dentro de `config = ImagePickerConfig(...)`, con handlers personalizados anidados según corresponda.
- Se garantizó que toda la documentación, tanto en inglés como en español, esté completamente sincronizada y sea precisa.
- Todos los ejemplos y guías actualizados para reflejar el nuevo manejo automático de permisos y la selección múltiple en iOS.
- Notas añadidas sobre diferencias de plataforma en el comportamiento de permisos y selección (Android vs iOS).
- **Documentación actualizada**: Se añadieron ejemplos y documentación para el nuevo parámetro `selectionLimit` en `GalleryConfig`, incluyendo casos de uso para límites de selección y optimización de rendimiento.

## [1.0.1] - 2024-01-15

### Añadido
- Lanzamiento inicial de ImagePickerKMP
- Integración de cámara multiplataforma
- Funcionalidad básica de captura de fotos
- Manejo de permisos para Android e iOS
- Componentes de UI simples
- Manejo de errores y excepciones
- Clase de datos PhotoResult
- Preferencias de captura (FAST, BALANCED, HIGH_QUALITY)

### Características
- **Soporte para Android**: Integración completa de cámara usando CameraX
- **Soporte para iOS**: Integración nativa de cámara usando AVFoundation
- **Gestión de Permisos**: Manejo inteligente de permisos para ambas plataformas
- **Captura de Fotos**: Captura de fotos de alta calidad con preview
- **Manejo de Errores**: Manejo completo de errores y feedback del usuario
- **Personalización**: Opciones básicas de personalización para UI y comportamiento

### Detalles Técnicos
- **SDK Mínimo**: Android API 21+, iOS 12.0+
- **Versión de Kotlin**: 1.8+
- **Compose Multiplatform**: Soporte completo
- **Dependencias**: Dependencias externas mínimas

## [0.9.0] - 2024-01-10

### Añadido
- Lanzamiento beta para testing
- Funcionalidad core de cámara
- Manejo básico de permisos
- Captura y preview de fotos
- Framework de manejo de errores

### Problemas Conocidos
- Flujo de permisos de iOS incompleto
- Optimización de memoria necesaria
- Opciones de personalización limitadas

## [0.8.0] - 2024-01-05

### Añadido
- Lanzamiento alpha
- Integración básica de cámara
- Manejo de solicitud de permisos
- Funcionalidad de captura de fotos

### Limitaciones
- Solo Android
- UI básica
- Manejo de errores limitado

## [0.7.0] - 2024-01-01

### Añadido
- Lanzamiento inicial de desarrollo
- Configuración de estructura del proyecto
- Funcionalidad básica de cámara
- Manejo de permisos

---

## Historial de Versiones

### Versión 1.0.1 (Estable Actual)
- **Fecha de Lanzamiento**: 15 de Enero, 2024
- **Estado**: Estable
- **Características Clave**:
  - Integración de cámara multiplataforma
  - Manejo inteligente de permisos
  - Captura de fotos de alta calidad
  - Manejo completo de errores
  - Componentes de UI personalizables

### Versión 0.9.0 (Beta)
- **Fecha de Lanzamiento**: 10 de Enero, 2024
- **Estado**: Beta
- **Características Clave**:
  - Funcionalidad core completa
  - Manejo básico de permisos
  - Captura y preview de fotos
  - Framework de manejo de errores

### Versión 0.8.0 (Alpha)
- **Fecha de Lanzamiento**: 5 de Enero, 2024
- **Estado**: Alpha
- **Características Clave**:
  - Integración básica de cámara
  - Manejo de solicitud de permisos
  - Funcionalidad de captura de fotos

### Versión 0.7.0 (Desarrollo)
- **Fecha de Lanzamiento**: 1 de Enero, 2024
- **Estado**: Desarrollo
- **Características Clave**:
  - Configuración de estructura del proyecto
  - Funcionalidad básica de cámara
  - Manejo de permisos

## Guía de Migración

### De 0.9.0 a 1.0.1

#### Cambios Rompedores
- API de manejo de permisos actualizada
- Estructura de manejo de errores cambiada
- Clase de datos PhotoResult modificada

#### Pasos de Migración
1. **Actualizar Dependencias**
   ```kotlin
   // Antiguo
   implementation("io.github.ismoy:imagepickerkmp:0.9.0")
   
   // Nuevo
   implementation("io.github.ismoy:imagepickerkmp:1.0.22")
   ```

2. **Actualizar Manejo de Permisos**
   ```kotlin
   // Antiguo
   RequestCameraPermission(
       onPermissionGranted = { /* ... */ },
       onPermissionDenied = { /* ... */ }
   )
   
   // Nuevo
   RequestCameraPermission(
       onPermissionPermanentlyDenied = { /* ... */ },
       onResult = { granted -> /* ... */ }
   )
   ```

3. **Actualizar Manejo de Errores**
   ```kotlin
   // Antiguo
   onError = { error ->
       // Manejar error genérico
   }
   
   // Nuevo
   onError = { exception ->
       when (exception) {
           is CameraPermissionException -> { /* ... */ }
           is PhotoCaptureException -> { /* ... */ }
           else -> { /* ... */ }
       }
   }
   ```

### De 0.8.0 a 0.9.0

#### Cambios Rompedores
- Añadido soporte para iOS
- Estructura de API actualizada
- Manejo de permisos cambiado

#### Pasos de Migración
1. **Actualizar Soporte de Plataforma**
   ```kotlin
   // Antiguo (solo Android)
   ImagePickerLauncher(
       context = LocalContext.current,
       // ...
   )
   
   // Nuevo (Multiplataforma)
   ImagePickerLauncher(
       context = LocalContext.current, // null para iOS
       // ...
   )
   ```

2. **Actualizar Manejo de Permisos**
   ```kotlin
   // Antiguo
   requestCameraPermission()
   
   // Nuevo
   RequestCameraPermission(
       onPermissionGranted = { /* ... */ },
       onPermissionDenied = { /* ... */ }
   )
   ```

## Política de Deprecación

### Características Deprecadas
- No hay características deprecadas en la versión actual

### Calendario de Eliminación
- Las características deprecadas serán eliminadas en la próxima versión mayor
- Los usuarios serán notificados 6 meses antes de la eliminación
- Se proporcionarán guías de migración

## Matriz de Compatibilidad

| Versión | Android API | Versión iOS | Versión Kotlin | Versión Compose |
|---------|-------------|-------------|----------------|-----------------|
| 1.0.1   | 21+         | 12.0+       | 1.8+           | 1.4+            |
| 0.9.0   | 21+         | 12.0+       | 1.8+           | 1.4+            |
| 0.8.0   | 21+         | N/A         | 1.8+           | 1.4+            |
| 0.7.0   | 21+         | N/A         | 1.8+           | 1.4+            |

## Problemas Conocidos

### Versión 1.0.1
- **Problema**: Uso de memoria alto con fotos grandes
  - **Estado**: Arreglado en la próxima versión
  - **Solución temporal**: Usar compresión de imagen

- **Problema**: Diálogo de permisos de iOS a veces no se muestra
  - **Estado**: Arreglado en la próxima versión
  - **Solución temporal**: Usar manejador de permisos personalizado

### Versión 0.9.0
- **Problema**: Inicialización de cámara lenta en algunos dispositivos
  - **Estado**: Arreglado en 1.0.1
  - **Solución temporal**: Usar preferencia de captura FAST

### Versión 0.8.0
- **Problema**: Manejo de permisos incompleto
  - **Estado**: Arreglado en 0.9.0
  - **Solución temporal**: Manejo manual de permisos

## Roadmap

### Versión 1.1.0 (Planificada)
- **Características**:
  
  - Temas de UI personalizados
  - Soporte para captura de video
  - Filtros y efectos de imagen
  - Captura de fotos en lote

### Versión 1.2.0 (Planificada)
- **Características**:
  - Integración de cámara AR
  - Filtros en tiempo real
  - Compartir en redes sociales
  - Integración con almacenamiento en la nube
  - Herramientas de edición avanzadas

### Versión 2.0.0 (Futuro)
- **Características**:
  - Rediseño completo de UI
  - Personalización avanzada
  - Sistema de plugins
  - Optimizaciones de rendimiento
  - Soporte de plataforma extendido

## Contribuir

### Cómo Contribuir
1. Haz fork del repositorio
2. Crea una rama de feature
3. Haz tus cambios
4. Añade tests
5. Envía un pull request

### Configuración de Desarrollo
```bash
# Clonar el repositorio
git clone https://github.com/ismoy/ImagePickerKMP.git

# Navegar al directorio del proyecto
cd ImagePickerKMP

# Construir el proyecto
./gradlew build

# Ejecutar tests
./gradlew test
```

### Proceso de Lanzamiento
1. **Bump de Versión**: Actualizar versión en `build.gradle.kts`
2. **Changelog**: Actualizar este changelog
3. **Tests**: Ejecutar todos los tests
4. **Documentación**: Actualizar documentación
5. **Lanzamiento**: Crear lanzamiento de GitHub
6. **Publicar**: Publicar en Maven Central

## Soporte

### Obtener Ayuda
- **Documentación**: [README.es.md](../README.es.md)
- **Issues**: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- **Discusiones**: [GitHub Discussions](https://github.com/ismoy/ImagePickerKMP/discussions)
- **Email**: belizairesmoy72@gmail.com

### Reportar Problemas
Al reportar problemas, por favor incluye:
- Número de versión
- Plataforma (Android/iOS)
- Información del dispositivo
- Pasos para reproducir
- Comportamiento esperado vs actual
- Logs (si aplica)

### Solicitudes de Características
Para solicitudes de características, por favor:
- Revisa issues existentes primero
- Proporciona descripción detallada
- Incluye ejemplos de casos de uso
- Considera la complejidad de implementación

---

**Nota**: Este changelog es mantenido por el equipo de ImagePickerKMP. Para preguntas o sugerencias, por favor contáctanos.