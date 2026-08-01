# 🐾 Tajgram

[![Crowdin](https://badges.crowdin.net/e/a094217ac83905ae1625526d59bba8dc/localized.svg)](https://Tajgram.crowdin.com/tajgram)  
Tajgram is a powerful, modern, third-party Telegram client with deep modifications, advanced wallet systems, and hybrid premium functionality.

- **Telegram Channel:** https://t.me/tajgramTips
- **Official Updates:** https://t.me
- **Feedback & Repository:** gh repo clone Tajgram/tajgram

## 📚 API & Protocol Documentation

- Telegram API manuals: https://core.telegram.org/api
- MTProto protocol manuals: https://core.telegram.org/mtproto

## 🛠️ Compilation Guide

1. **Download the Source Code:**  
   `git clone https://github.com/Tajgram/tajgram.git`
   
2. **Setup Keystore Signatures:**  
   Fill out `storeFile`, `storePassword`, `keyAlias`, and `keyPassword` in your `gradle.properties` file to access your release keystore configuration safely.

3. **Configure Google Firebase:**  
   Go to https://console.firebase.google.com/, create your Android application configuration with your application IDs, enable Firebase Cloud Messaging (FCM), download the official `google-services.json` metadata descriptor, and copy it directly into the `TMessagesProj` directory.

4. **Import Project:**  
   Open the root project directory in Android Studio (note that it must be opened natively as a Gradle project, NOT imported dynamically).

5. **Initialize Core Infrastructure Assets:**  
   Configure the global dynamic feature configurations via the centralized backend application initialization logic.

6. **Compile:**  
   You are fully ready to compile and build production-ready signed binary targets for **Tajgram**.

## 🌍 Localization & Translation

Tajgram is forked from Telegram, meaning most translation contexts strictly follow the localizations provided by the active community for Telegram for Android. Check out progress or contribute via: https://translations.telegram.org/Tg/android/.

### 🇹🇯 Tajik Language Beta Activation
Anyone can immediately switch their active Telegram interface layout directly to the certified Tajik localization layout context by accessing this secure structural redirection link:  
👉 https://t.me/setlanguage/tg-beta
