# Documentación de ImageProcessor

## Descripción General

`ImageProcessor` es una clase utilitaria multiplataforma responsable de todas las operaciones de procesamiento de imágenes dentro de la librería. Se encarga de que las imágenes capturadas desde la cámara o seleccionadas desde la galería tengan la orientación, tamaño y formato correctos para su uso o visualización posterior. Este componente abstrae los detalles específicos de cada plataforma y ofrece una API unificada para la manipulación de imágenes.

## Responsabilidades Principales

- **Corrección de Orientación:** Garantiza que las imágenes se muestren con la orientación correcta, usando datos EXIF o metadatos de la plataforma.
- **Redimensionamiento:** Ajusta las dimensiones de la imagen para cumplir requisitos o mejorar el rendimiento.
- **Conversión de Formato:** Convierte imágenes entre diferentes formatos (por ejemplo, JPEG, PNG) según sea necesario.
- **Recorte (si está implementado):** Permite recortar imágenes a una relación de aspecto o región específica.
- **Manejo de Errores:** Gestiona formatos no soportados, imágenes corruptas o fallos de procesamiento de forma segura.

## Métodos Típicos

| Método | Descripción |
|--------|-------------|
| `processImage(input: Image): Image` | Procesa la imagen de entrada y devuelve una versión procesada (orientación, tamaño, etc.). |
| `correctImageOrientation(image: Image, orientation: Int): Image` | Ajusta la orientación de la imagen según datos EXIF o de la plataforma. |
| `resizeImage(image: Image, width: Int, height: Int): Image` | Redimensiona la imagen a las dimensiones especificadas. |
| `convertFormat(image: Image, format: String): Image` | Convierte la imagen al formato especificado. |

## Ejemplo de Uso

```kotlin
val procesada = ImageProcessor.processImage(imagenOriginal)
val rotada = ImageProcessor.correctImageOrientation(procesada, orientacion)
val redimensionada = ImageProcessor.resizeImage(rotada, 1024, 768)
```

## Manejo de Errores

- Lanza excepciones o devuelve resultados de error si la imagen no puede ser procesada.
- Gestiona formatos no soportados e imágenes corruptas de forma segura.

## Notas de Plataforma

- **Android:** Utiliza `Bitmap`, utilidades EXIF y APIs de la plataforma para manipulación de imágenes.
- **iOS:** Utiliza `UIImage`, CoreGraphics y APIs nativas para el procesamiento.

## Buenas Prácticas

1. Siempre maneja las excepciones de los métodos de procesamiento de imágenes.
2. Valida las imágenes de entrada antes de procesarlas.
3. Usa formatos de imagen apropiados para tu caso de uso (por ejemplo, JPEG para fotos, PNG para transparencia).
4. Prueba en varios dispositivos y plataformas para asegurar resultados consistentes. 