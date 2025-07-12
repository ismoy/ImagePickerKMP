This document is also available in English: [IOS_LOCALIZATION_GUIDE.md](IOS_LOCALIZATION_GUIDE.md)

# Guía de Localización para iOS - ImagePickerKMP

## Problema

En iOS, los archivos de recursos de la librería no se incluyen automáticamente en el bundle de la aplicación. Esto significa que aunque la librería tenga los archivos `Localizable.strings`, la app iOS no los verá y mostrará las claves en lugar de las traducciones.

## Solución

Para que las traducciones funcionen en iOS, debes añadir los archivos de localización a tu proyecto iOS.

### Paso 1: Crear los archivos de localización en tu app iOS

#### Opción A: Crear manualmente

1. **En Xcode, crea las carpetas de idiomas:**
   - Clic derecho en tu proyecto iOS
   - "New Group" → "en.lproj"
   - "New Group" → "es.lproj"
   - "New Group" → "fr.lproj"

2. **Crear los archivos Localizable.strings:**
   - Clic derecho en "en.lproj" → "New File" → "Strings File" → "Localizable.strings"
   - Clic derecho en "es.lproj" → "New File" → "Strings File" → "Localizable.strings"
   - Clic derecho en "fr.lproj" → "New File" → "Strings File" → "Localizable.strings"

#### Opción B: Copiar desde la librería

Puedes copiar los archivos desde la librería a tu proyecto iOS:

```bash
# Copiar archivos de la librería a tu app iOS
cp library/src/iosMain/resources/en.lproj/Localizable.strings iosApp/Resources/en.lproj/
cp library/src/iosMain/resources/es.lproj/Localizable.strings iosApp/Resources/es.lproj/
cp library/src/iosMain/resources/fr.lproj/Localizable.strings iosApp/Resources/fr.lproj/
```

### Paso 2: Contenido de los archivos

#### en.lproj/Localizable.strings (Inglés)

```plaintext
/* Camera Permissions */
"camera_permission_required" = "Camera permission required";
"camera_permission_description" = "Camera permission is required to capture photos. Please grant it in settings";
"open_settings" = "Open settings";
"camera_permission_denied" = "Camera permission denied";
"camera_permission_denied_description" = "Camera permission is required to capture photos. Please grant the permissions";
"grant_permission" = "Grant permission";
"camera_permission_permanently_denied" = "Camera permission permanently denied";

/* Image Confirmation */
"image_confirmation_title" = "Are you satisfied with the photo?";
"accept_button" = "Accept";
"retry_button" = "Retry";

/* Selection Dialogs */
"select_option_dialog_title" = "Select option";
"take_photo_option" = "Take photo";
"select_from_gallery_option" = "Select from gallery";
"cancel_option" = "Cancel";

/* Accessibility */
"preview_image_description" = "Preview";
"hd_quality_description" = "HD";
"sd_quality_description" = "SD";

/* Errors */
"invalid_context_error" = "Invalid context. Must be ComponentActivity";
"photo_capture_error" = "Photo capture failed";
"gallery_selection_error" = "Gallery selection failed";
"permission_error" = "Permission error occurred";
```

#### es.lproj/Localizable.strings (Español)

```plaintext
/* Permisos de cámara */
"camera_permission_required" = "Permiso de cámara requerido";
"camera_permission_description" = "El permiso de cámara es necesario para capturar fotos. Por favor, concédelo en la configuración";
"open_settings" = "Abrir configuración";
"camera_permission_denied" = "Permiso de cámara denegado";
"camera_permission_denied_description" = "El permiso de cámara es necesario para capturar fotos. Por favor, concede los permisos";
"grant_permission" = "Conceder permiso";
"camera_permission_permanently_denied" = "Permiso de cámara denegado permanentemente";

/* Confirmación de imagen */
"image_confirmation_title" = "¿Estás satisfecho con la foto?";
"accept_button" = "Aceptar";
"retry_button" = "Reintentar";

/* Diálogos de selección */
"select_option_dialog_title" = "Seleccionar opción";
"take_photo_option" = "Tomar foto";
"select_from_gallery_option" = "Seleccionar de galería";
"cancel_option" = "Cancelar";

/* Accesibilidad */
"preview_image_description" = "Vista previa";
"hd_quality_description" = "HD";
"sd_quality_description" = "SD";

/* Errores */
"invalid_context_error" = "Contexto inválido. Debe ser ComponentActivity";
"photo_capture_error" = "Error al capturar la foto";
"gallery_selection_error" = "Error al seleccionar de la galería";
"permission_error" = "Error de permisos";
```

