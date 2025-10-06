const fs = require('fs');
const path = require('path');

console.log('🔧 Create bundle with WebRTC ...');

const bundleContent = `
(function (root, factory) {
  if (typeof module === 'object' && module.exports) {
    module.exports = factory();
  } else {
    root.ImagePickerKMP = factory();
  }
}(typeof self !== 'undefined' ? self : this, function () {
  'use strict';

  function isMobileContext() {
    try {
      const userAgent = window.navigator.userAgent.toLowerCase();
      return userAgent.includes("mobile") || 
             userAgent.includes("android") || 
             userAgent.includes("iphone") || 
             userAgent.includes("ipad") ||
             ('ontouchstart' in window || navigator.maxTouchPoints > 0);
    } catch (e) {
      return false;
    }
  }

  function handleCameraCapture(onSuccess, onError, onCancel) {
    try {
      navigator.mediaDevices.getUserMedia({ video: true })
        .then(function(stream) {
          showCameraInterface(stream, onSuccess, onError, onCancel);
        })
        .catch(function(error) {
          let errorMessage;
          if (error.name === 'NotAllowedError') {
            errorMessage = 'Camera permission denied';
          } else if (error.name === 'NotFoundError') {
            errorMessage = 'No camera found';
          } else if (error.name === 'NotReadableError') {
            errorMessage = 'Camera is being used by another application';
          } else {
            errorMessage = 'Error accessing camera: ' + error;
          }
          onError(errorMessage);
        });
    } catch (e) {
      onError(e.message || 'Camera access failed');
    }
  }

  function showCameraInterface(stream, onSuccess, onError, onCancel) {
    const overlay = document.createElement('div');
    Object.assign(overlay.style, {
      position: 'fixed',
      top: '0',
      left: '0',
      width: '100vw',
      height: '100vh',
      backgroundColor: 'rgba(0, 0, 0, 0.9)',
      zIndex: '10000',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center'
    });

    const video = document.createElement('video');
    video.srcObject = stream;
    video.autoplay = true;
    video.muted = true;
    video.playsinline = true;
    Object.assign(video.style, {
      maxWidth: '90%',
      maxHeight: '70%',
      borderRadius: '8px'
    });

    const controls = document.createElement('div');
    Object.assign(controls.style, {
      display: 'flex',
      marginTop: '20px'
    });

    const captureButton = document.createElement('button');
    captureButton.textContent = 'Loading...';
    Object.assign(captureButton.style, {
      padding: '12px 24px',
      fontSize: '16px',
      backgroundColor: '#666666',
      color: 'white',
      border: 'none',
      borderRadius: '8px',
      cursor: 'not-allowed',
      marginRight: '20px',
      opacity: '0.6'
    });
    captureButton.disabled = true;

    const cancelButton = document.createElement('button');
    cancelButton.textContent = 'Cancel';
    Object.assign(cancelButton.style, {
      padding: '12px 24px',
      fontSize: '16px',
      backgroundColor: '#FF3B30',
      color: 'white',
      border: 'none',
      borderRadius: '8px',
      cursor: 'pointer'
    });

    const canvas = document.createElement('canvas');
    canvas.style.display = 'none';

    captureButton.onclick = function() {
      try {
        if (!video.videoWidth || !video.videoHeight) {
          onError('Camera not ready, please try again');
          return;
        }

        const context = canvas.getContext('2d');
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        
        if (!context) {
          onError('Canvas context unavailable');
          return;
        }

        context.drawImage(video, 0, 0);

        let captureAttempted = false;
        
        canvas.toBlob(function(blob) {
          if (blob && !captureAttempted) {
            captureAttempted = true;
            const imageUrl = URL.createObjectURL(blob);
            const timestamp = Date.now().toString();

            if (imageUrl && imageUrl !== 'blob:') {
              const result = {
                uri: imageUrl,
                fileName: 'camera_capture_' + timestamp + '.png',
                fileSize: blob.size,
                width: canvas.width,
                height: canvas.height
              };

              cleanupCameraResources(stream, overlay);
              onSuccess(result);
            } else {
              attemptDataUrlCapture();
            }
          } else if (!captureAttempted) {
            attemptDataUrlCapture();
          }
        }, 'image/jpeg', 0.8);

        function attemptDataUrlCapture() {
          if (captureAttempted) return;
          captureAttempted = true;
          
          try {
            const dataUrl = canvas.toDataURL('image/jpeg', 0.8);
            if (dataUrl && dataUrl.length > 22) {
              const timestamp = Date.now().toString();
              const result = {
                uri: dataUrl,
                fileName: 'camera_capture_' + timestamp + '.jpg',
                fileSize: Math.round(dataUrl.length * 0.75),
                width: canvas.width,
                height: canvas.height
              };

              cleanupCameraResources(stream, overlay);
              onSuccess(result);
            } else {
              onError('Failed to capture image data');
            }
          } catch (e) {
            onError('Image capture failed: ' + (e.message || 'Unknown error'));
          }
        }

        setTimeout(function() {
          if (!captureAttempted) {
            attemptDataUrlCapture();
          }
        }, 2000);

      } catch (e) {
        cleanupCameraResources(stream, overlay);
        onError(e.message || 'Capture failed');
      }
    };

    cancelButton.onclick = function() {
      cleanupCameraResources(stream, overlay);
      onCancel();
    };

    video.addEventListener('loadedmetadata', function() {
      setTimeout(function() {
        if (video.videoWidth > 0 && video.videoHeight > 0) {
          captureButton.textContent = '📷 Capture';
          captureButton.disabled = false;
          captureButton.style.backgroundColor = '#007AFF';
          captureButton.style.cursor = 'pointer';
          captureButton.style.opacity = '1';
        }
      }, 500);
    });

    setTimeout(function() {
      if (captureButton.disabled) {
        captureButton.textContent = ' Capture';
        captureButton.disabled = false;
        captureButton.style.backgroundColor = '#007AFF';
        captureButton.style.cursor = 'pointer';
        captureButton.style.opacity = '1';
      }
    }, 3000);

    controls.appendChild(captureButton);
    controls.appendChild(cancelButton);
    overlay.appendChild(video);
    overlay.appendChild(controls);
    overlay.appendChild(canvas);
    document.body.appendChild(overlay);
  }

  function handleFilePicker(onSuccess, onError, onCancel, allowMultiple, mimeTypes) {
    try {
      console.log('handleFilePicker: Creating file input');
      var input = document.createElement('input');
      input.type = 'file';
      input.accept = mimeTypes ? mimeTypes.join(',') : 'image/*';
      input.multiple = allowMultiple;

      console.log('handleFilePicker: Input configured - multiple: ' + allowMultiple + ', accept: ' + input.accept);

      input.onchange = function(e) {
        try {
          var files = input.files;
          console.log('handleFilePicker: Files selected - count: ' + (files ? files.length : 0));
          
          if (files && files.length > 0) {
            if (allowMultiple) {
              var results = [];
              var processedCount = 0;
              var totalFiles = files.length;
              
              console.log('handleFilePicker: Processing ' + totalFiles + ' files (multiple mode)');

              for (var i = 0; i < totalFiles; i++) {
                var file = files[i];
                if (file) {
                  console.log('handleFilePicker: Processing file ' + i + ': ' + file.name + ' (' + file.size + ' bytes)');
                  
                  var reader = new FileReader();
                  reader.onload = function(loadEvent) {
                    var result = loadEvent.target.result;
                    console.log('handleFilePicker: File read - result available: ' + !!result + ', length: ' + (result ? result.length : 0));
                    
                    if (result && result.length > 0) {
                      var fileResult = {
                        uri: result,
                        fileName: file.name || ('file_' + i),
                        fileSize: file.size || 0,
                        width: 0,
                        height: 0
                      };
                      results.push(fileResult);
                      console.log('handleFilePicker: Added file result - fileName: ' + fileResult.fileName + ', uri starts with: ' + result.substring(0, 50));
                    } else {
                      console.error('handleFilePicker: Empty result for file ' + file.name);
                    }

                    processedCount++;
                    console.log('handleFilePicker: Processed ' + processedCount + '/' + totalFiles + ' files');
                    
                    if (processedCount === totalFiles) {
                      console.log('handleFilePicker: All files processed, calling onSuccess with ' + results.length + ' results');
                      onSuccess(results);
                    }
                  };
                  
                  reader.onerror = function(errorEvent) {
                    console.error('handleFilePicker: Error reading file ' + file.name + ':', errorEvent);
                    onError('Failed to read file: ' + file.name);
                  };
                  
                  reader.readAsDataURL(file);
                } else {
                  console.warn('handleFilePicker: File at index ' + i + ' is null');
                }
              }
            } else {
              var file = files[0];
              if (file) {
                console.log('handleFilePicker: Processing single file: ' + file.name + ' (' + file.size + ' bytes)');
                
                var reader = new FileReader();
                reader.onload = function(loadEvent) {
                  var result = loadEvent.target.result;
                  console.log('handleFilePicker: Single file read - result available: ' + !!result + ', length: ' + (result ? result.length : 0));
                  
                  if (result && result.length > 0) {
                    var fileResult = {
                      uri: result,
                      fileName: file.name || 'file',
                      fileSize: file.size || 0,
                      width: 0,
                      height: 0
                    };
                    console.log('handleFilePicker: Single file result - fileName: ' + fileResult.fileName + ', uri starts with: ' + result.substring(0, 50));
                    onSuccess(fileResult);
                  } else {
                    console.error('handleFilePicker: Empty result for single file');
                    onError('Failed to read file - empty result');
                  }
                };
                
                reader.onerror = function(errorEvent) {
                  console.error('handleFilePicker: Error reading single file:', errorEvent);
                  onError('Failed to read file');
                };
                
                reader.readAsDataURL(file);
              } else {
                console.error('handleFilePicker: First file is null');
                onError('Selected file is null');
              }
            }
          } else {
            console.log('handleFilePicker: No files selected, calling onCancel');
            onCancel();
          }
        } catch (e) {
          console.error('handleFilePicker: Error in onchange handler:', e);
          onError(e.message || 'File selection failed');
        }
      };

      input.click();
    } catch (e) {
      console.error('handleFilePicker: Error creating file picker:', e);
      onError(e.message || 'Failed to open file picker');
    }
  }

  function cleanupCameraResources(stream, overlay) {
    try {
      const tracks = stream.getTracks();
      for (let i = 0; i < tracks.length; i++) {
        tracks[i].stop();
      }
      if (overlay && overlay.parentNode) {
        overlay.parentNode.removeChild(overlay);
      }
    } catch (e) {
      console.error('Error during cleanup:', e);
    }
  }

  return {
    ImagePickerLauncher: function(onSuccess, onError, onCancel) {
      console.log(' ImagePickerLauncher starting...');
      
      try {
        const hasCameraSupport = !!(navigator.mediaDevices && navigator.mediaDevices.getUserMedia);
        const preferCamera = isMobileContext() && hasCameraSupport;
        
        console.log('Mobile Context:', isMobileContext());
        console.log('Support Camera:', hasCameraSupport);
        console.log('Prefer Camera:', preferCamera);

        if (hasCameraSupport && preferCamera) {
          console.log(' Opening camera with WebRTC...');
          handleCameraCapture(onSuccess, onError, onCancel);
        } else {
          console.log(' Using file picker with fallback...');
          handleFilePicker(onSuccess, onError, onCancel, false, ['image/*']);
        }
      } catch (e) {
        onError(e.message || 'ImagePickerLauncher failed');
      }
    },

    GalleryPickerLauncher: function(onSuccess, onError, onCancel, allowMultiple) {
      console.log(' GalleryPickerLauncher starting...');
      
      try {
        const mimeTypes = ['image/*'];
        handleFilePicker(onSuccess, onError, onCancel, allowMultiple || false, mimeTypes);
      } catch (e) {
        onError(e.message || 'GalleryPickerLauncher failed');
      }
    },

    // Extension functions for PhotoResult
    PhotoResultExtensions: {
      loadBytes: function(photoResult) {
        try {
          const dataUrlPrefix = 'data:';
          const base64Prefix = ';base64,';
          
          const dataString = photoResult.uri;
          if (dataString && dataString.startsWith(dataUrlPrefix) && dataString.includes(base64Prefix)) {
            const base64Data = dataString.substring(dataString.indexOf(base64Prefix) + base64Prefix.length);
            // Convert base64 to Uint8Array
            const binaryString = atob(base64Data);
            const bytes = new Uint8Array(binaryString.length);
            for (let i = 0; i < binaryString.length; i++) {
              bytes[i] = binaryString.charCodeAt(i);
            }
            return bytes;
          } else {
            return new Uint8Array(0);
          }
        } catch (e) {
          console.error('PhotoResultExtensions.loadBytes error:', e);
          return new Uint8Array(0);
        }
      },

      loadBase64: function(photoResult) {
        try {
          const dataUrlPrefix = 'data:';
          const base64Prefix = ';base64,';
          
          const dataString = photoResult.uri;
          if (dataString && dataString.startsWith(dataUrlPrefix) && dataString.includes(base64Prefix)) {
            return dataString.substring(dataString.indexOf(base64Prefix) + base64Prefix.length);
          } else {
            return '';
          }
        } catch (e) {
          console.error('PhotoResultExtensions.loadBase64 error:', e);
          return '';
        }
      },

      loadImageElement: function(photoResult, callback) {
        try {
          const img = new Image();
          img.onload = function() {
            callback(img, null);
          };
          img.onerror = function(error) {
            console.error('PhotoResultExtensions.loadImageElement error:', error);
            callback(null, error);
          };
          img.src = photoResult.uri;
        } catch (e) {
          console.error('PhotoResultExtensions.loadImageElement error:', e);
          callback(null, e);
        }
      },

      loadCanvas: function(photoResult, callback) {
        this.loadImageElement(photoResult, function(img, error) {
          if (error) {
            callback(null, error);
            return;
          }
          
          try {
            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');
            canvas.width = img.width;
            canvas.height = img.height;
            ctx.drawImage(img, 0, 0);
            callback(canvas, null);
          } catch (e) {
            console.error('PhotoResultExtensions.loadCanvas error:', e);
            callback(null, e);
          }
        });
      },

      loadBlob: function(photoResult, callback) {
        try {
          const photoExtensions = this; // Reference to PhotoResultExtensions
          const bytes = photoExtensions.loadBytes(photoResult);
          if (bytes.length > 0) {
            const blob = new Blob([bytes], { type: 'image/jpeg' });
            callback(blob, null);
          } else {
            callback(null, new Error('No bytes available'));
          }
        } catch (e) {
          console.error('PhotoResultExtensions.loadBlob error:', e);
          callback(null, e);
        }
      }
    }
  };
}));
`;

