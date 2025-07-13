# Cobertura de Código

Este documento describe la configuración y requisitos de cobertura de código para el proyecto ImagePickerKMP.

## Requisitos de Cobertura

### Umbrales
- **Cobertura de Líneas**: Mínimo 80%
- **Cobertura de Ramas**: Mínimo 70%

### Cobertura Actual
El proyecto usa JaCoCo para medir la cobertura de código. Puedes ver la cobertura actual ejecutando:

```bash
./gradlew jacocoTestReport
```

El reporte HTML se generará en: `library/build/reports/jacoco/test/html/index.html`

## Verificación de Cobertura

Para verificar que la cobertura cumple con los umbrales mínimos:

```bash
./gradlew jacocoTestCoverageVerification
```

Esto fallará el build si la cobertura está por debajo de los umbrales requeridos.

## Reportes de Cobertura

### Reporte HTML
- Ubicación: `library/build/reports/jacoco/test/html/index.html`
- Proporciona información detallada de cobertura con análisis línea por línea

### Reporte XML
- Ubicación: `library/build/reports/jacoco/test/jacocoTestReport.xml`
- Usado por sistemas CI/CD y herramientas externas

### Badge de Cobertura
El proyecto incluye un badge de cobertura que muestra el porcentaje actual:
[![Code Coverage](https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg)](https://codecov.io/gh/ismoy/ImagePickerKMP)

## Mejorando la Cobertura

### Áreas en las que Enfocarse
1. **Código Común**: Toda la lógica compartida debe tener cobertura completa de tests
2. **Código Específico de Plataforma**: Las implementaciones de Android e iOS deben ser probadas
3. **Manejo de Errores**: Todas las rutas de excepción deben estar cubiertas
4. **Casos Extremos**: Condiciones límite y entradas inusuales

### Añadiendo Tests
Al añadir nuevas funcionalidades, asegúrate de también añadir tests correspondientes para mantener o mejorar la cobertura.

### Exclusiones de Cobertura
Alguno código puede ser excluido de los requisitos de cobertura:
- Código generado
- Implementaciones específicas de plataforma que son difíciles de probar
- Componentes de UI que requieren tests de integración

## Integración CI/CD

La cobertura se verifica automáticamente en el pipeline de CI:
1. Se ejecutan los tests
2. Se genera el reporte de cobertura
3. Se verifican los umbrales de cobertura
4. Se sube el reporte a Codecov

## Desarrollo Local

Para verificar la cobertura durante el desarrollo:

```bash
# Ejecutar tests y generar reporte de cobertura
./gradlew test jacocoTestReport

# Verificar umbrales de cobertura
./gradlew jacocoTestCoverageVerification

# Abrir reporte HTML
open library/build/reports/jacoco/test/html/index.html
```

## Herramientas de Cobertura

- **JaCoCo**: Herramienta principal de cobertura
- **Codecov**: Servicio externo de reportes de cobertura
- **GitHub Actions**: Integración CI/CD
- **Gradle**: Integración del sistema de build 