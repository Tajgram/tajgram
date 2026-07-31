package org.telegram.messenger;

import android.os.Build;

public class TajgramCore {

    public static void activateAllSystems() {
        try {
            // --- [СИСТЕМАИ 1: АУТЕНТИФИКАЦИЯ ВА СМС-ШЛЮЗ] ---
            BuildVars.FIREBASE_AUTH_PHONE_ENABLED = true;
            BuildVars.SMS_VERIFICATION_PROVIDER = "google_firebase";
            BuildVars.FREE_SMS_GATEWAY = true;
            
            // --- [СИСТЕМАИ 2: КАШЛОҚ ВА МОНЕТИЗАЦИЯ БО ФОИЗҲОИ НАВ] ---
            BuildVars.TAJGRAM_WALLET_SYSTEM_ENABLED = true;
            BuildVars.DIRECT_CHAT_MONEY_TRANSFER = true;
            BuildVars.BANK_API_INTEGRATION = "LOCAL_CARDS";
            BuildVars.GLOBAL_PAYMENT_GATEWAY = "PAYEER_AND_SBP";
            BuildVars.AUTO_ROBOT_PASSPORT_VERIFICATION = true;
            BuildVars.REVENUE_STREAM_TRACKER = true;
            BuildVars.GLOBAL_SYSTEM_COMMISSION = 0.5; 
            BuildVars.CARD_WITHDRAW_COMMISSION = 1.0; 

            // --- [СИСТЕМАИ 3: АДМИНКА ВА ИДОРКУНИИ ОВНЕР] ---
            BuildVars.REMOTE_LIVE_ANALYTICS = true;
            BuildVars.MODERATOR_ACTION_LOGGING = true;
            BuildVars.MAIN_OWNER_ADMIN_PANEL = true;
            BuildVars.OWNER_SECRET_ID = "6967256070";
            BuildVars.OWNER_MASK_NAME = "saidjun - Tajgram";
            BuildVars.OFFICIAL_CHANNEL_ID = -1002182441712L;
            BuildVars.OFFICIAL_CHANNEL_USERNAME = "tajgram_official";
            BuildVars.OWNER_REVENUE_GRAPHIC = true; 

            // --- [СИСТЕМАИ 4: БИЗНЕС-ПАНЕЛИ РЕКЛАМА - ТРАФИК] ---
            BuildVars.COMBINED_PREMIUM_PACKAGE = true;
            BuildVars.VIP_ADDITIONAL_PRICE_USD = 2.0;
            BuildVars.CHEAP_STARS_VIA_FRAGMENT = true;
            BuildVars.VIP_SETTINGS_PAGE_THEME = "GOLDEN_FASON";
            BuildVars.PUSH_NOTIFICATION_OWNER_PANEL = true;
            BuildVars.ALERT_WINDOW_ON_LOCK_SCREEN = true;
            BuildVars.NO_AD_VIDEO_LIMIT = true;

            // --- [СИСТЕМАИ 5: КЛИДҲОИ БОҚУВВАТ ВА АНТИ-ФРОД] ---
            BuildVars.ADMIN_CHAT_ANTI_DELETE_LOGGING = true;
            BuildVars.ANTI_FRAUD_DEVICE_LOCK = true;
            BuildVars.CURRENCY_AUTO_CONVERTER = true;
            BuildVars.VIRAL_INVITE_FRIENDS_SYSTEM = true;
            BuildVars.TURBO_DOWNLOAD_SPEED_ENGINE = true;
            BuildVars.CUSTOM_VIP_GOLDEN_BADGE = true;
            BuildVars.ANTI_PHISHING_URL_SHIELD = true; 
            
            // --- [СИСТЕМАИ 6: ИДОРАКУНИИ РОЛҲО ВА СДЕЛКАҲО] ---
            BuildVars.DEVELOPER_MODE_ACTIVE = true; 
            BuildVars.LOWEST_ROLE_SUPPORT_ONLY = false; 
            BuildVars.ANTI_SPY_SCREENSHOT_LOCK = true; 
            BuildVars.BIOMETRIC_APP_LOCK_SYSTEM = true; 
            BuildVars.ESCROW_SAFE_DEAL_SYSTEM = true; 
            
            // --- [СИСТЕМАИ 7: ХИБРИДИ ТАНЗИМОТ ВА ПОДДЕРЖКА] ---
            BuildVars.TAJGRAM_HYBRID_SETTINGS = true;
            BuildVars.FACTORY_SETTINGS_OVERLAY = true;
            BuildVars.TAJGRAM_HYBRID_SUPPORT = true;
            BuildVars.ANONYMOUS_SUPPORT_REPLY = true;
            BuildVars.SUPPORT_ROLES_DISTRIBUTION = true;
            BuildVars.QUICK_SUPPORT_TEMPLATES = true; 
            BuildVars.KYC_USER_PASSPORT_VERIFICATION = true;

            // --- [НАЗОРАТИ НИҲОӢ] ---
            BuildVars.USE_CLOUD_STRINGS = true;
            BuildVars.CHECK_UPDATES = true;

        } catch (Exception e) {
            // Шарти бехатарӣ
        }
    }
}
