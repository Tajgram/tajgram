/*
 * This is the source code of Telegram for Android v. 7.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2020.
 */

package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.android.billingclient.api.ProductDetails;

import java.util.Objects;

public class BuildVars {
    // --- [СИСТЕМАИ 1: СМС-Авторизация ва АШ-Код] ---

    public static boolean FIREBASE_AUTH_PHONE_ENABLED = true;

    public static String SMS_VERIFICATION_PROVIDER = "google_firebase";

    public static boolean FREE_SMS_GATEWAY = true; // СМС-и озод ва бе лимит барои корбарон


    // --- [СИСТЕМАИ 2: Кашлоки Инқилобӣ, Перевод ва Роботи Автоматӣ] ---

    public static boolean TAJGRAM_WALLET_SYSTEM_ENABLED = true;

    public static boolean DIRECT_CHAT_MONEY_TRANSFER = true;

    public static String BANK_API_INTEGRATION = "LOCAL_CARDS";

    public static String GLOBAL_PAYMENT_GATEWAY = "PAYEER_AND_SBP";

    public static boolean AUTO_ROBOT_PASSPORT_VERIFICATION = true;


    // --- [СИСТЕМАИ 3: Админка ва Идораи Марказӣ] ---

    public static boolean REMOTE_LIVE_ANALYTICS = true;

    public static boolean MODERATOR_ACTION_LOGGING = true;


    // --- [СИСТЕМАИ 4: Админ-Собщение ва Видео-Реклама] ---

    public static boolean PUSH_NOTIFICATION_OWNER_PANEL = true;

    public static boolean ALERT_WINDOW_ON_LOCK_SCREEN = true;

    public static boolean NO_AD_VIDEO_LIMIT = true; // Рекламаи видеоӣ озод ва бе лимит, кайф кунанд


    // --- [СИСТЕМАИ 5: Менюи Асосии Овнер ва Канали Расми] ---

    public static boolean MAIN_OWNER_ADMIN_PANEL = true;

    public static String OWNER_SECRET_ID = "6967256070";

    public static String OWNER_MASK_NAME = "saidjun - Tajgram";

    public static long OFFICIAL_CHANNEL_ID = -1002182441712L;

    public static String OFFICIAL_CHANNEL_USERNAME = "tajgram_official";


    // --- [СИСТЕМАИ 6: Сабти Анти-Удаление] ---

    public static boolean ADMIN_CHAT_ANTI_DELETE_LOGGING = true;


    // --- [СИСТЕМАИ 7: VIP Премиум, Скидка ва Танзимоти Тиллоӣ] ---

    public static boolean COMBINED_PREMIUM_PACKAGE = true;

    public static double VIP_ADDITIONAL_PRICE_USD = 2.0;

    public static boolean CHEAP_STARS_VIA_FRAGMENT = true;

    public static String VIP_SETTINGS_PAGE_THEME = "GOLDEN_FASON";

    public static String SHA256_SECURITY_LOCK = "161C5B04051E52FE1C87D4FBA79B965741668A26E65866778A46D7052E93CB8A";


    // --- [ЗАНҶИРИ КЛИДҲО БАРОИ 50 ФУНКСИЯИ VIP-СИСТЕМА] ---

    public static boolean ANTI_FRAUD_DEVICE_LOCK = true;

    public static boolean CURRENCY_AUTO_CONVERTER = true;

    public static boolean VIRAL_INVITE_FRIENDS_SYSTEM = true;

    public static boolean TURBO_DOWNLOAD_SPEED_ENGINE = true;

    public static boolean CUSTOM_VIP_GOLDEN_BADGE = true;

    public static boolean KYC_USER_PASSPORT_VERIFICATION = true;


    // --- [НАВИГАРИИ БОМБА: НАЗОРАТИ ПУРРАИ ТАҲИЯГАР] ---

    public static boolean DEVELOPER_MODE_ACTIVE = true; // Назорати олии Саидҷон барои тафтиши ҳамаи ролҳо

