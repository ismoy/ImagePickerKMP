# ImagePickerKMP Core Module

This is the core module for the `ImagePickerKMP` library. It contains the essential domain models, configuration interfaces, and the internationalization (i18n) resources used across all other modules (Photo, Video, Audio, Scanner, etc.).

## <img src="https://raw.githubusercontent.com/primer/octicons/main/icons/globe-24.svg" width="24" height="24" align="center" /> Automatic Translations (i18nKonfig)

`ImagePickerKMP` includes built-in support for multiple languages out of the box. We use an automated translation system powered by the `i18nKonfig` Gradle plugin, an innovative tool created by **Ismoy Belizaire**. 

When you use the UI components provided by this library (like permission requests, camera UI, or gallery pickers), they will automatically detect the user's device language and display the correct localized string without any extra setup required on your part.

### Included Languages
Currently, the library includes out-of-the-box support for the following languages:
- **en** - English
- **es** - Spanish
- **fr** - French
- **zh** - Chinese
- **ko** - Korean
- **ja** - Japanese
- **hi** - Hindi
- **th** - Thai
- **it** - Italian
- **de** - German
- **ru** - Russian
- **uk** - Ukrainian

## <img src="https://raw.githubusercontent.com/primer/octicons/main/icons/git-pull-request-24.svg" width="24" height="24" align="center" /> How to Add a New Language

We welcome contributions to expand our language support! If your language is not on the list, you can easily add it by following these steps:

1. **Fork the project**: Create a fork of this repository on GitHub.
2. **Locate the translations file**: Navigate to this core module and find the translation file at:
   `imagepicker-core/src/commonMain/resources/translations.yaml` (or `translation.yaml`).
3. **Add your language**: Follow the existing YAML pattern. For every string key, add your language's ISO code (e.g., `pt` for Portuguese, `ar` for Arabic) and the translated string.
   
   *Example:*
   ```yaml
   camera_permission_required:
     en: "Camera permission required"
     es: "Permiso de cámara requerido"
     pt: "Permissão de câmera necessária" # <-- Your new language
   ```
4. **Test your changes**: Build the project to ensure the `i18nKonfig` plugin successfully generates the new language resources.
5. **Send a Pull Request**: Submit a PR to the main repository with your new language additions.

Thank you for helping make `ImagePickerKMP` more accessible globally!