const bundleFile = './build/js/packages/ImagePickerKMP-library/ImagePickerKMP-bundle.js';
fs.writeFileSync(bundleFile, bundleContent);

console.log(' Bundle WebRTC created successfully:', bundleFile);
console.log(` Weight: ${Math.round(fs.statSync(bundleFile).size / 1024)} KB`);

const npmBundleDir = './build/js/packages/library-library';
const npmBundleFile = `${npmBundleDir}/ImagePickerKMP-bundle.js`;

if (!fs.existsSync(npmBundleDir)) {
  fs.mkdirSync(npmBundleDir, { recursive: true });
}

fs.writeFileSync(npmBundleFile, bundleContent);
console.log(' Bundle copied to npm directory:', npmBundleFile);

const bundleTypeDefinitions = `
// TypeScript declarations for ImagePickerKMP Bundle

declare module 'imagepickerkmp/ImagePickerKMP-bundle.js' {
  export interface PhotoResult {
    uri: string;
    fileName: string;
    fileSize: number;
    width?: number;
    height?: number;
  }

  export interface PhotoResultExtensions {
    loadBytes: (photoResult: PhotoResult) => Uint8Array;
    loadBase64: (photoResult: PhotoResult) => string;
    loadImageElement: (photoResult: PhotoResult, callback: (img: HTMLImageElement | null, error: Error | null) => void) => void;
    loadCanvas: (photoResult: PhotoResult, callback: (canvas: HTMLCanvasElement | null, error: Error | null) => void) => void;
    loadBlob: (photoResult: PhotoResult, callback: (blob: Blob | null, error: Error | null) => void) => void;
  }

  export interface ImagePickerKMP {
    ImagePickerLauncher: (
      onSuccess: (result: PhotoResult) => void,
      onError: (error: string) => void,
      onCancel: () => void
    ) => void;

    GalleryPickerLauncher: (
      onSuccess: (results: PhotoResult | PhotoResult[]) => void,
      onError: (error: string) => void,
      onCancel: () => void,
      allowMultiple?: boolean
    ) => void;

    PhotoResultExtensions: PhotoResultExtensions;
  }

  const ImagePickerKMP: ImagePickerKMP;
  export default ImagePickerKMP;
}

declare module 'imagepickerkmp/ImagePickerKMP-bundle' {
  export * from 'imagepickerkmp/ImagePickerKMP-bundle.js';
}

declare module 'imagepickerkmp' {
  export * from 'imagepickerkmp/ImagePickerKMP-bundle.js';
}
`;

const bundleTypeFile = './build/js/packages/ImagePickerKMP-library/ImagePickerKMP-bundle.d.ts';
fs.writeFileSync(bundleTypeFile, bundleTypeDefinitions);
console.log(' Bundle TypeScript declarations created:', bundleTypeFile);

const npmTypeFile = `${npmBundleDir}/ImagePickerKMP-bundle.d.ts`;
fs.writeFileSync(npmTypeFile, bundleTypeDefinitions);
console.log(' Bundle TypeScript declarations copied to npm directory:', npmTypeFile);

