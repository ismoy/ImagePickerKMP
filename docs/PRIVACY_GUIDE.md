# Privacy Guide

This document explains the privacy principles of the ImagePickerKMP library.

## Overview

ImagePickerKMP is designed with privacy in mind. The library does **not collect, store, or transmit any personal or non-personal data** under any circumstances.

- No data is collected by default or optionally.
- No analytics or tracking is performed.
- No information is sent to third parties or the library author.

## Camera Permissions

The library requires camera permissions to function properly:

- **Camera Access**: Required to capture photos using the device camera
- **Permission Handling**: The library handles permission requests transparently
- **No Image Storage**: Captured images are not stored by the library itself
- **User Control**: Users can grant or deny camera permissions through system dialogs

## Gallery Access

When gallery selection is enabled:

- **Gallery Access**: Required to select existing photos from the device gallery
- **No Content Transmission**: Selected image content is not transmitted anywhere
- **Local Processing**: Images are processed locally on the device
- **User Choice**: Users can choose which images to select from their gallery

## Legal Compliance

While ImagePickerKMP itself does not collect data, developers using this library are responsible for:

- **GDPR Compliance**: If your app serves EU users
- **CCPA Compliance**: If your app serves California users
- **Other Privacy Laws**: Compliance with applicable privacy regulations
- **User Consent**: Obtaining necessary user consent for your app's data practices

## Transparency

ImagePickerKMP is committed to transparency:

- **Open Source**: The complete source code is publicly available
- **Code Review**: Users can review the code to verify privacy practices
- **No Hidden Features**: All functionality is visible in the source code
- **Community Driven**: Privacy concerns are addressed through community feedback

## User Rights

End users can use applications that integrate ImagePickerKMP with the confidence that their privacy is fully respected.

## Contact

For privacy-related questions, contact the library maintainer at: ismoy.belizaire@inmotrust.cl 