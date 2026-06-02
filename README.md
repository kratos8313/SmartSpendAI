# 💰 SmartSpend AI — Android Expense Tracker

> An industry-grade, AI-powered personal finance app built with Java, Firebase, Room, ML Kit OCR, and MPAndroidChart.

---

## 📱 Features

| Feature | Details |
|---|---|
| 🔐 Authentication | Firebase Email/Password + Biometric |
| 💸 Expense Management | Add, Edit, Delete with Categories, Tags, Notes |
| 📷 Receipt Scanner | ML Kit OCR — extract merchant, amount, date |
| 🤖 AI Insights | Overspending detection, savings tips, predictions |
| 📊 Analytics | Pie chart (categories) + Bar chart (weekly) |
| 💰 Budget Planner | Monthly budget + per-category budgets + alerts |
| ☁️ Cloud Sync | Firebase Firestore real-time sync |
| 📦 Offline Mode | Room Database local cache |
| 🔔 Notifications | Daily reminders + budget alerts + monthly reports |
| 🌙 Dark Mode | Material Design 3 full dark theme |
| 🎤 Voice Input | Speech-to-text expense entry |
| 📄 PDF Export | Export monthly spending reports |

---

## 🏗️ Architecture

```
MVVM + Repository Pattern
├── Activities / Fragments (View)
│   ├── SplashActivity
│   ├── AuthActivity (Login / Signup / Forgot Password)
│   ├── MainActivity (Bottom Navigation Host)
│   ├── AddExpenseActivity
│   ├── ReceiptScannerActivity
│   └── SettingsActivity
│
├── ViewModels (Business Logic)
│   ├── ExpenseViewModel
│   └── BudgetViewModel
│
├── Repositories (Data Layer)
│   ├── ExpenseRepository  ← Room DB + Firestore sync
│   └── BudgetRepository   ← Room DB + Firestore sync
│
├── Database (Room)
│   ├── AppDatabase
│   ├── ExpenseDao
│   └── BudgetDao
│
├── Models
│   ├── Expense
│   ├── Budget
│   ├── User
│   └── AiInsight
│
└── Utils
    ├── AiEngine        ← AI analytics logic
    ├── OcrUtils        ← ML Kit OCR receipt scanning
    ├── CategoryUtils   ← Icons and colors
    ├── CurrencyUtils   ← INR formatting
    ├── DateUtils       ← Date range helpers
    ├── NotificationUtils
    └── ThemeUtils
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| UI | XML + Material Design 3 |
| Architecture | MVVM + Repository |
| Local DB | Room Database |
| Cloud DB | Firebase Firestore |
| Auth | Firebase Authentication |
| OCR | Google ML Kit Text Recognition |
| Charts | MPAndroidChart |
| Background | WorkManager |
| Images | Glide |
| Networking | Retrofit + OkHttp |
| Security | AndroidX Biometric |

---

## 🚀 Setup Instructions

### Step 1: Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create project: `SmartSpend AI`
3. Add Android app — Package: `com.smartspend.ai`
4. Download `google-services.json` → place in `app/` directory
5. Enable **Authentication** → Email/Password
6. Enable **Firestore Database** → Start in production mode
7. Deploy Firestore rules from `firestore.rules`

### Step 2: Fonts (Required)

Download **Poppins** from [Google Fonts](https://fonts.google.com/specimen/Poppins):

Place these files in `app/src/main/res/font/`:
- `poppins_regular.ttf`
- `poppins_medium.ttf`
- `poppins_semibold.ttf`
- `poppins_bold.ttf`

### Step 3: Build & Run

```bash
# Clone or open the project in Android Studio
# Ensure minSdk 26+, compileSdk 34

