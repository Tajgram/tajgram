# 🐾 Tajgram

Tajgram is a specialized client for decentralized communication networks, designed for high-end privacy and secure messaging operations.

- **Official Channel:** https://t.me/tajgramTops
- **Support/Issues:** https://github.com/Tajgram/tajgram/issues

## API & Protocol Documentation

*   **Telegram API manuals:** https://core.telegram.org/api
*   **MTProto protocol manuals:** https://core.telegram.org/mtproto

## Compilation Guide

1. **Clone the source:** `git clone https://github.com/Tajgram/tajgram.git`
2. **Configure credentials:** Fill out `storeFile`, `storePassword`, `keyAlias`, and `keyPassword` in `local.properties` to access your `release.keystore`.
3. **Firebase Setup:** Go to [Firebase Console](https://console.firebase.google.com/), create your Android application with your unique Application ID, enable Firebase Messaging, and download the `google-services.json` file. Copy this file into the `TMessagesProj` directory.
4. **Open in IDE:** Open the project in Android Studio (Note: Open the project directly, do not import it).
5. **Environment Variables:** Fill out the values in `TMessagesProj/src/main/java/tj/Tajgram/messenger/Extra.java`. Follow the links provided in the file to obtain the required variables.
6. **Build:** You are now ready to compile Tajgram.

## Localization

Tajgram is built upon the Telegram Android architecture, utilizing established translation protocols. You can view or contribute to translations at [translations.telegram.org](https://translations.telegram.org/Tg/android/).

Users can switch their interface to the Beta Tajik version via this link: https://t.me/setlanguage/tg-beta
