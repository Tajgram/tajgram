package tj.Tajgram.messenger; // Суроғаи папкаи миллии нави ту

import org.telegram.messenger.regular.BuildConfig; // Импорти мустақими Грэдли ту барои хондани калидҳо

public class Extra {

    // Калидҳои асосии амниятӣ, ки аз GitHub Secrets ва local.properties меоянд
    public static int APP_ID = BuildConfig.API_ID;
    public static String APP_HASH = BuildConfig.API_HASH;
    public static String APP_SHA256 = BuildConfig.SHA_256;

    // Нишон додани намуди билд (Debug ё Release) барои ядрои барнома
    public static boolean isDirectApp() {
        return "release".equals(BuildConfig.BUILD_TYPE) || "debug".equals(BuildConfig.BUILD_TYPE);
    }
}
