# SmartSpend AI

SmartSpend AI is an Android personal-finance app built with Java, Room, Firebase Authentication, Firestore, WorkManager, ML Kit OCR, and Material Design.

## What is included

- Email/password authentication, password reset, and optional biometric app lock
- Offline-first expense and budget storage with conflict-aware Firestore synchronization
- Receipt scanning with EXIF rotation, quality warnings, confidence reporting, currency detection, and scored total extraction
- Deterministic spending insights plus an optional Gemini monthly narrative grounded only in calculated aggregates
- Budget threshold alerts, daily reminders, and deduplicated monthly summaries
- Analytics, a functional monthly report, configurable account currency, search, voice-assisted entry, dark mode, and PDF export
- Room database migration support, restrictive Firestore rules, unit tests, lint, and CI

## Requirements

- Android Studio with JDK 17
- Android SDK 34
- A Firebase project with Android package com.smartspend.ai
- Email/Password Authentication and Cloud Firestore enabled

## Local setup

1. Clone the repository.
2. Download your Firebase Android configuration as app/google-services.json. This file is intentionally ignored by Git.
3. Deploy firestore.rules to the same Firebase project.
4. Run: ./gradlew testDebugUnitTest lintDebug assembleDebug

The project can compile without google-services.json for CI and static verification, but Firebase features require it at runtime.

## Firebase AI setup

The monthly report always works offline using deterministic calculations. To enable the optional Gemini summary:

1. Add `app/google-services.json` from the Firebase project.
2. Enable Firebase AI Logic with the Google AI backend in the Firebase console.
3. Enable Firebase App Check. Debug builds use the App Check debug provider; register the printed debug token. Release builds use Play Integrity, so register the release SHA-256 certificate.
4. Set Firebase AI Logic and App Check usage alerts/quotas before production rollout.

Only monthly totals, budget, count, currency code, and category totals are sent to the model. Transaction titles, merchant names, notes, dates, and receipt images remain local to this feature. Model failure falls back to the local summary.

## Currency behavior

Settings offers INR, USD, EUR, GBP, JPY, CAD, AUD, SGD, and AED. The choice controls budgets, dashboard totals, reports, and new expenses. Existing expenses retain their stored currency and are not silently exchange-rate converted. A future multi-currency conversion feature should store the rate, rate timestamp, source, and original amount before combining currencies.

## OCR quality validation

OCR is assistive, not authoritative: users must review detected values before saving. The scanner corrects EXIF rotation, warns for low-resolution images, scores likely total lines while penalizing subtotal/tax/change lines, detects common currency markers, and exposes a field-completeness confidence score.

Before each release, test at least 100 consented or synthetic receipts across currencies, merchants, lighting, crumpling, camera angles, and printed/digital formats. Track exact-match accuracy separately for amount, date, merchant, and currency, with a launch target of at least 95% amount accuracy and no silent save path. Do not include personal receipt data in the repository.
## Release checklist

Before publishing to an app store:

1. Set a unique production application ID if this package is not final.
2. Configure a private release signing key outside the repository.
3. Add the production SHA-256 certificate fingerprint in Firebase.
4. Deploy the included Firestore rules and enable Firebase budget/usage alerts.
5. Provide a hosted privacy policy based on PRIVACY.md, a support address, and store disclosures.
6. Test database migration from version 1, offline edits, sync retries, notifications, biometrics, OCR, and PDF sharing on physical devices.
7. Run: ./gradlew testDebugUnitTest lintRelease assembleRelease
8. Review the generated release bundle with Play Console pre-launch reports.

Release builds enable code shrinking and resource shrinking. Financial data is excluded from Android cloud backup.

## Architecture

The app follows MVVM:

- Activities and fragments render state and handle user interactions.
- ViewModels own screen-facing state.
- Repositories coordinate Room and Firestore on serial executors.
- Room is the offline source of truth.
- WorkManager schedules reminders and summaries.

Sync records carry an updatedAt value. Deletions use local tombstones until Firestore confirms removal, and late acknowledgements are guarded so newer edits are not overwritten.

## Security and privacy

- Firebase configuration files and generated build artifacts are not committed.
- Firestore rules enforce authenticated ownership and validate stored fields.
- Raw OCR text is not logged.
- Local database and preferences are excluded from device backup.
- Biometric state is persisted locally and gates access on launch.

See SECURITY.md for vulnerability reporting and PRIVACY.md for the data inventory. A Firebase client configuration is not a server secret; authorization depends on Authentication, Firestore rules, and operational controls.

## Testing

Unit tests cover monetary normalization, OCR parsing, merchant categorization, and insight comparison behavior. GitHub Actions verifies the Gradle wrapper and runs unit tests, lint, and a debug build for pushes and pull requests.

## License

MIT. See LICENSE.