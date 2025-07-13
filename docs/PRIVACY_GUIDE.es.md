Este documento también está disponible en inglés: [PRIVACY_GUIDE.md](docs/PRIVACY_GUIDE.md)

# Guía de Privacidad

Este documento explica los principios de privacidad de la librería ImagePickerKMP.

## Resumen

ImagePickerKMP está diseñada pensando en la privacidad. La librería **no recopila, almacena ni transmite ningún dato personal ni no personal** bajo ninguna circunstancia.

- No se recopilan datos ni por defecto ni de forma opcional.
- No se realiza ningún tipo de analítica ni seguimiento.
- No se envía información a terceros ni al autor de la librería.

## Permisos de Cámara

La librería requiere permisos de cámara para funcionar correctamente:

- **Acceso a Cámara**: Requerido para capturar fotos usando la cámara del dispositivo
- **Manejo de Permisos**: La librería maneja las solicitudes de permisos de forma transparente
- **Sin Almacenamiento**: Las imágenes capturadas no se almacenan por la librería misma
- **Control del Usuario**: Los usuarios pueden conceder o denegar permisos de cámara a través de diálogos del sistema

## Acceso a Galería

Cuando la selección de galería está habilitada:

- **Acceso a Galería**: Requerido para seleccionar fotos existentes de la galería del dispositivo
- **Sin Transmisión de Contenido**: El contenido de las imágenes seleccionadas no se transmite a ningún lugar
- **Procesamiento Local**: Las imágenes se procesan localmente en el dispositivo
- **Elección del Usuario**: Los usuarios pueden elegir qué imágenes seleccionar de su galería

## Cumplimiento Legal

Aunque ImagePickerKMP no recopila datos, los desarrolladores que usan esta librería son responsables de:

- **Cumplimiento GDPR**: Si tu app sirve a usuarios de la UE
- **Cumplimiento CCPA**: Si tu app sirve a usuarios de California
- **Otras Leyes de Privacidad**: Cumplimiento con regulaciones de privacidad aplicables
- **Consentimiento del Usuario**: Obtener el consentimiento necesario del usuario para las prácticas de datos de tu app

## Transparencia

ImagePickerKMP está comprometida con la transparencia:

- **Código Abierto**: El código fuente completo está disponible públicamente
- **Revisión de Código**: Los usuarios pueden revisar el código para verificar las prácticas de privacidad
- **Sin Funciones Ocultas**: Toda la funcionalidad es visible en el código fuente
- **Impulsada por la Comunidad**: Las preocupaciones de privacidad se abordan a través del feedback de la comunidad

## Derechos del usuario

Los usuarios pueden utilizar aplicaciones que integren ImagePickerKMP con la confianza de que su privacidad está completamente respetada.

## Contacto

Para consultas relacionadas con privacidad, contacta al responsable de la librería en: ismoy.belizaire@inmotrust.cl 