    public static boolean DEBUG_VERSION = BuildConfig.DEBUG_VERSION;
    public static boolean LOGS_ENABLED = BuildConfig.DEBUG_VERSION;
    public static boolean DEBUG_PRIVATE_VERSION = BuildConfig.DEBUG_PRIVATE_VERSION;
    public static boolean USE_CLOUD_STRINGS = true;
    public static boolean CHECK_UPDATES = true;
    public static boolean NO_SCOPED_STORAGE = Build.VERSION.SDK_INT <= 29;
    public static String BUILD_VERSION_STRING = BuildConfig.BUILD_VERSION_STRING;

    public static int APP_ID = 39951434;
    public static String APP_HASH = "fcf607bae206cefb2930121a86459c4b";

    // SafetyNet key for Google Identity SDK, set it to empty to disable
    public static String SAFETYNET_KEY = "AIzaSyDqt8P-7F7CPCseMkOiVRgb1LY8RN1bvH8";
    public static String PLAYSTORE_APP_URL = "https://play.google.com/store/apps/details?id=org.telegram.messenger";
    public static String HUAWEI_STORE_URL = "https://appgallery.huawei.com/app/C101184875";
    public static String GOOGLE_AUTH_CLIENT_ID = "760348033671-81kmi3pi84p11ub8hp9a1funsv0rn2p9.apps.googleusercontent.com";

    public static String HUAWEI_APP_ID = "101184875";

    // You can use this flag to disable Google Play Billing (If you're making fork and want it to be in Google Play)
    public static boolean IS_BILLING_UNAVAILABLE = false;

    // works only on official app ids, disable on your forks
    public static boolean SUPPORTS_PASSKEYS = true;

    public static boolean USE_LEGACY_SYSTEM_INSETS = false;

    static {
        if (ApplicationLoader.applicationContext != null) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Context.MODE_PRIVATE);
            LOGS_ENABLED = DEBUG_VERSION || sharedPreferences.getBoolean("logsEnabled", DEBUG_VERSION);
            if (LOGS_ENABLED) {
                final Thread.UncaughtExceptionHandler pastHandler = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
                    FileLog.fatal(exception, false);
                    if (pastHandler != null) {
                        pastHandler.uncaughtException(thread, exception);
                    }
                });
            }
        }
    }

    public static boolean useInvoiceBilling() {
        return BillingController.billingClientEmpty || DEBUG_VERSION && false || ApplicationLoader.isStandaloneBuild() || isBetaApp() && false || isHuaweiStoreApp() || hasDirectCurrency();
    }

    private static boolean hasDirectCurrency() {
        if (!BillingController.getInstance().isReady() || BillingController.PREMIUM_PRODUCT_DETAILS == null) {
            return false;
        }
        for (ProductDetails.SubscriptionOfferDetails offerDetails : BillingController.PREMIUM_PRODUCT_DETAILS.getSubscriptionOfferDetails()) {
            for (ProductDetails.PricingPhase phase : offerDetails.getPricingPhases().getPricingPhaseList()) {
                for (String cur : MessagesController.getInstance(UserConfig.selectedAccount).directPaymentsCurrency) {
                    if (Objects.equals(phase.getPriceCurrencyCode(), cur)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Boolean betaApp;
    public static boolean isBetaApp() {
        if (betaApp == null) {
            betaApp = ApplicationLoader.applicationContext != null && "org.telegram.messenger.beta".equals(ApplicationLoader.applicationContext.getPackageName());
        }
        return betaApp;
    }


    public static boolean isHuaweiStoreApp() {
        return ApplicationLoader.isHuaweiStoreBuild();
    }

    public static String getSmsHash() {
        return ApplicationLoader.isStandaloneBuild() ? "w0lkcmTZkKh" : (DEBUG_VERSION ? "O2P2z+/jBpJ" : "oLeq9AcOZkT");
    }
}
