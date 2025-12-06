# Add project specific ProGuard rules here.
-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlin.** { *; }
-keep class org.eclipse.lsp4j.** { *; }
-keep class io.github.rosemoe.sora.** { *; }
-dontwarn org.eclipse.lsp4j.**
-dontwarn io.github.rosemoe.sora.**
-keepclassmembers class * {
    @org.eclipse.lsp4j.jsonrpc.json.JsonAdapter <methods>;
}