#### fr.lproj/Localizable.strings (Francés)

```plaintext
/* Permissions caméra */
"camera_permission_required" = "Permission d'appareil photo requise";
"camera_permission_description" = "Cette application a besoin d'accéder à votre appareil photo pour prendre des photos";
"open_settings" = "Ouvrir les paramètres";
"camera_permission_denied" = "Permission d'appareil photo refusée";
"camera_permission_denied_description" = "Pour utiliser cette fonctionnalité, vous devez autoriser l'accès à l'appareil photo dans les paramètres";
"grant_permission" = "Accorder la permission";
"camera_permission_permanently_denied" = "Permission d'appareil photo définitivement refusée";

/* Confirmation d'image */
"image_confirmation_title" = "Confirmer l'image";
"accept_button" = "Accepter";
"retry_button" = "Réessayer";

/* Dialogues de sélection */
"select_option_dialog_title" = "Sélectionner une option";
"take_photo_option" = "Prendre une photo";
"select_from_gallery_option" = "Sélectionner depuis la galerie";
"cancel_option" = "Annuler";

/* Accessibilité */
"preview_image_description" = "Aperçu de l'image capturée";
"hd_quality_description" = "Qualité haute définition";
"sd_quality_description" = "Qualité standard";

/* Erreurs */
"invalid_context_error" = "Contexte invalide";
"photo_capture_error" = "Erreur lors de la capture de la photo";
"gallery_selection_error" = "Erreur lors de la sélection depuis la galerie";
"permission_error" = "Erreur de permission";
```

### Paso 3: Verificar en Xcode

1. **Selecciona cada archivo Localizable.strings**
2. **En el inspector de la derecha, asegúrate de que:**
   - Está marcado para tu target de app
   - El tipo es "Resource"
   - Está incluido en el bundle

### Paso 4: Limpiar y recompilar

1. **Clean Build Folder:** `Shift + Cmd + K`
2. **Recompilar:** `Cmd + B`

### Paso 5: Probar

1. **Cambia el idioma del simulador/dispositivo**
2. **Ejecuta la app**
3. **Verifica que los textos aparecen traducidos**

## Añadir más idiomas

Para añadir alemán, por ejemplo:

1. **Crear carpeta:** `de.lproj`
2. **Crear archivo:** `de.lproj/Localizable.strings`
3. **Añadir traducciones:**

```plaintext
"take_photo_option" = "Foto aufnehmen";
"select_from_gallery_option" = "Aus Galerie auswählen";
"cancel_option" = "Abbrechen";
```

## Fallback automático

Si no encuentras las traducciones, la librería usará automáticamente los textos en inglés como fallback. Esto significa que:

- Si el dispositivo está en español pero no tienes el archivo `es.lproj/Localizable.strings`, verás los textos en inglés
- Si el dispositivo está en francés pero no tienes el archivo `fr.lproj/Localizable.strings`, verás los textos en inglés

## Verificación

Para verificar que funciona:

1. **Cambia el idioma del dispositivo a español**
2. **Ejecuta la app**
3. **Deberías ver:**
   - "Tomar foto" en lugar de "Take photo" (español)
   - "Prendre une photo" en lugar de "Take photo" (francés)
   - "Seleccionar de galería" en lugar de "Select from gallery" (español)
   - "Sélectionner depuis la galerie" en lugar de "Select from gallery" (francés)
   - "Cancelar" en lugar de "Cancel" (español)
   - "Annuler" en lugar de "Cancel" (francés)

## Solución de Problemas

### Si sigues viendo las claves

1. **Verifica que los archivos están en la ruta correcta:**
   ```
   iosApp/Resources/en.lproj/Localizable.strings
   iosApp/Resources/es.lproj/Localizable.strings
   ```

2. **Verifica que están incluidos en el target:**
   - Selecciona el archivo en Xcode
   - En el inspector, asegúrate de que está marcado para tu app

3. **Limpia y recompila:**
   - `Shift + Cmd + K` (Clean)
   - `Cmd + B` (Build)

4. **Verifica el idioma del dispositivo:**
   - Asegúrate de que el dispositivo está en el idioma correcto
   - Para francés: Configuración → General → Idioma y región → Francés

### Si necesitas debuggear

Puedes añadir logs temporales para verificar:

```kotlin
val bundle = NSBundle.mainBundle
val value = bundle.localizedStringForKey("take_photo_option", value = null, table = null)
println("Traducción para 'take_photo_option': $value")
```

Si imprime la clave, el problema es de recursos. Si imprime la traducción, el sistema funciona. 