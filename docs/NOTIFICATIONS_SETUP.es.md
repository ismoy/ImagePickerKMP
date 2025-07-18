# Guía de Configuración de Notificaciones de Discord

Esta guía explica cómo configurar las notificaciones de Discord para el pipeline de CI/CD de ImagePickerKMP.

## Resumen

El proyecto incluye notificaciones de Discord para:

1. **Notificaciones Básicas** - Integradas directamente en el workflow de CI
2. **Notificaciones Avanzadas** - Workflow separado con información detallada de coverage

## Configuración de Discord

### 1. Crear Webhook de Discord

1. Ve a tu servidor de Discord
2. Selecciona el canal donde quieres las notificaciones
3. Haz clic en el ícono de engranaje → "Integrations"
4. Haz clic en "Create Webhook"
5. Dale un nombre (ej., "ImagePickerKMP CI")
6. Copia la URL del webhook

### 2. Agregar Secreto de GitHub

1. Ve a tu repositorio de GitHub
2. Navega a Settings → Secrets and variables → Actions
3. Haz clic en "New repository secret"
4. Nombre: `DISCORD_WEBHOOK_URL`
5. Valor: Pega tu URL de webhook de Discord

## Tipos de Notificaciones

### Notificaciones Básicas (Workflow de CI)

Estas se activan en cada build:

- **Éxito**: 🎉 Build completado exitosamente
- **Fallo**: ❌ Build falló

### Notificaciones Avanzadas (Workflow de Notificaciones)

Estas proporcionan información detallada:

- **Estado del Build**: Éxito/fallo con métricas detalladas
- **Reportes de Coverage**: Porcentajes de coverage de líneas y ramas
- **Alertas de Coverage**: Alertas basadas en umbrales para coverage bajo

## Formato de Mensajes

### Mensajes de Discord

```
🎉 ImagePickerKMP Build Success

Repository: ismoy/ImagePickerKMP
Branch: main
Commit: abc123...
Triggered by: username

📊 Coverage Report:
• Line Coverage: 45.2%
• Branch Coverage: 32.1%

Duration: 120s
Workflow: CI

🔗 View Details: https://github.com/...
```

## Umbrales de Coverage

El sistema incluye alertas automáticas de coverage:

- **< 20%**: ⚠️ Advertencia de coverage bajo
- **20-50%**: 📈 Coverage mejorando
- **≥ 50%**: 🎉 Coverage excelente

## Personalización

### Modificar Canal de Notificación

Edita `.github/workflows/ci.yml` y `.github/workflows/notifications.yml`:

```yaml
- name: Notify Discord on Success
  uses: Ilshidur/action-discord@master
  env:
    DISCORD_WEBHOOK: ${{ secrets.DISCORD_WEBHOOK_URL }}
  with:
    args: |
      Tu mensaje personalizado aquí...
```

### Agregar Mensajes Personalizados

Puedes personalizar el texto de notificación modificando el campo `args`:

```yaml
args: |
  🚀 **Mensaje Personalizado de Build Exitoso**
  
  **Proyecto:** ImagePickerKMP
  **Estado:** ${{ github.event.workflow_run.conclusion }}
  
  Tu mensaje personalizado aquí...
```

### Deshabilitar Notificaciones

Para deshabilitar notificaciones temporalmente, comenta los pasos de notificación en los archivos de workflow.

## Solución de Problemas

### Las Notificaciones No Funcionan

1. **Verificar Secreto**: Asegúrate de que `DISCORD_WEBHOOK_URL` esté configurado correctamente
2. **Verificar URL de Webhook**: Prueba el webhook manualmente usando curl o Postman
3. **Verificar Permisos de Canal**: Asegúrate de que el webhook tenga acceso al canal
4. **Revisar Logs de Workflow**: Revisa los logs de GitHub Actions para mensajes de error

### Probar Webhook Manualmente

```bash
# Probar webhook de Discord
curl -X POST -H 'Content-type: application/json' \
  --data '{"content":"Test notification from ImagePickerKMP"}' \
  TU_URL_DE_WEBHOOK_DE_DISCORD
```

### Problemas Comunes

1. **URL de Webhook Inválida**: Verifica el formato de la URL
2. **Canal No Encontrado**: Asegúrate de que el canal existe y el webhook tiene acceso
3. **Rate Limiting**: Discord tiene límites de tasa; las notificaciones pueden retrasarse
4. **Permisos de Workflow**: Asegúrate de que el workflow tenga permiso para enviar notificaciones

## Consideraciones de Seguridad

- **Las URLs de webhook son sensibles**: Nunca las commits al repositorio
- **Usar secretos del repositorio**: Siempre almacena las URLs de webhook en secretos de GitHub
- **Monitorear uso**: Revisa regularmente el uso de webhook para asegurar que funciona como se espera

## Configuración Avanzada

### Múltiples Canales

Puedes enviar notificaciones a múltiples canales creando múltiples webhooks:

```yaml
- name: Notify Discord - Development
  uses: Ilshidur/action-discord@master
  env:
    DISCORD_WEBHOOK: ${{ secrets.DISCORD_DEV_WEBHOOK_URL }}
  # ... otra configuración

- name: Notify Discord - Production
  uses: Ilshidur/action-discord@master
  env:
    DISCORD_WEBHOOK: ${{ secrets.DISCORD_PROD_WEBHOOK_URL }}
  # ... otra configuración
```

### Notificaciones Condicionales

Envía notificaciones solo para condiciones específicas:

```yaml
- name: Notify on Main Branch Only
  if: github.ref == 'refs/heads/main'
  uses: Ilshidur/action-discord@master
  # ... configuración de notificación
```

### Umbrales de Coverage Personalizados

Modifica los umbrales de alerta de coverage en `.github/workflows/notifications.yml`:

```yaml
${{ steps.coverage.outputs.line_coverage < 30 && '⚠️ **Low Coverage Alert!**' || '' }}
```

## Soporte

Si encuentras problemas con las notificaciones:

1. Revisa la [documentación de GitHub Actions](https://docs.github.com/en/actions)
2. Revisa la [documentación de webhook de Discord](https://discord.com/developers/docs/resources/webhook)
3. Abre un issue en el repositorio para problemas específicos 