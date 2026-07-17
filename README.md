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

## Localization

Tajgram is forked from Telegram, thus most locales follows the translations of Telegram for Android, checkout https://translations.telegram.org/Tg/android/.




Anyone can switch their Telegram interface to the beta version of Tajik by following this link: https://t.me/setlanguage/tg-beta