./gradlew assembleDebug
```

Or open in **Android Studio Hedgehog+** and click ▶️ Run.

### Step 4: Optional — Currency Conversion API

To enable real-time currency conversion, add to `local.properties`:
```
EXCHANGE_RATE_API_KEY=your_key_here
```
Get a free key at [exchangerate-api.com](https://exchangerate-api.com)

---

## 📂 Project Structure

```
SmartSpendAI/
├── app/
│   ├── src/main/
│   │   ├── java/com/smartspend/ai/
│   │   │   ├── activities/         # All Activities
│   │   │   ├── adapters/           # RecyclerView Adapters
│   │   │   ├── database/           # Room DAO + Database
│   │   │   ├── fragments/          # 5 Navigation Fragments
│   │   │   ├── models/             # Data Models
│   │   │   ├── repositories/       # Data Repositories
│   │   │   ├── utils/              # Helpers & AI Engine
│   │   │   ├── viewmodels/         # ViewModels
│   │   │   ├── workers/            # WorkManager
│   │   │   └── SmartSpendApp.java  # Application class
│   │   └── res/
│   │       ├── anim/               # Slide & fade animations
│   │       ├── color/              # Color state lists
│   │       ├── drawable/           # Vector icons & backgrounds
│   │       ├── font/               # Poppins font family
│   │       ├── layout/             # All XML layouts
│   │       ├── menu/               # Bottom navigation menu
│   │       ├── mipmap-*/           # Launcher icons
│   │       ├── values/             # Colors, strings, themes, dimens
│   │       └── xml/                # FileProvider, backup rules
│   ├── build.gradle
│   └── google-services.json        # ← Replace with yours!
├── build.gradle
├── settings.gradle
└── firestore.rules
```

---

## 🤖 AI Engine Logic

The `AiEngine.java` class powers all intelligent features:

```java
// Auto-categorize a merchant name
String category = AiEngine.autoCategorizeMerchant("McDonald's");
// → "Food"

// Generate insights comparing current vs last month
List<AiInsight> insights = AiEngine.generateInsights(
    currentMonthExpenses,
    lastMonthExpenses,
    monthlyBudget
);
```

Insights include:
- 📈 Spending spike detection (>20% increase)
- 🎉 Savings achievement (>10% decrease)
- 🚨 Category overspending alerts (>35% increase per category)
- 💡 Budget usage warnings (80% and 100% thresholds)
- 🔮 End-of-month spending projection
- 💰 Savings opportunity suggestions (15% reduction target)

---

## 📷 Receipt Scanner

Uses **Google ML Kit Text Recognition** to:
1. Capture photo via CameraX or pick from gallery
2. Run OCR on the receipt image
3. Extract: merchant name, total amount, date
4. Auto-categorize using AI keyword matching
5. Pre-fill the Add Expense form

---

## 🔔 Notifications

| Channel | Trigger | Time |
|---|---|---|
| Budget Alert | >80% or 100% budget used | Real-time |
| Daily Reminder | Every day | 8:00 PM |
| Monthly Report | First of month | Automatic |

---

## 🎨 Design System

- **Primary**: `#4F46E5` (Indigo)
- **Secondary**: `#7C3AED` (Violet)
- **Font**: Poppins (Regular, Medium, SemiBold, Bold)
- **Corners**: 12–16dp cards, 20dp chips
- **Charts**: MPAndroidChart with custom category colors

---

## 📊 Dashboard Screens

| Screen | Description |
|---|---|
| 🏠 Home | Greeting, monthly total, budget progress, quick actions, AI insight strip, recent expenses |
| 📋 Expenses | Full list with search, category filter chips, swipe-to-delete |
| 📊 Analytics | Pie chart breakdown + weekly bar chart + month comparison |
| 💰 Budget | Set total + per-category budgets, live progress bars |
| ✨ Insights | Full AI insights list with stats header |

---

## 🧩 Known Limitations & Next Steps

- [ ] Real currency conversion API integration (Retrofit endpoint wired up)
- [ ] PDF export implementation (iText7 dependency included)
- [ ] Lottie animations for empty states
- [ ] Google Sign-In (dependency ready)
- [ ] Notification permission request on Android 13+
- [ ] Deep linking from notifications

---

## 📄 License

MIT License — Free to use for personal and commercial projects.

---

*Built with ❤️ using Java, Firebase, ML Kit, and Material Design 3*
