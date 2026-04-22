# 🐝 BumbleBeeMax

A personalized companion and device management app for Android, designed for monitoring **two personal devices** — your own phone and your wife's phone.

> ⚠️ **Consent**: Both device owners have explicitly consented to monitoring. This app is strictly for personal, consensual use on two owned devices.

---

## Features

| Feature | Description |
|---|---|
| 🔋 Battery Monitoring | Real-time percentage, charging status (AC/USB/Wireless), battery health and temperature |
| 📍 Location Tracking | Adaptive-interval GPS tracking over Wi-Fi and mobile data, with address resolution |
| 🗺️ Geofencing | Configurable zones with enter/exit push notifications |
| 📊 App Usage | Per-app duration, daily screen time, 7-day bar chart trend |
| 😊 Companion | Daily mood/habit log, reminders, activity feed, personalised suggestions |
| ☁️ Sync | Firebase Firestore for real-time cross-device sync; WorkManager hourly offline-buffer sync |
| 🔒 Security | EncryptedSharedPreferences for all secrets; TLS in transit (Firebase default) |

---

## Architecture

```
BumbleBeeMax/
├── data/
│   ├── local/          Room database (BatteryInfo, LocationInfo, AppUsageInfo,
│   │                   GeofenceZone, HabitLog, CompanionEvent)
│   ├── model/          Kotlin data classes
│   ├── remote/         FirebaseRepository – Firestore push helpers
│   └── repository/     BatteryRepository, LocationRepository, UsageRepository,
│                       GeofenceRepository, CompanionRepository
├── service/
│   ├── BatteryMonitorService    – foreground, sticky, BroadcastReceiver-based
│   ├── LocationTrackingService  – FusedLocationProvider, adaptive interval
│   ├── UsageMonitorService      – UsageStatsManager, 15-min polling
│   └── FcmService               – push notifications → CompanionEvent
├── receiver/
│   ├── BootReceiver             – restarts services after reboot
│   ├── BatteryReceiver          – wakes monitor on power events
│   └── GeofenceBroadcastReceiver
├── worker/
│   └── SyncWorker               – hourly Hilt WorkManager, retries exponentially
├── ui/
│   ├── setup/   SetupActivity – choose "My Phone" or "Wife's Phone" on first launch
│   ├── dashboard, battery, location, usage, companion, settings
└── util/
    ├── SecurePrefs   – EncryptedSharedPreferences wrapper
    ├── PermissionUtils
    └── DateUtils
```

---

## First-Time Setup

1. Install the APK on **both** phones.
2. On first launch, each phone shows the **Setup** screen.  
   - Phone 1: enter your name → tap **"This is MY Phone"**  
   - Phone 2: enter wife's name → tap **"This is Wife's Phone"**
3. Grant all requested permissions (Location, Background Location, Notifications).
4. Go to **Settings → Usage Stats** and grant the app usage access permission.
5. The app immediately starts monitoring and syncing.

---

## Firebase Setup (Required)

1. Create a project at [Firebase Console](https://console.firebase.google.com).
2. Add an Android app with package `com.bumblebeemax`.
3. Download `google-services.json` and replace `app/google-services.json`.
4. Enable **Firestore**, **Authentication** (anonymous), and **Cloud Messaging** in the console.

---

## Build

```bash
./gradlew assembleDebug
```

Requires Android Studio Hedgehog or newer.

---

## Privacy & Security

- All data is stored only on your two devices and your private Firebase project.
- Local data is encrypted at rest via `EncryptedSharedPreferences`.
- Firebase Firestore traffic is TLS-encrypted by default.
- No third-party analytics beyond Firebase.
- Geofence events and companion feed are logged locally and on Firestore only.
