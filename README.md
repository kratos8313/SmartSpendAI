# SmartSpend AI

SmartSpend AI is an Android personal-finance app built with Java, Room, Firebase Authentication, Firestore, WorkManager, ML Kit OCR, and Material Design.

## What is included

- Email/password authentication, password reset, and optional biometric app lock
- Offline-first expense and budget storage with conflict-aware Firestore synchronization
- Receipt scanning with sampled image decoding and merchant, amount, and date extraction
- Rule-based spending insights using like-for-like month-to-date comparisons
- Budget threshold alerts, daily reminders, and deduplicated monthly summaries
- Analytics, search, category filters, voice-assisted entry, dark mode, and PDF export
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