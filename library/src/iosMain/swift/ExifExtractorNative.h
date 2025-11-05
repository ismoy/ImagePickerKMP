#ifndef ExifExtractorNative_h
#define ExifExtractorNative_h

#import <Foundation/Foundation.h>

@interface ExifExtractorNative : NSObject

+ (NSDictionary * _Nullable)extractExifDataFrom:(NSString * _Nonnull)imagePath;

@end

// C functions for Kotlin/Native interop
NSDictionary * _Nullable extractExifDataFromPath(const char * _Nonnull path);

#endif /* ExifExtractorNative_h */
