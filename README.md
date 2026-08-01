# 🐾 Tajgram

[![Crowdin](https://badges.crowdin.net/e/a094217ac83905ae1625526d59bba8dc/localized.svg)](https://Tajgram.crowdin.com/tajgram)  
Tajgram is a powerful, modern, third-party Telegram client with deep modifications, advanced wallet systems, and hybrid premium functionality.

- **Telegram Channel:** https://t.me/tajgramTops
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

You will require Android Studio 2025.1.4, Android NDK 27.2.12479018 and Android SDK 36.

1. Clone the Telegram source code with its submodules:
   ```bash
   git clone --recursive --shallow-submodules https://github.com/DrKLO/Telegram.git Telegram
   ```
   In case you forgot the `--recursive` flag, change to the `Telegram` directory and run:
   ```bash
   git submodule init && git submodule update --init --recursive --depth=1
   ```
2. Copy your release.keystore into TMessagesProj/config
3. Fill out RELEASE_KEY_PASSWORD, RELEASE_KEY_ALIAS, RELEASE_STORE_PASSWORD in gradle.properties to access your  release.keystore
4.  Go to https://console.firebase.google.com/, create two android apps with application IDs org.telegram.messenger and org.telegram.messenger.beta, turn on firebase messaging and download google-services.json, which should be copied to the same folder as TMessagesProj.
5. Open the project in the Studio (note that it should be opened, NOT imported).
6. Fill out values in TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java – there’s a link for each of the variables showing where and which data to obtain.
7. You are ready to compile Telegram.

6. **Compile:**  
   You are fully ready to compile and build production-ready signed binary targets for **Tajgram**.

## 🌍 Localization & Translation

Tajgram is forked from Telegram, meaning most translation contexts strictly follow the localizations provided by the active community for Telegram for Android. Check out progress or contribute via: https://translations.telegram.org/Tg/android/.

### 🇹🇯 Tajik Language Beta Activation
Anyone can immediately switch their active Telegram interface layout directly to the certified Tajik localization layout context by accessing this secure structural redirection link:  
👉 https://t.me/setlanguage/tg-beta
