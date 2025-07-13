# Guía de Cobertura

Esta guía explica cómo funciona la cobertura de código en el proyecto ImagePickerKMP y cómo gestionar los umbrales de cobertura.

## Estado Actual de Cobertura

- **Cobertura de Líneas**: 4% (umbral mínimo: 4%)
- **Cobertura de Ramas**: 0% (umbral mínimo: 0%)

## Umbrales de Cobertura

El proyecto usa JaCoCo para aplicar umbrales mínimos de cobertura:

- **Cobertura de Líneas**: Mínimo 4% (configurable en `library/build.gradle.kts`)
- **Cobertura de Ramas**: Mínimo 0% (configurable en `library/build.gradle.kts`)

## Cómo Aumentar la Cobertura

### 1. Agregar Más Tests Unitarios

Enfócate en probar las clases de lógica principal:

```kotlin
// Ejemplo: Agregar tests para ImageProcessor
@Test
fun `test image processing with different formats`() {
    // Probar lógica de procesamiento de imágenes
}
```

### 2. Probar Componentes UI (Cuando Esté Listo)

Una vez que el testing de Compose esté configurado correctamente, agrega tests para componentes UI:

```kotlin
@Test
fun `test image picker launcher composable`() {
    // Probar componentes UI
}
```

### 3. Probar Código Específico de Plataforma

Agrega tests para implementaciones específicas de Android e iOS.

## Comandos de Cobertura

### Generar Reporte de Cobertura
```bash
./gradlew jacocoTestReport
```

### Verificar Umbrales de Cobertura
```bash
./gradlew jacocoTestCoverageVerification
```

### Ver Reporte HTML
Abre `library/build/reports/jacoco/jacocoTestReport/html/index.html` en tu navegador.

## Ajustar Umbrales

Para aumentar los umbrales de cobertura:

1. **Aumenta gradualmente los umbrales** en `library/build.gradle.kts`:
   ```kotlin
   minimum = "0.10".toBigDecimal() // 10% cobertura de líneas
   minimum = "0.05".toBigDecimal() // 5% cobertura de ramas
   ```

2. **Agrega tests** para cumplir con los nuevos umbrales.

3. **Repite** hasta alcanzar tu cobertura objetivo (ej: 80% líneas, 70% ramas).

## Integración con CI

- La cobertura se verifica automáticamente en CI
- Los reportes se suben a Codecov
- Los comentarios en PR muestran resumen de cobertura
- Los badges de cobertura se actualizan automáticamente

## Objetivos de Cobertura

### Corto Plazo (Próximo Release)
- Cobertura de Líneas: 20%
- Cobertura de Ramas: 10%

### Mediano Plazo (3-6 meses)
- Cobertura de Líneas: 50%
- Cobertura de Ramas: 30%

### Largo Plazo (6+ meses)
- Cobertura de Líneas: 80%
- Cobertura de Ramas: 70%

## Mejores Prácticas

1. **Prueba la Lógica Principal Primero**: Enfócate en lógica de negocio y clases de datos
2. **Prueba Casos de Error**: Asegúrate de que el manejo de errores esté cubierto
3. **Prueba Casos Extremos**: Cubre condiciones límite
4. **Mantén Tests Simples**: Escribe tests legibles y mantenibles
5. **Usa Nombres Descriptivos**: Haz que los nombres de los tests expliquen qué prueban

## Solución de Problemas

### La Cobertura No Aumenta
- Verifica si el nuevo código está siendo ejecutado por los tests
- Confirma que los archivos de test estén en el source set correcto
- Asegúrate de que los tests se estén ejecutando realmente

### Umbrales Demasiado Altos
- Baja los umbrales temporalmente
- Agrega más tests gradualmente
- Enfócate en áreas de alto impacto primero

### CI Fallando en Cobertura
- Verifica si el nuevo código está cubierto
- Confirma que los valores de umbral sean razonables
- Agrega tests para código no cubierto

## Recursos

- [Documentación de JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/)
- [Documentación de Codecov](https://docs.codecov.io/)
- [Guía de Testing de Kotlin](https://kotlinlang.org/docs/testing.html) 