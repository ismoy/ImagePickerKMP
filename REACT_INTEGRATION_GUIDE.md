# ImagePickerKMP - React Integration Guide

[![NPM Version](https://img.shields.io/npm/v/imagepickerkmp.svg)](https://www.npmjs.com/package/imagepickerkmp)
[![NPM Downloads](https://img.shields.io/npm/dt/imagepickerkmp.svg)](https://www.npmjs.com/package/imagepickerkmp)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![TypeScript](https://img.shields.io/badge/%3C%2F%3E-TypeScript-%230074c1.svg)](http://www.typescriptlang.org/)

A complete Kotlin Multiplatform library for image picking with camera support, now available for React web applications via NPM.

##  Quick Start

### Installation

```bash
npm install imagepickerkmp
```

### Basic Setup

#### 1. HTML Setup (index.html)

Add the ImagePickerKMP bundle script to your HTML file **before** your React app loads:

```html
<!doctype html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Your React App</title>
  </head>
  <body>
    <div id="root"></div>
    
    <!-- Load ImagePickerKMP bundle -->
    <script src="/node_modules/imagepickerkmp/ImagePickerKMP-bundle.js"></script>
    
    <!-- Debug script (optional) -->
    <script>
      if (window.ImagePickerKMP) {
        console.log(' ImagePickerKMP loaded:', Object.keys(window.ImagePickerKMP));
        if (window.ImagePickerKMP.PhotoResultExtensions) {
          console.log('PhotoResultExtensions available:', Object.keys(window.ImagePickerKMP.PhotoResultExtensions));
        }
      } else {
        console.log(' ImagePickerKMP NOT loaded');
      }
    </script>
    
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
```

#### 2. TypeScript Declarations

Create type definitions for better TypeScript support:

```typescript
// types/imagepicker.d.ts
declare global {
  interface Window {
    ImagePickerKMP: {
      ImagePickerLauncher: (
        onSuccess: (result: any) => void,
        onError: (error: any) => void,
        onCancel: () => void
      ) => void;
      GalleryPickerLauncher: (
        onSuccess: (results: any) => void,
        onError: (error: any) => void,
        onCancel: () => void,
        allowMultiple: boolean
      ) => void;
      PhotoResultExtensions: {
        loadBase64: (result: any) => string;
        loadBytes: (result: any) => Uint8Array;
      };
    };
  }
}

export interface ImageResult {
  uri: string;
  fileName: string;
  fileSize: number;
  width?: number;
  height?: number;
  id: string;
}

export interface ImagePickerProps {
  onImageSelected: (image: ImageResult) => void;
  onImagesSelected: (images: ImageResult[]) => void;
  variant?: 'single' | 'multiple' | 'both';
  buttonStyle?: 'contained' | 'outlined';
  size?: 'small' | 'medium' | 'large';
  disabled?: boolean;
}
```

#### 3. React Component Implementation

```tsx
import React from 'react';
import { Button, Box } from '@mui/material';
import { PhotoCamera } from '@mui/icons-material';
import { toast } from 'react-toastify';

// Import the bundle (this ensures it's loaded)
// @ts-ignore
import 'imagepickerkmp/ImagePickerKMP-bundle.js';

const ImagePickerComponent: React.FC<ImagePickerProps> = ({
  onImageSelected,
  onImagesSelected,
  variant = 'both',
  buttonStyle = 'contained',
  size = 'medium',
  disabled = false
}) => {

  // Single image selection
  const handleSingleImagePicker = () => {
    if (!window.ImagePickerKMP) {
      toast.error('ImagePickerKMP not loaded');
      return;
    }

    window.ImagePickerKMP.ImagePickerLauncher(
      (result: any) => {
        const imageResult: ImageResult = {
          ...result,
          id: Date.now().toString() + Math.random().toString(36).substr(2, 9)
        };
        
        console.log('Single image result:', result);
        
        // Use PhotoResultExtensions for advanced processing
        if (window.ImagePickerKMP?.PhotoResultExtensions) {
          try {
            const base64 = window.ImagePickerKMP.PhotoResultExtensions.loadBase64(result);
            const bytes = window.ImagePickerKMP.PhotoResultExtensions.loadBytes(result);
            
            console.log(' Base64 length:', base64.length);
            console.log(' Bytes length:', bytes.length);
            
            toast.success(` Image processed! Base64: ${base64.length} chars`);
          } catch (error) {
            console.error(' Error processing image:', error);
            toast.error(` Error: ${error instanceof Error ? error.message : String(error)}`);
          }
        }
        
        onImageSelected(imageResult);
        toast.success(`Image selected: ${result.fileName || 'image'}`);
      },
      (error: any) => {
        toast.error(`Error selecting image: ${error}`);
      },
      () => {
        toast.info('Selection cancelled');
      }
    );
  };

  // Multiple images selection
  const handleMultipleImagesPicker = () => {
    if (!window.ImagePickerKMP) {
      toast.error('ImagePickerKMP not loaded');
      return;
    }

    window.ImagePickerKMP.GalleryPickerLauncher(
      (results: any) => {
        const resultsArray = Array.isArray(results) ? results : [results];
        const imageResults: ImageResult[] = resultsArray.map((result, index) => ({
          ...result,
          id: (Date.now() + index).toString() + Math.random().toString(36).substr(2, 9)
        }));
        
        // Process first image as example
        if (window.ImagePickerKMP?.PhotoResultExtensions && resultsArray.length > 0) {
          try {
            const firstResult = resultsArray[0];
            const base64 = window.ImagePickerKMP.PhotoResultExtensions.loadBase64(firstResult);
            const bytes = window.ImagePickerKMP.PhotoResultExtensions.loadBytes(firstResult);
            
            console.log(' Multiple images - Base64 length:', base64.length);
            console.log(' Multiple images - Bytes length:', bytes.length);
            
            toast.success(` Extensions working! Base64: ${base64.length} chars, Bytes: ${bytes.length}`);
          } catch (error) {
            console.error(' Error processing multiple images:', error);
            toast.error(` Error: ${error instanceof Error ? error.message : String(error)}`);
          }
        }
        
        onImagesSelected(imageResults);
        toast.success(`${imageResults.length} image(s) selected`);
      },
      (error: any) => {
        toast.error(`Error: ${error}`);
      },
      () => {
        toast.info('Selection cancelled');
      },
      true // allowMultiple
    );
  };

  // Button styling
  const getButtonProps = (isMultiple: boolean = false) => ({
    variant: buttonStyle as 'contained' | 'outlined',
    startIcon: <PhotoCamera />,
    disabled,
    sx: {
      backgroundColor: buttonStyle === 'contained' 
        ? (isMultiple ? '#1976d2' : '#006f29') 
        : 'transparent',
      color: buttonStyle === 'contained' 
        ? 'white' 
        : (isMultiple ? '#1976d2' : '#006f29'),
      borderColor: isMultiple ? '#1976d2' : '#006f29',
      '&:hover': {
        backgroundColor: isMultiple ? '#1565c0' : '#004d1d',
        color: 'white',
      },
      padding: size === 'small' ? '8px 16px' : size === 'large' ? '16px 32px' : '12px 24px',
      fontSize: size === 'small' ? '0.875rem' : size === 'large' ? '1.125rem' : '1rem',
      fontWeight: 'bold',
    }
  });

  return (
    <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
      {(variant === 'single' || variant === 'both') && (
        <Button
          {...getButtonProps(false)}
          onClick={handleSingleImagePicker}
        >
          Select Image
        </Button>
      )}
      
      {(variant === 'multiple' || variant === 'both') && (
        <Button
          {...getButtonProps(true)}
          onClick={handleMultipleImagesPicker}
        >
          Select Multiple
        </Button>
      )}
    </Box>
  );
};

export default ImagePickerComponent;
```

##  Usage Examples

### Basic Usage

```tsx
import ImagePickerComponent from './ImagePickerComponent';

function App() {
  const handleImageSelected = (image: ImageResult) => {
    console.log('Selected image:', image);
    // Process single image
  };

  const handleImagesSelected = (images: ImageResult[]) => {
    console.log('Selected images:', images);
    // Process multiple images
  };

  return (
    <ImagePickerComponent
      onImageSelected={handleImageSelected}
      onImagesSelected={handleImagesSelected}
      variant="both"
      buttonStyle="contained"
      size="medium"
    />
  );
}
```

### Advanced Usage with Image Processing

```tsx
import { useState } from 'react';

function ImageGallery() {
  const [images, setImages] = useState<ImageResult[]>([]);
  const [processedImages, setProcessedImages] = useState<string[]>([]);

  const handleImagesSelected = (selectedImages: ImageResult[]) => {
    setImages(selectedImages);
    
    // Process images to base64 for display
    const base64Images = selectedImages.map(image => {
      if (window.ImagePickerKMP?.PhotoResultExtensions) {
        try {
          return window.ImagePickerKMP.PhotoResultExtensions.loadBase64(image);
        } catch (error) {
          console.error('Error processing image:', error);
          return image.uri; // fallback to original URI
        }
      }
      return image.uri;
    });
    
    setProcessedImages(base64Images);
  };

  return (
    <div>
      <ImagePickerComponent
        onImageSelected={(image) => handleImagesSelected([image])}
        onImagesSelected={handleImagesSelected}
        variant="multiple"
      />
      
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '16px', marginTop: '20px' }}>
        {processedImages.map((base64, index) => (
          <div key={index}>
            <img 
              src={base64.startsWith('data:') ? base64 : `data:image/jpeg;base64,${base64}`}
              alt={`Selected ${index + 1}`}
              style={{ width: '100%', height: '200px', objectFit: 'cover', borderRadius: '8px' }}
            />
            <p>{images[index]?.fileName}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
```

## 🔧 Configuration Options

### Component Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `onImageSelected` | `(image: ImageResult) => void` | Required | Callback for single image selection |
| `onImagesSelected` | `(images: ImageResult[]) => void` | Required | Callback for multiple images selection |
| `variant` | `'single' \| 'multiple' \| 'both'` | `'both'` | Which buttons to show |
| `buttonStyle` | `'contained' \| 'outlined'` | `'contained'` | Button style variant |
| `size` | `'small' \| 'medium' \| 'large'` | `'medium'` | Button size |
| `disabled` | `boolean` | `false` | Disable buttons |

### ImageResult Interface

```typescript
interface ImageResult {
  uri: string;        // Image URI/data URL
  fileName: string;   // Original filename
  fileSize: number;   // File size in bytes
  width?: number;     // Image width (if available)
  height?: number;    // Image height (if available)
  id: string;         // Unique identifier
}
```

##  Key Features

###  Camera Support
- **Mobile**: Opens native camera app
- **Desktop**: Opens webcam interface
- **Fallback**: File picker if camera unavailable

###  Gallery/File Picker
- Single or multiple image selection
- Drag & drop support (desktop)
- Touch-friendly (mobile)

###  Image Processing
- **Base64 conversion**: `PhotoResultExtensions.loadBase64()`
- **Byte array access**: `PhotoResultExtensions.loadBytes()`
- **Automatic format detection**

###  Cross-Platform
- **React** (Primary support)
- **Vanilla JavaScript** (Fully supported)
- **Vue.js** (Compatible)
- **Angular** (Compatible)

##  Vanilla JavaScript Usage

### Simple HTML + CSS + JS Implementation

You can use ImagePickerKMP with pure vanilla JavaScript! Here's a complete example:

#### HTML Structure
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ImagePickerKMP - Vanilla JS</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .picker-buttons {
            display: flex;
            gap: 10px;
            margin: 20px 0;
        }
        
        .btn {
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-weight: bold;
            transition: background-color 0.3s;
        }
        
        .btn-camera {
            background-color: #006f29;
            color: white;
        }
        
        .btn-camera:hover {
            background-color: #004d1d;
        }
        
        .btn-gallery {
            background-color: #1976d2;
            color: white;
        }
        
        .btn-gallery:hover {
            background-color: #1565c0;
        }
        
        .image-preview {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 16px;
            margin-top: 20px;
        }
        
        .image-item {
            border: 2px solid #ddd;
            border-radius: 8px;
            overflow: hidden;
        }
        
        .image-item img {
            width: 100%;
            height: 200px;
            object-fit: cover;
        }
        
        .image-info {
            padding: 10px;
            background-color: #f5f5f5;
        }
        
        .toast {
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 12px 20px;
            border-radius: 6px;
            color: white;
            z-index: 1000;
            opacity: 0;
            transition: opacity 0.3s;
        }
        
        .toast.show {
            opacity: 1;
        }
        
        .toast.success {
            background-color: #4caf50;
        }
        
        .toast.error {
            background-color: #f44336;
        }
        
        .toast.info {
            background-color: #2196f3;
        }
    </style>
</head>
<body>
    <h1> ImagePickerKMP - Vanilla JavaScript Demo</h1>
    
    <div class="picker-buttons">
        <button id="cameraBtn" class="btn btn-camera"> Take Photo</button>
        <button id="galleryBtn" class="btn btn-gallery"> Select Images</button>
        <button id="clearBtn" class="btn" style="background-color: #ff5722; color: white;">🗑️ Clear</button>
    </div>
    
    <div id="imagePreview" class="image-preview"></div>
    
    <!-- Load ImagePickerKMP bundle -->
    <script src="/node_modules/imagepickerkmp/ImagePickerKMP-bundle.js"></script>
    
    <script>
        // Check if ImagePickerKMP is loaded
        if (window.ImagePickerKMP) {
            console.log(' ImagePickerKMP loaded:', Object.keys(window.ImagePickerKMP));
        } else {
            console.error(' ImagePickerKMP NOT loaded');
        }

        let selectedImages = [];

        // Toast notification system
        function showToast(message, type = 'info') {
            const toast = document.createElement('div');
            toast.className = `toast ${type}`;
            toast.textContent = message;
            document.body.appendChild(toast);
            
            setTimeout(() => toast.classList.add('show'), 100);
            setTimeout(() => {
                toast.classList.remove('show');
                setTimeout(() => document.body.removeChild(toast), 300);
            }, 3000);
        }

        // Camera photo capture
        document.getElementById('cameraBtn').addEventListener('click', function() {
            if (!window.ImagePickerKMP) {
                showToast('ImagePickerKMP not loaded', 'error');
                return;
            }

            window.ImagePickerKMP.ImagePickerLauncher(
                (result) => {
                    console.log(' Camera result:', result);
                    
                    // Process with PhotoResultExtensions
                    if (window.ImagePickerKMP.PhotoResultExtensions) {
                        try {
                            const base64 = window.ImagePickerKMP.PhotoResultExtensions.loadBase64(result);
                            const bytes = window.ImagePickerKMP.PhotoResultExtensions.loadBytes(result);
                            
                            console.log(' Base64 length:', base64.length);
                            console.log(' Bytes length:', bytes.length);
                            
                            result.processedBase64 = base64;
                            showToast(` Photo captured! Base64: ${base64.length} chars`, 'success');
                        } catch (error) {
                            console.error(' Error processing:', error);
                            showToast(` Processing error: ${error.message}`, 'error');
                        }
                    }
                    
                    // Add to images array
                    result.id = Date.now().toString() + Math.random().toString(36).substr(2, 9);
                    selectedImages.push(result);
                    updateImagePreview();
                },
                (error) => {
                    showToast(` Camera error: ${error}`, 'error');
                },
                () => {
                    showToast(' Camera cancelled', 'info');
                }
            );
        });

        // Gallery selection
        document.getElementById('galleryBtn').addEventListener('click', function() {
            if (!window.ImagePickerKMP) {
                showToast('ImagePickerKMP not loaded', 'error');
                return;
            }

            window.ImagePickerKMP.GalleryPickerLauncher(
                (results) => {
                    const resultsArray = Array.isArray(results) ? results : [results];
                    console.log(' Gallery results:', resultsArray);
                    
                    // Process each image
                    resultsArray.forEach((result, index) => {
                        if (window.ImagePickerKMP.PhotoResultExtensions) {
                            try {
                                const base64 = window.ImagePickerKMP.PhotoResultExtensions.loadBase64(result);
                                const bytes = window.ImagePickerKMP.PhotoResultExtensions.loadBytes(result);
                                
                                result.processedBase64 = base64;
                                console.log(` Image ${index + 1} - Base64: ${base64.length}, Bytes: ${bytes.length}`);
                            } catch (error) {
                                console.error(` Error processing image ${index + 1}:`, error);
                            }
                        }
                        
                        result.id = (Date.now() + index).toString() + Math.random().toString(36).substr(2, 9);
                        selectedImages.push(result);
                    });
                    
                    updateImagePreview();
                    showToast(` ${resultsArray.length} image(s) selected`, 'success');
                },
                (error) => {
                    showToast(` Gallery error: ${error}`, 'error');
                },
                () => {
                    showToast(' Gallery cancelled', 'info');
                },
                true // allowMultiple
            );
        });

        // Clear images
        document.getElementById('clearBtn').addEventListener('click', function() {
            selectedImages = [];
            updateImagePreview();
            showToast(' Images cleared', 'info');
        });

        // Update image preview
        function updateImagePreview() {
            const previewContainer = document.getElementById('imagePreview');
            previewContainer.innerHTML = '';

            selectedImages.forEach((image, index) => {
                const imageItem = document.createElement('div');
                imageItem.className = 'image-item';
                
                // Use processed base64 or fallback to original URI
                const imageSrc = image.processedBase64 
                    ? (image.processedBase64.startsWith('data:') 
                        ? image.processedBase64 
                        : `data:image/jpeg;base64,${image.processedBase64}`)
                    : image.uri;

                imageItem.innerHTML = `
                    <img src="${imageSrc}" alt="Selected Image ${index + 1}">
                    <div class="image-info">
                        <strong>${image.fileName || `Image ${index + 1}`}</strong><br>
                        <small>Size: ${(image.fileSize / 1024).toFixed(1)} KB</small>
                        ${image.width && image.height ? `<br><small>Dimensions: ${image.width}x${image.height}</small>` : ''}
                    </div>
                `;

                previewContainer.appendChild(imageItem);
            });
        }

        // Debug mode (optional)
        window.ImagePickerKMPDebug = true;
    </script>
</body>
</html>
```

### Key Advantages for Vanilla JS:

1. **Zero Framework Dependencies** - Just include the script and go
2. **Lightweight** - No React, Vue, or Angular overhead  
3. **Direct API Access** - Work directly with `window.ImagePickerKMP`
4. **Full CSS Control** - Style everything exactly as you want
5. **Same Features** - Camera, gallery, Base64, bytes processing
6. **Easy Integration** - Drop into any existing HTML page

### Installation for Vanilla JS:

```bash
npm install imagepickerkmp
# Then reference the script in your HTML
```

Or use CDN (when available):
```html
<script src="https://unpkg.com/imagepickerkmp@latest/ImagePickerKMP-bundle.js"></script>
```

##  Dependencies

Required peer dependencies for Material-UI styling:

```bash
npm install @mui/material @mui/icons-material react-toastify
```

##  Troubleshooting

### Common Issues

#### 1. "ImagePickerKMP is not loaded"
**Solution**: Ensure the bundle script is loaded before your React app:

```html
<script src="/node_modules/imagepickerkmp/ImagePickerKMP-bundle.js"></script>
<!-- BEFORE -->
<script type="module" src="/src/main.tsx"></script>
```

#### 2. "PhotoResultExtensions not available"
**Solution**: Check that `window.ImagePickerKMP.PhotoResultExtensions` exists:

```javascript
if (window.ImagePickerKMP?.PhotoResultExtensions) {
  // Use extensions
} else {
  console.log('Extensions not loaded');
}
```

#### 3. TypeScript errors
**Solution**: Add type declarations to your project:

```typescript
declare global {
  interface Window {
    ImagePickerKMP: any;
  }
}
```

### Debug Mode

Enable debug logging by adding this to your HTML:

```html
<script>
  window.ImagePickerKMPDebug = true;
</script>
```

##  License

MIT License - see [LICENSE](LICENSE) file for details.

##  Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

##  Links

- **NPM Package**: [imagepickerkmp](https://www.npmjs.com/package/imagepickerkmp)
- **GitHub Repository**: [ImagePickerKMP](https://github.com/ismoy/ImagePickerKMP)
- **Issues**: [Bug Reports](https://github.com/ismoy/ImagePickerKMP/issues)
- **Discussions**: [Feature Requests](https://github.com/ismoy/ImagePickerKMP/discussions)

---

**Made with ❤️ using Kotlin Multiplatform**
