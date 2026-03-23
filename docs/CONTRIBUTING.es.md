#  Contribuir (Guía Rápida)

¡Damos la bienvenida a tus aportes en **ImagePickerKMP**!  

##  Pasos para Contribuir
1. **Fork & Clonar**  
   ```bash
   git clone https://github.com/your-username/ImagePickerKMP.git
   cd ImagePickerKMP
   git checkout -b feature/tu-feature
   ```
2. **Compilar & Probar**  
   ```bash
   # Compilar el proyecto
   ./gradlew build

   # Ejecutar tests unitarios (JVM — rápido, sin emulador)
   ./gradlew :library:jvmTest

   # Ejecutar tests unitarios de Android
   ./gradlew :library:testDebugUnitTest

   # Ejecutar todos los tests
   ./gradlew :library:allTests

   # Ejecutar análisis estático
   ./gradlew detekt
   ```

3. **Verificar Cobertura de Tests**

   Genera el reporte de cobertura y verifica que supere el umbral mínimo (97%):
   ```bash
   # Generar reporte HTML + XML y verificar el umbral del 97%
   ./gradlew :library:koverVerify

   # Generar solo el reporte HTML (abrir en el navegador)
   ./gradlew :library:koverHtmlReport

   # Generar solo el reporte XML (usado por Codecov en CI)
   ./gradlew :library:koverXmlReport
   ```

   Después de ejecutar `koverHtmlReport`, abre el reporte en el navegador:
   ```bash
   open library/build/reports/kover/html/index.html
   ```

   > ⚠️ **Cobertura mínima requerida: 97% de líneas cubiertas.**  
   > Todo PR debe pasar `koverVerify` sin errores. Si agregas código nuevo, agrega los tests correspondientes para mantener la cobertura por encima del umbral.
   - Sigue las [convenciones de Kotlin](https://kotlinlang.org/docs/coding-conventions.html).  
   - Indentación de 4 espacios, máximo 120 caracteres por línea, trailing commas.  
   - Usa `expect/actual` para código específico de plataforma.  

4. **Estilo de Código**  
   - Sigue las [convenciones de Kotlin](https://kotlinlang.org/docs/coding-conventions.html).  
   - Indentación de 4 espacios, máximo 120 caracteres por línea, trailing commas.  
   - Usa `expect/actual` para código específico de plataforma.  

5. **Commit & PR**  
   ```bash
   git commit -m "feat: agregar soporte para dialogo de permisos"
   git push origin feature/tu-feature
   ```
   - Abre un PR con descripción, pruebas e issues relacionados.  

##  Issues & Features
- **Bugs**: incluye pasos de reproducción, comportamiento esperado vs real, info del dispositivo/SO.  
- **Features**: explica el caso de uso, propuesta y alternativas.  

##  Flujo de Ramas
- `main` → producción  
- `develop` → desarrollo  
- `feature/*`, `fix/*`, `release/*`, `hotfix/*`  

##  Ayuda
- [Issues](https://github.com/ismoy/ImagePickerKMP/issues) • [Discussions](https://github.com/ismoy/ImagePickerKMP/discussions) • Discord  

---

 **En resumen**: Fork → Nueva Rama → Código + Tests → `./gradlew :library:koverVerify` (≥97%) → `./gradlew detekt` → Commit → PR
