# Firestore creates these objects through reflection.
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.smartspend.ai.models.** { *; }

# Keep generic type metadata used by Firebase Tasks and Firestore.
-keepattributes InnerClasses,EnclosingMethod