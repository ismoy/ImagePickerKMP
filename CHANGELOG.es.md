This document is also available in English: [CHANGELOG.md](CHANGELOG.md)

# Registro de Cambios

Todos los cambios notables en ImagePickerKMP serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto sigue [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Sin Publicar]

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

### Cambiado
- Mejorado el flujo de manejo de permisos
- Componentes de UI mejorados
- Mejor compatibilidad multiplataforma
- La imagen de preview en confirmación ahora usa FIT_CENTER para evitar zoom en imágenes de galería
- Procesamiento de imágenes mejorado con corrección automática de orientación para fotos de cámara frontal

### Arreglado
- Flujo de denegación de permisos en iOS
- Problemas de inicialización de cámara en Android
- Memory leaks en procesamiento de fotos
- Problemas de visualización de diálogos de permisos
- Disparador (botón de captura) siempre centrado, botón de galería no lo empuja
- Fotos de cámara frontal apareciendo espejadas o incorrectamente orientadas

## [1.0.0] - 2024-01-15

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

### Versión 1.0.0 (Estable Actual)
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

### De 0.9.0 a 1.0.0

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
   implementation("io.github.ismoy:imagepickerkmp:1.0.0")
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
| 1.0.0   | 21+         | 12.0+       | 1.8+           | 1.4+            |
| 0.9.0   | 21+         | 12.0+       | 1.8+           | 1.4+            |
| 0.8.0   | 21+         | N/A         | 1.8+           | 1.4+            |
| 0.7.0   | 21+         | N/A         | 1.8+           | 1.4+            |

## Problemas Conocidos

### Versión 1.0.0
- **Problema**: Uso de memoria alto con fotos grandes
  - **Estado**: Arreglado en la próxima versión
  - **Solución temporal**: Usar compresión de imagen

- **Problema**: Diálogo de permisos de iOS a veces no se muestra
  - **Estado**: Arreglado en la próxima versión
  - **Solución temporal**: Usar manejador de permisos personalizado

### Versión 0.9.0
- **Problema**: Inicialización de cámara lenta en algunos dispositivos
  - **Estado**: Arreglado en 1.0.0
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
- **Documentación**: [README.es.md](README.es.md)
- **Issues**: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- **Discusiones**: [GitHub Discussions](https://github.com/ismoy/ImagePickerKMP/discussions)
- **Email**: support@imagepickerkmp.com

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