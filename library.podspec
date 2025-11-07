Pod::Spec.new do |s|
  s.name         = "library"
  s.version      = "1.0"
  s.summary      = "ImagePickerKMP Swift Extensions"
  s.homepage     = "https://github.com/ismoy/imagepickerkmp"
  s.license      = { :type => "MIT", :file => "LICENSE" }
  s.author       = { "Ismoy" => "ismoy@example.com" }
  
  s.platform     = :ios, "11.0"
  s.ios.deployment_target = "11.0"
  
  s.source       = { :path => "." }
  s.source_files = "library/src/iosMain/swift/**/*.{swift,h,m}"
  
  s.frameworks = 'Foundation', 'UIKit', 'ImageIO', 'CoreLocation', 'CoreFoundation'
  
  s.swift_version = '5.0'
  
  # Dependencias del framework Kotlin
  s.dependency 'imagepickerkmp'
end
