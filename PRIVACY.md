# SmartSpend AI Privacy Information

This document is a deployment template and technical data inventory. The publisher must review it, add its legal identity and contact details, host the final policy publicly, and adapt it to actual production services and applicable law.

## Data processed

SmartSpend AI may process account identifiers, email address, display name, expense and budget records, receipt images selected by the user, OCR-derived receipt details, and app preferences.

## Purpose

The data is used to authenticate users, store and synchronize their financial records, scan receipts, generate spending insights and optional aggregate-only AI summaries, display analytics, send local reminders, and export reports requested by the user.

## Storage and sharing

Expense and budget records are stored locally with Room and, when configured, synchronized to the publisher's Firebase project. Authentication is provided by Firebase Authentication and cloud records by Cloud Firestore. Receipt OCR is performed with Google ML Kit. PDF reports are created locally and shared only through an app selected by the user. For foreign-currency expenses, the app sends only the source currency code, account currency code, and requested date to the configured Frankfurter reference-rate endpoint; expense amounts, titles, notes, and identities are not sent to that service. As with any internet request, the service and its delivery network may receive connection metadata such as the device IP address.

The publisher should document Firebase/Google as a service provider, its retention settings, hosting region, and any additional analytics enabled in the production build.

## User controls

Users can edit and delete expenses, sign out, disable biometric access, and control notification permission in Android settings. The publisher must provide a verified process for account deletion and deletion/export requests before release.

## Security

Cloud access is restricted by authenticated-user Firestore rules. Raw OCR receipt text is not written to application logs. Financial databases and preferences are excluded from Android cloud backup. No security measure is absolute.

## Contact and effective date

Publisher: [ADD LEGAL NAME]
Contact: [ADD SUPPORT/PRIVACY ADDRESS]
Effective date: [ADD DATE]