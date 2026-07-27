package tj.Tajgram.messenger;

import org.telegram.messenger.BuildConfig;

public class Extra {
    public static int APP_ID = BuildConfig.APP_ID;
    public static String APP_HASH = BuildConfig.APP_HASH;
    public static String SHA256 = BuildConfig.SHA256;
    public static String CRYPTO_BOT_TOKEN = BuildConfig.CRYPTO_BOT_TOKEN;

    // Нишон додани намуди билд (Debug ё Release) барои ядрои барнома
    public static boolean isDirectApp() {
        return "release".equals(BuildConfig.BUILD_TYPE) || "debug".equals(BuildConfig.BUILD_TYPE);
    }

    // Санҷиши аслияти калид
    public static boolean isKeyValid() {
        return APP_ID != 0 && APP_HASH != null && !APP_HASH.isEmpty();
    }
}
