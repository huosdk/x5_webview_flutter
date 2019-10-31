#import "X5WebviewPlugin.h"

@interface X5WebviewPlugin : NSObject<FlutterPlugin>
@end

@implementation X5WebviewPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {

  FlutterMethodChannel *channel = [FlutterMethodChannel
              methodChannelWithName:@"x5_webview"
                    binaryMessenger:[registrar messenger]];
  X5WebviewPlugin* instance = [[X5WebviewPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];

}
@end

