package com.smartspend.ai;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
public final class AppCheckInitializer {
 private AppCheckInitializer() {} public static void initialize() { FirebaseAppCheck.getInstance().installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance()); }
}
