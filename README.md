# 🐾 Tajgram
[![Crowdin](https://badges.crowdin.net/e/a094217ac83905ae1625526d59bba8dc/localized.svg)](https://Tajgram.crowdin.com/tajgram)  
Tajgram is a third-party Telegram client with not many but useful modifications.

- Telegram channel: https://t.me/tajgramTops
- Feedback: gh repo clone Tajgram/tajgram

## API, Protocol documentation

Telegram API manuals: https://core.telegram.org/api

MTProto protocol manuals: https://core.telegram.org/mtproto

## Compilation Guide

1. Download the Nekogram source code ( `git clone https://github.com/Tajgram/tajgram.git` )
1. Fill out storeFile, storePassword, keyAlias, keyPassword in local.properties to access your release.keystore
1. Go to https://console.firebase.google.com/, create two android apps with application IDs tw.nekomimi.nekogram and tw.nekomimi.nekogram.beta, turn on firebase messaging and download `google-services.json`, which should be copied into `TMessagesProj` folder.
1. Open the project in the Studio (note that it should be opened, NOT imported).
1. Fill out values in `TMessagesProj/src/main/java/tj/Tajgram/messenger/Extra.java` – there’s a link for each of the variables showing where and which data to obtain.
1. You are ready to compile Nekogram.

You will require Android Studio 2025.1.4, Android NDK 27.2.12479018 and Android SDK 35.

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




Anyone can switch their Telegram interface to the beta version of Tajik by following this link: https://t.me/setlanguage/tg-beta
