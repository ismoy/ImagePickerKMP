# ImagePickerKMP Core Module

This is the core module for the `ImagePickerKMP` library. It contains the essential cross-platform abstractions: permissions, filesystem, URI handling, and shared logging interfaces.

## <img src="https://raw.githubusercontent.com/primer/octicons/main/icons/globe-24.svg" width="24" height="24" align="center" /> Automatic Translations (i18nKonfig)

`ImagePickerKMP` includes built-in support for multiple languages out of the box. Translations are modularized per feature module (`imagepickerkmp-photo`, `imagepickerkmp-video`, `imagepickerkmp-audio`, `imagepickerkmp-scanner`) using the `i18nKonfig` Gradle plugin created by **Ismoy Belizaire**.

When you use the UI components provided by each module, they automatically detect the user's device language and display the correct localized string without extra setup.

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
- **pl** - Polish
- **nl** - Dutch

## <img src="https://raw.githubusercontent.com/primer/octicons/main/icons/git-pull-request-24.svg" width="24" height="24" align="center" /> How to Add a New Language

We welcome contributions to expand our language support! If your language is not on the list, you can easily add it by following these steps:

1. **Fork the project**: Create a fork of this repository on GitHub.
2. **Locate the module's translation file**: Navigate to the feature module you want to translate and find its translation file at:
   - `imagepickerkmp-photo/src/commonMain/resources/translations.yaml`
   - `imagepickerkmp-video/src/commonMain/resources/translations.yaml`
   - `imagepickerkmp-audio/src/commonMain/resources/translations.yaml`
   - `imagepickerkmp-scanner/src/commonMain/resources/translations.yaml`
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
