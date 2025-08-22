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
   ./gradlew build test androidTest iosTest
   ./gradlew detekt   # ejecutar análisis estático
   ```
3. **Estilo de Código**  
   - Sigue las [convenciones de Kotlin](https://kotlinlang.org/docs/coding-conventions.html).  
   - Indentación de 4 espacios, máximo 120 caracteres por línea, trailing commas.  
   - Usa `expect/actual` para código específico de plataforma.  

4. **Commit & PR**  
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

 **En resumen**: Fork → Nueva Rama → Código + Tests → Corre `./gradlew detekt` → Commit → PR 
