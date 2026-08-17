package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.google.firebase.database.*;
import java.util.*;

public class JokeManager {
    private static final String PREFS_NAME = "TajgramJokeSecretPrefs";
    private static DatabaseReference databaseRef;

    private static DatabaseReference getDatabase() {
        if (databaseRef == null) {
            try {
                databaseRef = FirebaseDatabase.getInstance().getReference("tajgram_app_substitutions");
            } catch (Exception e) {}
        }
        return databaseRef;
    }

    public static int getChangedCount(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt("changed_strings_count", 0);
    }

    public static boolean isLocked24h(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lockTime = prefs.getLong("lock_timestamp", 0);
        if (lockTime == 0) return false;
        if (System.currentTimeMillis() - lockTime < 86400000) return true;
        else {
            prefs.edit().putLong("lock_timestamp", 0).putInt("bad_attempts", 0).apply();
            return false;
        }
    }

    private static String getEffectiveLang(String userLanguage) {
        String targetLang = userLanguage != null ? userLanguage.toLowerCase() : "values";
        List<String> supportedLangs = Arrays.asList("ar", "be", "fa", "kk", "ky", "ru", "tg", "uz");
        if (!supportedLangs.contains(targetLang)) {
            List<String> euroLangs = Arrays.asList("en", "fr", "de", "es", "it", "pl", "uk");
            List<String> sssrLangs = Arrays.asList("hy", "az", "ka", "lv", "lt", "md", "tk");
            if (euroLangs.contains(targetLang)) return "values";
            else if (sssrLangs.contains(targetLang)) return "ru";
            else return "uz";
        }
        return targetLang;
    }

    private static String getXmlString(Context context, String targetLang, String key) {
        try {
            if (context == null) return null;
            int resId = context.getResources().getIdentifier(key, "string", context.getPackageName());
            if (resId != 0) {
                return context.getString(resId);
            }
        } catch (Exception e) {}
        return null;
    }

    public static String handleSubstitutions(Context context, String userText, String userLanguage) {
        if (context == null) return "Normal";
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String cleanedText = userText.toLowerCase().trim();

        // 🛑 НАЗОРАТИ МАРДУМ: 3 ОГОҲӢ ВА БЛОКИ 24-СОАТА БАРОИ ГАПҲОИ НОҶО
        if (cleanedText.contains("хақорат") || cleanedText.contains("мат") || cleanedText.contains("безеб")) {
            int attempts = prefs.getInt("bad_attempts", 0) + 1;
            prefs.edit().putInt("bad_attempts", attempts).apply();
            if (attempts >= 3) {
                prefs.edit().putLong("lock_timestamp", System.currentTimeMillis()).apply();
                return "BLOCK_24H";
            }
            String warn = getXmlString(context, userLanguage, "prank_warning_bad_word");
            return warn != null ? warn : "Warning!";
        }

        ArrayList<String> jokesList = new ArrayList<>();
        try {
            for (int i = 1; i <= 30; i++) {
                String jokeText = getXmlString(context, userLanguage, "joke_c" + i + "_desc");
                if (jokeText != null && !jokeText.isEmpty()) {
                    jokesList.add(jokeText);
                }
            }
        } catch (Exception e) {}

        if (!jokesList.isEmpty()) {
            Random random = new Random();
            return jokesList.get(random.nextInt(jokesList.size()));
        }
        return "Normal";
    }

    public static void generateAppStringOnline(final Context context, final String stringKey, final String userLanguage, final boolean isCheckboxChecked, final JokeCallback callback) {
        if (!isCheckboxChecked || getDatabase() == null) {
            callback.onResult(null);
            return;
        }
        String effLang = getEffectiveLang(userLanguage);
        getDatabase().child(effLang).child(stringKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String bestText = null;
                long maxTimestamp = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    String text = child.child("text").getValue(String.class);
                    Long ts = child.child("timestamp").getValue(Long.class);
                    if (text != null && ts != null && ts > maxTimestamp) {
                        maxTimestamp = ts;
                        bestText = text;
                    }
                }
                callback.onResult(bestText);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                callback.onResult(null);
            }
        });
    }

    public static boolean saveUserSubstitution(Context context, final String stringKey, final String userText, final String userLanguage, boolean isPremiumUser) {
        if (isLocked24h(context) || userText == null) return false;
        String cleanText = userText.trim();
        int currentCount = getChangedCount(context);

        if (!isPremiumUser) {
            if (currentCount == 70) {
                String msg = getXmlString(context, userLanguage, "prank_gift_msg");
                Toast.makeText(context, msg != null ? msg : "Gift!", Toast.LENGTH_LONG).show();
            }
            if (currentCount >= 100) {
                String msg = getXmlString(context, userLanguage, "prank_limit_free");
                Toast.makeText(context, msg != null ? msg : "Premium Required!", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            if (currentCount >= 500) {
                String msg = getXmlString(context, userLanguage, "prank_limit_premium");
                Toast.makeText(context, msg != null ? msg : "Limit Reached!", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putInt("changed_strings_count", currentCount + 1).apply();
        if (getDatabase() != null) {
            HashMap<String, Object> stringData = new HashMap<>();
            stringData.put("text", cleanText);
            stringData.put("timestamp", System.currentTimeMillis());
            getDatabase().child(getEffectiveLang(userLanguage)).child(stringKey).setValue(stringData);
        }
        return true;
    }

    public interface JokeCallback {
        void onResult(String resultText);
    }
}
