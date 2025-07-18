This document is also available in English: [CONTRIBUTING.md](docs/CONTRIBUTING.md)

# Guía de Contribución

// TODO: Traducir el contenido detallado

Consulta la versión en inglés para la guía completa de contribución. 

# Contribuir a ImagePickerKMP

¡Gracias por tu interés en contribuir a ImagePickerKMP! Este documento proporciona guías e información para los contribuyentes.

## Tabla de Contenidos

- [Comenzando](#comenzando)
- [Configuración de Desarrollo](#configuración-de-desarrollo)
- [Estilo de Código](#estilo-de-código)
- [Testing](#testing)
- [Proceso de Pull Request](#proceso-de-pull-request)
- [Reportar Issues](#reportar-issues)
- [Solicitudes de Características](#solicitudes-de-características)
- [Código de Conducta](#código-de-conducta)

## Comenzando

### Prerrequisitos

Antes de contribuir, asegúrate de tener:

- **Kotlin 1.8+** instalado
- **Android Studio** o **IntelliJ IDEA** con plugin de Kotlin
- **Xcode** (para desarrollo iOS)
- **Git** para control de versiones
- **Gradle** para gestión de builds

### Herramientas Requeridas

- **Android SDK**: API 21+ para desarrollo Android
- **Xcode**: Última versión para desarrollo iOS
- **Simuladores/Emuladores**: Para testing en ambas plataformas

## Configuración de Desarrollo

### 1. Fork y Clone

```bash
# Haz fork del repositorio en GitHub
# Luego clona tu fork
git clone https://github.com/ismoy/ImagePickerKMP.git
cd ImagePickerKMP
```

### 2. Configurar Entorno de Desarrollo

```bash
# Añade el repositorio original como upstream
git remote add upstream https://github.com/ismoy/ImagePickerKMP.git

# Crea una rama de desarrollo
git checkout -b development

# Construye el proyecto
./gradlew build
```

### 3. Verificar Configuración

```bash
# Ejecuta tests
./gradlew test

# Ejecuta tests de Android
./gradlew androidTest

# Ejecuta tests de iOS (requiere macOS)
./gradlew iosTest
```

### 4. Configuración del IDE

#### Android Studio / IntelliJ IDEA

1. **Importar Proyecto**: Abre el proyecto en Android Studio
2. **Configuración SDK**: Asegúrate de que Android SDK esté correctamente configurado
3. **Plugin Kotlin**: Verifica que el plugin de Kotlin esté instalado y habilitado
4. **Sincronización Gradle**: Sincroniza el proyecto con Gradle

#### Xcode (para desarrollo iOS)

1. **Abrir Proyecto**: Abre el proyecto iOS en Xcode
2. **Configuración Framework**: Asegúrate de que el framework de Kotlin esté correctamente enlazado
3. **Simulador**: Configura el simulador de iOS para testing

## Estilo de Código

### Guía de Estilo de Kotlin

Seguimos las [Convenciones de Código de Kotlin](https://kotlinlang.org/docs/coding-conventions.html).

#### Convenciones de Nomenclatura

```kotlin
// Clases y objetos
class ImagePickerLauncher
object Constants

// Funciones y variables
fun capturePhoto()
var isCapturing: Boolean = false

// Constantes
const val CAMERA_PERMISSION = "android.permission.CAMERA"

// Enums
enum class CapturePhotoPreference {
    FAST,
    BALANCED,
    HIGH_QUALITY
}
```

#### Formato de Código

```kotlin
// Usa 4 espacios para indentación
@Composable
fun MyComponent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Hello World")
    }
}

// Longitud de línea: máximo 120 caracteres
// Usa comas finales para mejores diffs de git
val longList = listOf(
    "item1",
    "item2",
    "item3",
)
```

#### Documentación

```kotlin
/**
 * Captura una foto usando la cámara del dispositivo.
 *
 * @param context El contexto para operaciones de cámara
 * @param onPhotoCaptured Callback cuando la foto es capturada
 * @param onError Callback cuando ocurre un error
 * @param preference Preferencia de calidad de captura de foto
 */
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    preference: CapturePhotoPreference = CapturePhotoPreference.BALANCED
) {
    // Implementación
}
```

### Código Específico de Plataforma

#### Android

```kotlin
// Usa expect/actual para código específico de plataforma
expect fun requestCameraPermission(context: Context): Boolean

// Implementación de Android
actual fun requestCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
```

#### iOS

```kotlin
// Implementación de iOS
actual fun requestCameraPermission(context: Context): Boolean {
    return AVCaptureDevice.authorizationStatus(for: .video) == .authorized
}
```

### Análisis Estático

Antes de enviar un pull request, ejecuta el análisis estático para asegurarte de que tu código cumple con los estándares de calidad del proyecto:

```sh
./gradlew detekt
```

Corrige cualquier problema reportado por Detekt antes de enviar tu PR.

## Testing

### Escribir Tests

#### Tests Unitarios

```kotlin
class ImagePickerTest {
    @Test
    fun `should capture photo successfully`() {
        // Given
        val mockContext = mock<Context>()
        val mockResult = PhotoResult(
            uri = Uri.parse("content://photo"),
            size = 1024L,
            format = "JPEG",
            width = 1920,
            height = 1080
        )
        
        // When
        val result = capturePhoto(mockContext)
        
        // Then
        assertEquals(mockResult, result)
    }
}
```

#### Tests de Integración

```kotlin
@RunWith(AndroidJUnit4::class)
class ImagePickerIntegrationTest {
    @Test
    fun testPhotoCaptureFlow() {
        // Test del flujo completo de captura de fotos
        // Incluyendo manejo de permisos e interacciones de UI
    }
}
```

#### Tests de Plataforma

```kotlin
// Tests de Android
@RunWith(AndroidJUnit4::class)
class AndroidImagePickerTest {
    @Test
    fun testAndroidCameraIntegration() {
        // Test de funcionalidad específica de Android
    }
}

// Tests de iOS
class IOSImagePickerTest {
    @Test
    fun testIOSCameraIntegration() {
        // Test de funcionalidad específica de iOS
    }
}
```

### Ejecutar Tests

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar test específico
./gradlew test --tests ImagePickerTest

# Ejecutar con cobertura
./gradlew test jacocoTestReport

# Ejecutar tests de Android
./gradlew androidTest

# Ejecutar tests de iOS
./gradlew iosTest
```

### Cobertura de Tests

Apuntamos a al menos 80% de cobertura de tests. Enfócate en:

- **Funcionalidad core**: Captura de fotos, manejo de permisos
- **Escenarios de error**: Permisos denegados, cámara no disponible
- **Diferencias de plataforma**: Comportamiento Android vs iOS
- **Interacciones de UI**: Interacciones de usuario y callbacks

## Proceso de Pull Request

### 1. Crear una Rama de Feature

```bash
# Crea una nueva rama para tu feature
git checkout -b feature/tu-nombre-de-feature

# O para arreglos de bugs
git checkout -b fix/tu-descripcion-de-bug
```

### 2. Haz tus Cambios

- **Escribe código**: Implementa tu feature o arreglo
- **Añade tests**: Incluye tests unitarios y de integración
- **Actualiza documentación**: Actualiza documentación relevante
- **Sigue la guía de estilo**: Asegúrate de que el código siga nuestras convenciones

### 3. Commit de tus Cambios

```bash
# Usa mensajes de commit convencionales
git commit -m "feat: add custom permission dialog support"
git commit -m "fix: resolve iOS permission denial issue"
git commit -m "docs: update API documentation"
```

### 4. Push y Crear PR

```bash
# Push de tu rama
git push origin feature/tu-nombre-de-feature

# Crear pull request en GitHub
# Incluye descripción detallada de los cambios
```

### 5. Guías de PR

#### Información Requerida

- **Título**: Título claro y descriptivo
- **Descripción**: Descripción detallada de los cambios
- **Tipo**: Feature, bug fix, documentación, etc.
- **Testing**: Cómo probaste tus cambios
- **Cambios Rompedores**: Cualquier cambio rompedor
- **Issues Relacionados**: Enlace a issues relacionados

#### Ejemplo de Descripción de PR

```markdown
## Descripción
Añade soporte para diálogos de permisos personalizados en ImagePickerKMP.

## Cambios
- Añadido composable `CustomPermissionDialog`
- Actualizado `ImagePickerLauncher` para soportar diálogos personalizados
- Añadida documentación y ejemplos

## Testing
- Añadidos tests unitarios para funcionalidad de diálogo personalizado
- Probado en simuladores de Android e iOS
- Verificada compatibilidad hacia atrás

## Cambios Rompedores
Ninguno - esta es una adición compatible hacia atrás

## Issues Relacionados
Cierra #123
```

### 6. Proceso de Revisión

1. **Checks Automatizados**: Pipeline de CI/CD ejecuta tests
2. **Revisión de Código**: Los maintainers revisan tu código
3. **Abordar Feedback**: Haz los cambios solicitados
4. **Merge**: Una vez aprobado, el PR se hace merge

## Reportar Issues

### Reportes de Bugs

Al reportar bugs, incluye:

```markdown
## Descripción del Bug
Descripción breve del bug

## Pasos para Reproducir
1. Paso 1
2. Paso 2
3. Paso 3

## Comportamiento Esperado
Lo que esperabas que pasara

## Comportamiento Actual
Lo que realmente pasó

## Entorno
- Plataforma: Android/iOS
- Versión: 1.0.0
- Dispositivo: Pixel 6 / iPhone 13
- Versión OS: Android 12 / iOS 15

## Información Adicional
- Screenshots (si aplica)
- Logs (si aplica)
- Ejemplos de código (si aplica)
```

### Solicitudes de Características

Para solicitudes de características, incluye:

```markdown
## Descripción de la Característica
Descripción breve de la característica

## Caso de Uso
Por qué se necesita esta característica

## Implementación Propuesta
Cómo crees que debería implementarse

## Alternativas Consideradas
Otros enfoques que consideraste

## Información Adicional
Cualquier otra información relevante
```

## Solicitudes de Características

### Guías

- **Revisa issues existentes**: Busca solicitudes similares
- **Sé específico**: Proporciona requisitos detallados
- **Incluye ejemplos**: Muestra cómo se usaría la característica
- **Considera el impacto**: Piensa en cambios rompedores
- **Proporciona contexto**: Explica el caso de uso

### Plantilla de Solicitud de Característica

```markdown
## Solicitud de Característica: [Nombre de la Característica]

### Declaración del Problema
Describe el problema que esta característica resolvería

### Solución Propuesta
Describe tu solución propuesta

### Casos de Uso
- Caso de uso 1
- Caso de uso 2
- Caso de uso 3

### Detalles de Implementación
Detalles técnicos sobre la implementación

### Alternativas
Otros enfoques que podrían considerarse

### Contexto Adicional
Cualquier otra información relevante
```

## Código de Conducta

### Nuestros Estándares

Estamos comprometidos a proporcionar una comunidad acogedora e inspiradora para todos. Esperamos que todos los contribuyentes:

- **Sean respetuosos**: Traten a otros con respeto
- **Sean colaborativos**: Trabajen juntos constructivamente
- **Sean constructivos**: Proporcionen feedback útil
- **Sean inclusivos**: Acojan perspectivas diversas
- **Sean profesionales**: Mantengan comportamiento profesional

### Comportamiento Inaceptable

- **Acoso**: Cualquier forma de acoso o discriminación
- **Trolling**: Comportamiento deliberadamente provocativo
- **Spam**: Contenido promocional no deseado
- **Contenido inapropiado**: Material ofensivo o inapropiado

### Aplicación

- **Advertencia**: Primera ofensa resulta en una advertencia
- **Ban temporal**: Violaciones repetidas pueden resultar en ban temporal
- **Ban permanente**: Violaciones severas pueden resultar en ban permanente

### Reportar

Si experimentas o presencias comportamiento inaceptable:

1. **Contacta maintainers**: Contacta a los maintainers del proyecto
2. **Proporciona detalles**: Incluye detalles específicos sobre el incidente
3. **Confidencialidad**: Los reportes se manejarán confidencialmente
4. **Acción**: Se tomará acción apropiada

## Flujo de Desarrollo

### Estrategia de Ramas

```bash
main                    # Código listo para producción
├── develop            # Rama de desarrollo
├── feature/*          # Ramas de features
├── fix/*              # Ramas de arreglos de bugs
├── hotfix/*           # Ramas de hotfix
└── release/*          # Ramas de lanzamiento
```

### Proceso de Lanzamiento

1. **Desarrollo de Features**: Desarrolla features en ramas de feature
2. **Integración**: Merge de features en rama develop
3. **Testing**: Test exhaustivo en rama develop
4. **Lanzamiento**: Crea rama de lanzamiento desde develop
5. **Producción**: Merge de lanzamiento en main
6. **Tagging**: Tag de lanzamientos con números de versión

### Gestión de Versiones

Usamos [Semantic Versioning](https://semver.org/):

- **Major**: Cambios rompedores
- **Minor**: Nuevas características (compatible hacia atrás)
- **Patch**: Arreglos de bugs (compatible hacia atrás)

## Obtener Ayuda

### Recursos

- **Documentación**: [README.es.md](docs/../README.es.md)
- **Referencia de API**: [API_REFERENCE.es.md](docs/API_REFERENCE.es.md)
- **Ejemplos**: [EXAMPLES.es.md](docs/EXAMPLES.es.md)
- **Issues**: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- **Discusiones**: [GitHub Discussions](https://github.com/ismoy/ImagePickerKMP/discussions)

### Comunicación

- **GitHub Issues**: Para bugs y solicitudes de características
- **GitHub Discussions**: Para preguntas y discusión general
- **Email**: belizairesmoy72@gmail.com
- **Discord**: [Comunidad Discord](https://discord.com/channels/1393705692484993114/1393706133864190133)

### Mentoría

Los nuevos contribuyentes pueden:

- **Hacer preguntas**: No dudes en pedir ayuda
- **Solicitar reviews**: Pide reviews de código a los maintainers
- **Unirse a discusiones**: Participa en discusiones comunitarias
- **Comenzar pequeño**: Empieza con documentación o arreglos pequeños

## Reconocimiento

### Contribuyentes

Reconocemos a los contribuyentes de varias maneras:

- **Lista de contribuyentes**: Añadidos a contribuyentes del proyecto
- **Notas de lanzamiento**: Mencionados en notas de lanzamiento
- **Documentación**: Crédito en documentación
- **Comunidad**: Reconocimiento en discusiones comunitarias

### Salón de la Fama

Los mejores contribuyentes aparecen en nuestro Salón de la Fama:

- **Contribuyentes Oro**: 50+ contribuciones
- **Contribuyentes Plata**: 20+ contribuciones
- **Contribuyentes Bronce**: 10+ contribuciones

---

**¡Gracias por contribuir a ImagePickerKMP!** Tus contribuciones ayudan a hacer este proyecto mejor para todos. 