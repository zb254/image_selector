#import "ImagesPickerPlugin.h"
#if __has_include(<image_selector/image_selector-Swift.h>)
#import <image_selector/image_selector-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "image_selector-Swift.h"
#endif

@implementation ImagesPickerPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftImagesPickerPlugin registerWithRegistrar:registrar];
}
@end
