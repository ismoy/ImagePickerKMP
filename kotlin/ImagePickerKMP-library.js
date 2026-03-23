(function (factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', './kotlin-kotlin-stdlib.js'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('./kotlin-kotlin-stdlib.js'));
  else {
    if (typeof globalThis['kotlin-kotlin-stdlib'] === 'undefined') {
      throw new Error("Error loading module 'ImagePickerKMP:library'. Its dependency 'kotlin-kotlin-stdlib' was not found. Please, check whether 'kotlin-kotlin-stdlib' is loaded prior to 'ImagePickerKMP:library'.");
    }
    globalThis['ImagePickerKMP:library'] = factory(typeof globalThis['ImagePickerKMP:library'] === 'undefined' ? {} : globalThis['ImagePickerKMP:library'], globalThis['kotlin-kotlin-stdlib']);
  }
}(function (_, kotlin_kotlin) {
  'use strict';
  //region block: imports
  var Unit_instance = kotlin_kotlin.$_$.a;
  var isArray = kotlin_kotlin.$_$.c;
  var Exception = kotlin_kotlin.$_$.g;
  var contains = kotlin_kotlin.$_$.f;
  var THROW_CCE = kotlin_kotlin.$_$.i;
  var ensureNotNull = kotlin_kotlin.$_$.j;
  var equals = kotlin_kotlin.$_$.b;
  var toString = kotlin_kotlin.$_$.e;
  var numberToDouble = kotlin_kotlin.$_$.d;
  //endregion
  //region block: pre-declaration
  //endregion
  var io_github_ismoy_imagepickerkmp_domain_config_CompressionConfig$stable;
  var io_github_ismoy_imagepickerkmp_domain_config_UiConfig$stable;
  var io_github_ismoy_imagepickerkmp_domain_config_CameraCallbacks$stable;
  var io_github_ismoy_imagepickerkmp_domain_config_PermissionAndConfirmationConfig$stable;
  var io_github_ismoy_imagepickerkmp_domain_config_GalleryConfig$stable;
  var io_github_ismoy_imagepickerkmp_domain_config_CropConfig$stable;
  var io_github_ismoy_imagepickerkmp_domain_config_CameraCaptureConfig$stable;
  var io_github_ismoy_imagepickerkmp_domain_config_ImagePickerConfig$stable;
  var io_github_ismoy_imagepickerkmp_domain_config_CameraPreviewConfig$stable;
  var io_github_ismoy_imagepickerkmp_domain_config_CameraPermissionDialogConfig$stable;
  var io_github_ismoy_imagepickerkmp_domain_config_ImagePickerUiConstants$stable;
  var io_github_ismoy_imagepickerkmp_domain_config_PermissionConfig$stable;
  var io_github_ismoy_imagepickerkmp_domain_exceptions_ImagePickerException$stable;
  var io_github_ismoy_imagepickerkmp_domain_exceptions_PhotoCaptureException$stable;
  var io_github_ismoy_imagepickerkmp_domain_exceptions_PermissionDeniedException$stable;
  var io_github_ismoy_imagepickerkmp_domain_exceptions_ImageProcessingException$stable;
  var io_github_ismoy_imagepickerkmp_domain_models_GalleryPhotoResult$stable;
  var io_github_ismoy_imagepickerkmp_domain_models_PhotoResult$stable;
  var io_github_ismoy_imagepickerkmp_domain_utils_CompressionUtils$stable;
  var io_github_ismoy_imagepickerkmp_domain_utils_DefaultLogger$stable;
  var io_github_ismoy_imagepickerkmp_presentation_ui_components_helper_utility_CropUtils$stable;
  var io_github_ismoy_imagepickerkmp_presentation_viewModel_ImagePickerViewModel$stable;
  function ImagePickerLauncher(options) {
    var config = options.config;
    if (config == null) {
      console.error("ImagePickerLauncher: 'config' parameter is required");
      return Unit_instance;
    }
    var tmp = config.onPhotoCaptured;
    var onPhotoCaptured = (!(tmp == null) ? typeof tmp === 'function' : false) ? tmp : null;
    var tmp_0 = config.onError;
    var tmp0_elvis_lhs = (!(tmp_0 == null) ? typeof tmp_0 === 'function' : false) ? tmp_0 : null;
    var tmp_1;
    if (tmp0_elvis_lhs == null) {
      tmp_1 = jsImagePickerLauncher$lambda;
    } else {
      tmp_1 = tmp0_elvis_lhs;
    }
    var onError = tmp_1;
    var tmp_2 = config.onDismiss;
    var tmp1_elvis_lhs = (!(tmp_2 == null) ? typeof tmp_2 === 'function' : false) ? tmp_2 : null;
    var tmp_3;
    if (tmp1_elvis_lhs == null) {
      tmp_3 = jsImagePickerLauncher$lambda_0;
    } else {
      tmp_3 = tmp1_elvis_lhs;
    }
    var onDismiss = tmp_3;
    var tmp_4 = config.onPhotosSelected;
    var onPhotosSelected = (!(tmp_4 == null) ? typeof tmp_4 === 'function' : false) ? tmp_4 : null;
    var tmp_5 = config.allowMultiple;
    var tmp2_elvis_lhs = (!(tmp_5 == null) ? typeof tmp_5 === 'boolean' : false) ? tmp_5 : null;
    var allowMultiple = tmp2_elvis_lhs == null ? false : tmp2_elvis_lhs;
    var tmp_6 = config.mimeTypes;
    var tmp3_elvis_lhs = (!(tmp_6 == null) ? isArray(tmp_6) : false) ? tmp_6 : null;
    var tmp_7;
    if (tmp3_elvis_lhs == null) {
      // Inline function 'kotlin.arrayOf' call
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      tmp_7 = ['image/*'];
    } else {
      tmp_7 = tmp3_elvis_lhs;
    }
    var mimeTypes = tmp_7;
    if (onPhotoCaptured == null) {
      onError("ImagePickerLauncher: 'onPhotoCaptured' callback is required");
      return Unit_instance;
    }
    launchImagePickerFromJS(options, onPhotoCaptured, onError, onDismiss, onPhotosSelected);
  }
  function GalleryPickerLauncher(options) {
    var tmp = options.onPhotosSelected;
    var onPhotosSelected = (!(tmp == null) ? typeof tmp === 'function' : false) ? tmp : null;
    var tmp_0 = options.onError;
    var tmp0_elvis_lhs = (!(tmp_0 == null) ? typeof tmp_0 === 'function' : false) ? tmp_0 : null;
    var tmp_1;
    if (tmp0_elvis_lhs == null) {
      tmp_1 = jsGalleryPickerLauncher$lambda;
    } else {
      tmp_1 = tmp0_elvis_lhs;
    }
    var onError = tmp_1;
    var tmp_2 = options.onDismiss;
    var tmp1_elvis_lhs = (!(tmp_2 == null) ? typeof tmp_2 === 'function' : false) ? tmp_2 : null;
    var tmp_3;
    if (tmp1_elvis_lhs == null) {
      tmp_3 = jsGalleryPickerLauncher$lambda_0;
    } else {
      tmp_3 = tmp1_elvis_lhs;
    }
    var onDismiss = tmp_3;
    var tmp_4 = options.allowMultiple;
    var tmp2_elvis_lhs = (!(tmp_4 == null) ? typeof tmp_4 === 'boolean' : false) ? tmp_4 : null;
    var allowMultiple = tmp2_elvis_lhs == null ? false : tmp2_elvis_lhs;
    var tmp_5 = options.mimeTypes;
    var tmp3_elvis_lhs = (!(tmp_5 == null) ? isArray(tmp_5) : false) ? tmp_5 : null;
    var tmp_6;
    if (tmp3_elvis_lhs == null) {
      // Inline function 'kotlin.arrayOf' call
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      tmp_6 = ['image/*'];
    } else {
      tmp_6 = tmp3_elvis_lhs;
    }
    var mimeTypes = tmp_6;
    var tmp_7 = options.selectionLimit;
    var tmp4_elvis_lhs = (!(tmp_7 == null) ? typeof tmp_7 === 'number' : false) ? tmp_7 : null;
    var selectionLimit = tmp4_elvis_lhs == null ? allowMultiple ? 10 : 1 : tmp4_elvis_lhs;
    var tmp_8 = options.enableCrop;
    var tmp5_elvis_lhs = (!(tmp_8 == null) ? typeof tmp_8 === 'boolean' : false) ? tmp_8 : null;
    var enableCrop = tmp5_elvis_lhs == null ? false : tmp5_elvis_lhs;
    var tmp_9 = options.fileFilterDescription;
    var tmp6_elvis_lhs = (!(tmp_9 == null) ? typeof tmp_9 === 'string' : false) ? tmp_9 : null;
    var fileFilterDescription = tmp6_elvis_lhs == null ? 'Images' : tmp6_elvis_lhs;
    if (onPhotosSelected == null) {
      onError("GalleryPickerLauncher: 'onPhotosSelected' callback is required");
      return Unit_instance;
    }
    launchGalleryFromJS(options, onPhotosSelected, onError, onDismiss);
  }
  function hasCameraSupport() {
    var tmp;
    try {
      tmp = (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) != null;
    } catch ($p) {
      var tmp_0;
      if ($p instanceof Exception) {
        var e = $p;
        tmp_0 = false;
      } else {
        throw $p;
      }
      tmp = tmp_0;
    }
    return tmp;
  }
  function isMobileDevice() {
    var tmp;
    try {
      // Inline function 'kotlin.text.lowercase' call
      // Inline function 'kotlin.js.asDynamic' call
      var userAgent = window.navigator.userAgent.toLowerCase();
      var tmp_0;
      if (contains(userAgent, 'mobile') || contains(userAgent, 'android') || contains(userAgent, 'iphone') || contains(userAgent, 'ipad')) {
        tmp_0 = true;
      } else {
        // Inline function 'kotlin.js.unsafeCast' call
        tmp_0 = 'ontouchstart' in window || navigator.maxTouchPoints > 0;
      }
      tmp = tmp_0;
    } catch ($p) {
      var tmp_1;
      if ($p instanceof Exception) {
        var e = $p;
        tmp_1 = false;
      } else {
        throw $p;
      }
      tmp = tmp_1;
    }
    return tmp;
  }
  function launchImagePickerFromJS(options, onPhotoCaptured, onError, onDismiss, onPhotosSelected) {
    var tmp = launchImagePickerFromJS$lambda(onPhotoCaptured, onError, onDismiss);
    showCameraDialog(tmp, launchImagePickerFromJS$lambda_0(onPhotoCaptured, onError, onDismiss), onDismiss);
  }
  function launchGalleryFromJS(options, onPhotosSelected, onError, onDismiss) {
    var tmp = options.allowMultiple;
    var tmp0_elvis_lhs = (!(tmp == null) ? typeof tmp === 'boolean' : false) ? tmp : null;
    var allowMultiple = tmp0_elvis_lhs == null ? false : tmp0_elvis_lhs;
    var tmp_0 = document.createElement('input');
    var input = tmp_0 instanceof HTMLInputElement ? tmp_0 : THROW_CCE();
    input.type = 'file';
    input.accept = 'image/*';
    input.multiple = allowMultiple;
    input.style.display = 'none';
    input.onchange = launchGalleryFromJS$lambda(input, onPhotosSelected, onError, onDismiss);
    var tmp1_safe_receiver = document.body;
    if (tmp1_safe_receiver == null)
      null;
    else
      tmp1_safe_receiver.appendChild(input);
    input.click();
    var tmp2_safe_receiver = document.body;
    if (tmp2_safe_receiver == null)
      null;
    else
      tmp2_safe_receiver.removeChild(input);
  }
  function showCameraDialog(onCameraSelected, onGallerySelected, onDismiss) {
    if (hasCameraSupport()) {
      var tmp = document.createElement('div');
      var overlay = tmp instanceof HTMLDivElement ? tmp : THROW_CCE();
      overlay.style.cssText = 'position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 10000;';
      var tmp_0 = document.createElement('div');
      var dialog = tmp_0 instanceof HTMLDivElement ? tmp_0 : THROW_CCE();
      dialog.style.cssText = 'background: white; padding: 20px; border-radius: 8px; text-align: center;';
      var tmp_1 = document.createElement('h3');
      var title = tmp_1 instanceof HTMLHeadingElement ? tmp_1 : THROW_CCE();
      title.textContent = 'Seleccionar imagen';
      dialog.appendChild(title);
      var tmp_2 = document.createElement('button');
      var cameraBtn = tmp_2 instanceof HTMLButtonElement ? tmp_2 : THROW_CCE();
      cameraBtn.textContent = 'C\xE1mara';
      cameraBtn.style.cssText = 'margin: 10px; padding: 10px 20px;';
      dialog.appendChild(cameraBtn);
      var tmp_3 = document.createElement('button');
      var galleryBtn = tmp_3 instanceof HTMLButtonElement ? tmp_3 : THROW_CCE();
      galleryBtn.textContent = 'Galer\xEDa';
      galleryBtn.style.cssText = 'margin: 10px; padding: 10px 20px;';
      dialog.appendChild(galleryBtn);
      overlay.appendChild(dialog);
      var tmp0_safe_receiver = document.body;
      if (tmp0_safe_receiver == null)
        null;
      else
        tmp0_safe_receiver.appendChild(overlay);
      cameraBtn.onclick = showCameraDialog$lambda(overlay, onCameraSelected);
      galleryBtn.onclick = showCameraDialog$lambda_0(overlay, onGallerySelected);
      overlay.onclick = showCameraDialog$lambda_1(overlay, onDismiss);
    } else {
      onGallerySelected();
    }
  }
  function handleCameraCapture(onPhotoCaptured, onError, onDismiss) {
    var tmp = document.createElement('video');
    var videoElement = tmp instanceof HTMLVideoElement ? tmp : THROW_CCE();
    videoElement.autoplay = true;
    videoElement.style.cssText = 'position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; object-fit: cover; z-index: 9999;';
    var tmp_0 = document.createElement('button');
    var captureButton = tmp_0 instanceof HTMLButtonElement ? tmp_0 : THROW_CCE();
    captureButton.textContent = 'Capturar';
    captureButton.style.cssText = 'position: fixed; bottom: 50px; left: 50%; transform: translateX(-50%); z-index: 10000; padding: 15px 30px; font-size: 16px;';
    var tmp_1 = document.createElement('button');
    var closeButton = tmp_1 instanceof HTMLButtonElement ? tmp_1 : THROW_CCE();
    closeButton.textContent = '\xD7';
    closeButton.style.cssText = 'position: fixed; top: 20px; right: 20px; z-index: 10000; width: 40px; height: 40px; border-radius: 50%; background: rgba(0,0,0,0.5); color: white; border: none; font-size: 20px;';
    var tmp0_safe_receiver = document.body;
    if (tmp0_safe_receiver == null)
      null;
    else
      tmp0_safe_receiver.appendChild(videoElement);
    var tmp1_safe_receiver = document.body;
    if (tmp1_safe_receiver == null)
      null;
    else
      tmp1_safe_receiver.appendChild(captureButton);
    var tmp2_safe_receiver = document.body;
    if (tmp2_safe_receiver == null)
      null;
    else
      tmp2_safe_receiver.appendChild(closeButton);
    var mediaDevices = navigator.mediaDevices;
    mediaDevices.getUserMedia({video: true}).then(handleCameraCapture$lambda(videoElement, captureButton, closeButton, onPhotoCaptured, onDismiss)).catch(handleCameraCapture$lambda_0(videoElement, captureButton, closeButton, onError));
  }
  function handleFilePickerForImages(onPhotoCaptured, onError, onDismiss) {
    var tmp = document.createElement('input');
    var input = tmp instanceof HTMLInputElement ? tmp : THROW_CCE();
    input.type = 'file';
    input.accept = 'image/*';
    input.style.display = 'none';
    input.onchange = handleFilePickerForImages$lambda(input, onDismiss, onPhotoCaptured);
    var tmp0_safe_receiver = document.body;
    if (tmp0_safe_receiver == null)
      null;
    else
      tmp0_safe_receiver.appendChild(input);
    input.click();
    var tmp1_safe_receiver = document.body;
    if (tmp1_safe_receiver == null)
      null;
    else
      tmp1_safe_receiver.removeChild(input);
  }
  function processSelectedFiles(files, onPhotosSelected, onError) {
    var results = [];
    var processedCount = {_v: 0};
    var totalFiles = files.length;
    var inductionVariable = 0;
    if (inductionVariable < totalFiles)
      do {
        var i = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        var file = ensureNotNull(files.item(i));
        var reader = new FileReader();
        reader.onload = processSelectedFiles$lambda(reader, file, results, processedCount, totalFiles, onPhotosSelected);
        reader.onerror = processSelectedFiles$lambda_0(onError, file);
        reader.readAsDataURL(file);
      }
       while (inductionVariable < totalFiles);
  }
  function jsImagePickerLauncher$lambda(error) {
    console.error('ImagePicker Error:', error);
    return Unit_instance;
  }
  function jsImagePickerLauncher$lambda_0() {
    return Unit_instance;
  }
  function jsGalleryPickerLauncher$lambda(error) {
    console.error('GalleryPicker Error:', error);
    return Unit_instance;
  }
  function jsGalleryPickerLauncher$lambda_0() {
    return Unit_instance;
  }
  function launchImagePickerFromJS$lambda($onPhotoCaptured, $onError, $onDismiss) {
    return function () {
      handleCameraCapture($onPhotoCaptured, $onError, $onDismiss);
      return Unit_instance;
    };
  }
  function launchImagePickerFromJS$lambda_0($onPhotoCaptured, $onError, $onDismiss) {
    return function () {
      handleFilePickerForImages($onPhotoCaptured, $onError, $onDismiss);
      return Unit_instance;
    };
  }
  function launchGalleryFromJS$lambda($input, $onPhotosSelected, $onError, $onDismiss) {
    return function (event) {
      var files = $input.files;
      var tmp;
      if (!(files == null) && files.length > 0) {
        processSelectedFiles(files, $onPhotosSelected, $onError);
        tmp = Unit_instance;
      } else {
        tmp = $onDismiss();
      }
      return Unit_instance;
    };
  }
  function showCameraDialog$lambda($overlay, $onCameraSelected) {
    return function (it) {
      var tmp0_safe_receiver = document.body;
      if (tmp0_safe_receiver == null)
        null;
      else
        tmp0_safe_receiver.removeChild($overlay);
      $onCameraSelected();
      return Unit_instance;
    };
  }
  function showCameraDialog$lambda_0($overlay, $onGallerySelected) {
    return function (it) {
      var tmp0_safe_receiver = document.body;
      if (tmp0_safe_receiver == null)
        null;
      else
        tmp0_safe_receiver.removeChild($overlay);
      $onGallerySelected();
      return Unit_instance;
    };
  }
  function showCameraDialog$lambda_1($overlay, $onDismiss) {
    return function (event) {
      var tmp;
      if (equals(event.target, $overlay)) {
        var tmp0_safe_receiver = document.body;
        if (tmp0_safe_receiver == null)
          null;
        else
          tmp0_safe_receiver.removeChild($overlay);
        tmp = $onDismiss();
      }
      return Unit_instance;
    };
  }
  function handleCameraCapture$lambda$lambda$lambda$lambda(track) {
    return track.stop();
  }
  function handleCameraCapture$lambda$lambda$lambda($canvas, $onPhotoCaptured, $stream, $videoElement, $captureButton, $closeButton) {
    return function (blob) {
      var tmp;
      if (!(blob == null)) {
        var url = toString(URL.createObjectURL(blob));
        var result = {};
        result.uri = url;
        result.fileName = 'camera_photo_' + Date.now() + '.jpg';
        result.fileSize = numberToDouble(blob.size);
        result.width = $canvas.width;
        result.height = $canvas.height;
        $onPhotoCaptured(result);
        $stream.getTracks().forEach(handleCameraCapture$lambda$lambda$lambda$lambda);
        var tmp0_safe_receiver = document.body;
        if (tmp0_safe_receiver == null)
          null;
        else
          tmp0_safe_receiver.removeChild($videoElement);
        var tmp1_safe_receiver = document.body;
        if (tmp1_safe_receiver == null)
          null;
        else
          tmp1_safe_receiver.removeChild($captureButton);
        var tmp2_safe_receiver = document.body;
        if (tmp2_safe_receiver == null)
          null;
        else
          tmp2_safe_receiver.removeChild($closeButton);
        tmp = Unit_instance;
      }
      return Unit_instance;
    };
  }
  function handleCameraCapture$lambda$lambda($videoElement, $onPhotoCaptured, $stream, $captureButton, $closeButton) {
    return function (it) {
      var tmp = document.createElement('canvas');
      var canvas = tmp instanceof HTMLCanvasElement ? tmp : THROW_CCE();
      var tmp_0 = canvas.getContext('2d');
      var context = tmp_0 instanceof CanvasRenderingContext2D ? tmp_0 : THROW_CCE();
      canvas.width = $videoElement.videoWidth;
      canvas.height = $videoElement.videoHeight;
      context.drawImage($videoElement, 0.0, 0.0);
      canvas.toBlob(handleCameraCapture$lambda$lambda$lambda(canvas, $onPhotoCaptured, $stream, $videoElement, $captureButton, $closeButton), 'image/jpeg', 0.8);
      return Unit_instance;
    };
  }
  function handleCameraCapture$lambda$lambda$lambda_0(track) {
    return track.stop();
  }
  function handleCameraCapture$lambda$lambda_0($stream, $videoElement, $captureButton, $closeButton, $onDismiss) {
    return function (it) {
      $stream.getTracks().forEach(handleCameraCapture$lambda$lambda$lambda_0);
      var tmp0_safe_receiver = document.body;
      if (tmp0_safe_receiver == null)
        null;
      else
        tmp0_safe_receiver.removeChild($videoElement);
      var tmp1_safe_receiver = document.body;
      if (tmp1_safe_receiver == null)
        null;
      else
        tmp1_safe_receiver.removeChild($captureButton);
      var tmp2_safe_receiver = document.body;
      if (tmp2_safe_receiver == null)
        null;
      else
        tmp2_safe_receiver.removeChild($closeButton);
      $onDismiss();
      return Unit_instance;
    };
  }
  function handleCameraCapture$lambda($videoElement, $captureButton, $closeButton, $onPhotoCaptured, $onDismiss) {
    return function (stream) {
      $videoElement.srcObject = stream;
      $captureButton.onclick = handleCameraCapture$lambda$lambda($videoElement, $onPhotoCaptured, stream, $captureButton, $closeButton);
      $closeButton.onclick = handleCameraCapture$lambda$lambda_0(stream, $videoElement, $captureButton, $closeButton, $onDismiss);
      return Unit_instance;
    };
  }
  function handleCameraCapture$lambda_0($videoElement, $captureButton, $closeButton, $onError) {
    return function (error) {
      var tmp0_safe_receiver = document.body;
      if (tmp0_safe_receiver == null)
        null;
      else
        tmp0_safe_receiver.removeChild($videoElement);
      var tmp1_safe_receiver = document.body;
      if (tmp1_safe_receiver == null)
        null;
      else
        tmp1_safe_receiver.removeChild($captureButton);
      var tmp2_safe_receiver = document.body;
      if (tmp2_safe_receiver == null)
        null;
      else
        tmp2_safe_receiver.removeChild($closeButton);
      $onError('Error accessing camera: ' + error);
      return Unit_instance;
    };
  }
  function handleFilePickerForImages$lambda$lambda($reader, $file, $onPhotoCaptured) {
    return function (it) {
      var result = {};
      result.uri = $reader.result;
      result.fileName = $file.name;
      result.fileSize = numberToDouble($file.size);
      result.width = 0.0;
      result.height = 0.0;
      $onPhotoCaptured(result);
      return Unit_instance;
    };
  }
  function handleFilePickerForImages$lambda($input, $onDismiss, $onPhotoCaptured) {
    return function (it) {
      var files = $input.files;
      var tmp;
      if (!(files == null) && files.length > 0) {
        var file = ensureNotNull(files.item(0));
        var reader = new FileReader();
        reader.onload = handleFilePickerForImages$lambda$lambda(reader, file, $onPhotoCaptured);
        reader.readAsDataURL(file);
        tmp = Unit_instance;
      } else {
        tmp = $onDismiss();
      }
      return Unit_instance;
    };
  }
  function processSelectedFiles$lambda($reader, $file, $results, $processedCount, $totalFiles, $onPhotosSelected) {
    return function (it) {
      var result = {};
      result.uri = $reader.result;
      result.fileName = $file.name;
      result.fileSize = numberToDouble($file.size);
      result.width = 0.0;
      result.height = 0.0;
      $results.push(result);
      var _unary__edvuaz = $processedCount._v;
      $processedCount._v = _unary__edvuaz + 1 | 0;
      var tmp;
      if ($processedCount._v === $totalFiles) {
        tmp = $onPhotosSelected($results);
      }
      return Unit_instance;
    };
  }
  function processSelectedFiles$lambda_0($onError, $file) {
    return function (it) {
      $onError('Error reading file: ' + $file.name);
      return Unit_instance;
    };
  }
  var io_github_ismoy_imagepickerkmp_di_KoinConfiguration$stable;
  //region block: init
  io_github_ismoy_imagepickerkmp_domain_config_CompressionConfig$stable = 8;
  io_github_ismoy_imagepickerkmp_domain_config_UiConfig$stable = 0;
  io_github_ismoy_imagepickerkmp_domain_config_CameraCallbacks$stable = 0;
  io_github_ismoy_imagepickerkmp_domain_config_PermissionAndConfirmationConfig$stable = 0;
  io_github_ismoy_imagepickerkmp_domain_config_GalleryConfig$stable = 8;
  io_github_ismoy_imagepickerkmp_domain_config_CropConfig$stable = 0;
  io_github_ismoy_imagepickerkmp_domain_config_CameraCaptureConfig$stable = 8;
  io_github_ismoy_imagepickerkmp_domain_config_ImagePickerConfig$stable = 8;
  io_github_ismoy_imagepickerkmp_domain_config_CameraPreviewConfig$stable = 0;
  io_github_ismoy_imagepickerkmp_domain_config_CameraPermissionDialogConfig$stable = 0;
  io_github_ismoy_imagepickerkmp_domain_config_ImagePickerUiConstants$stable = 0;
  io_github_ismoy_imagepickerkmp_domain_config_PermissionConfig$stable = 0;
  io_github_ismoy_imagepickerkmp_domain_exceptions_ImagePickerException$stable = 8;
  io_github_ismoy_imagepickerkmp_domain_exceptions_PhotoCaptureException$stable = 8;
  io_github_ismoy_imagepickerkmp_domain_exceptions_PermissionDeniedException$stable = 8;
  io_github_ismoy_imagepickerkmp_domain_exceptions_ImageProcessingException$stable = 8;
  io_github_ismoy_imagepickerkmp_domain_models_GalleryPhotoResult$stable = 0;
  io_github_ismoy_imagepickerkmp_domain_models_PhotoResult$stable = 0;
  io_github_ismoy_imagepickerkmp_domain_utils_CompressionUtils$stable = 0;
  io_github_ismoy_imagepickerkmp_domain_utils_DefaultLogger$stable = 0;
  io_github_ismoy_imagepickerkmp_presentation_ui_components_helper_utility_CropUtils$stable = 0;
  io_github_ismoy_imagepickerkmp_presentation_viewModel_ImagePickerViewModel$stable = 8;
  io_github_ismoy_imagepickerkmp_di_KoinConfiguration$stable = 0;
  //endregion
  //region block: exports
  function $jsExportAll$(_) {
    var $io = _.io || (_.io = {});
    var $io$github = $io.github || ($io.github = {});
    var $io$github$ismoy = $io$github.ismoy || ($io$github.ismoy = {});
    var $io$github$ismoy$imagepickerkmp = $io$github$ismoy.imagepickerkmp || ($io$github$ismoy.imagepickerkmp = {});
    $io$github$ismoy$imagepickerkmp.ImagePickerLauncher = ImagePickerLauncher;
    $io$github$ismoy$imagepickerkmp.GalleryPickerLauncher = GalleryPickerLauncher;
    $io$github$ismoy$imagepickerkmp.hasCameraSupport = hasCameraSupport;
    $io$github$ismoy$imagepickerkmp.isMobileDevice = isMobileDevice;
  }
  $jsExportAll$(_);
  //endregion
  return _;
}));

//# sourceMappingURL=ImagePickerKMP-library.js.map
