package org.telegram.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.JokeManager;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextSettingsCell;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VipSettingsActivity extends BaseFragment {

    private static final long OWNER_SECRET_ID = 6967256070L;
    private static final String CHANNEL_USERNAME = "TajgramTips";

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("VipSettingsTitle", R.string.VipSettingsTitle));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    showDynamicJokeDialog(context, "ExitDialogTitle", "ExitDialogDesc");
                    finishFragment();
                }
            }
        });

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        long currentUserId = UserConfig.getInstance(currentAccount).getClientUserId();
        SharedPreferences prefs = MessagesController.getMainSettings(UserConfig.selectedAccount);

        // === 1. ИД-И КОРБАР (БО ЯК ПАХШ НУСХАБАРДОРӢ) ===
        TextSettingsCell userIdCell = new TextSettingsCell(context);
        userIdCell.setTextAndValue("ID-и шумо:", String.valueOf(currentUserId), true);
        userIdCell.setOnClickListener(v -> {
            AndroidUtilities.addToClipboard(String.valueOf(currentUserId));
            Toast.makeText(context, "ID ба буфер нусха шуд!", Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(userIdCell);

        // === 2. РАВЗАНАИ ХУШ ОМАДЕД / БОЗГАШТ ===
        boolean isReentry = prefs.getBoolean("tajgram_visited_vip", false);
        if (!isReentry) {
            showDynamicJokeDialog(context, "WelcomeBellTitle", "WelcomeBellDesc");
            prefs.edit().putBoolean("tajgram_visited_vip", true).apply();
        } else {
            showDynamicJokeDialog(context, "WelcomeBellTitle_Reentry", "WelcomeBellDesc_Reentry");
        }

        // === 3. ФАЪОЛСОЗИИ РЕҶАИ ЧОКЕР ===
        TextCheckCell jokeModeCell = new TextCheckCell(context);
        boolean isJokeEnabled = prefs.getBoolean("tajgram_joke_mode", false);
        jokeModeCell.setTextAndCheck(
            LocaleController.getString("VipSettingsMenuTitle", R.string.VipSettingsMenuTitle),
            isJokeEnabled,
            true
        );
        jokeModeCell.setOnClickListener(v -> {
            boolean nextState = !prefs.getBoolean("tajgram_joke_mode", false);
            prefs.edit().putBoolean("tajgram_joke_mode", nextState).apply();
            jokeModeCell.setChecked(nextState);

            String currentLang = LocaleController.getInstance().getCurrentLocaleInfo().shortName;
            JokeManager.saveUserSubstitution(
                context,
                "welcome_prank",
                LocaleController.getString("prank_instruction_title", R.string.prank_instruction_title),
                currentLang,
                nextState
            );

            LocaleController.getInstance().recreateStringMaps();
            showDynamicJokeDialog(context, "prank_instruction_title", "prank_instruction_desc");
        });
        linearLayout.addView(jokeModeCell);

        // === 4. ПАНЕЛИ АДМИН (ТАНҲО БАРОИ ОВНЕР) ===
        if (currentUserId == OWNER_SECRET_ID) {
            TextSettingsCell secretSupportCell = new TextSettingsCell(context);
            secretSupportCell.setTextAndValue(
                LocaleController.getString("SecretSupportButtonLabel", R.string.SecretSupportButtonLabel),
                LocaleController.getString("AskAQuestion_Owner", R.string.AskAQuestion_Owner),
                true
            );
            secretSupportCell.setOnClickListener(v -> {
                AlertDialog.Builder adminBuilder = new AlertDialog.Builder(context);
                adminBuilder.setTitle(LocaleController.getString("SecretSupportButtonLabel", R.string.SecretSupportButtonLabel));

                CharSequence[] adminOptions = new CharSequence[]{
                    LocaleController.getString("VipSettingsTitle", R.string.VipSettingsTitle),
                    LocaleController.getString("AskAQuestion_Owner", R.string.AskAQuestion_Owner),
                    LocaleController.getString("VipSupportBot", R.string.VipSupportBot)
                };

                adminBuilder.setItems(adminOptions, (dialog, which) -> {
                    AlertDialog.Builder inputBuilder = new AlertDialog.Builder(context);
                    final EditText input = new EditText(context);
                    inputBuilder.setView(input);

                    inputBuilder.setPositiveButton(LocaleController.getString("prank_accept_btn", R.string.prank_accept_btn), (d, w) -> {
                        String idStr = input.getText().toString().trim();
                        if (!idStr.isEmpty()) {
                            long targetUid = Long.parseLong(idStr);
                            SharedPreferences.Editor editor = prefs.edit();
                            if (which == 0) {
                                editor.putBoolean("taj_mod_premium_" + targetUid, true);
                                editor.putBoolean("taj_mod_verified_" + targetUid, true);
                            } else if (which == 1) {
                                editor.putBoolean("taj_user_just_premium_" + targetUid, true);
                            } else if (which == 2) {
                                editor.putBoolean("taj_user_banned_" + targetUid, true);
                                editor.putBoolean("taj_mod_premium_" + targetUid, false);
                            }
                            editor.apply();
                        }
                    });
                    inputBuilder.show();
                });
                adminBuilder.show();
            });
            linearLayout.addView(secretSupportCell);
        }

        // === 5. ДИАЛОГИ ПУШТИБОНИИ ФАННӢ ===
        TextSettingsCell techSupportCell = new TextSettingsCell(context);
        techSupportCell.setTextAndValue(
            LocaleController.getString("AskAQuestion", R.string.AskAQuestion),
            LocaleController.getString("VipSupportBot", R.string.VipSupportBot),
            true
        );
        techSupportCell.setOnClickListener(v -> {
            AlertDialog.Builder liveDialogBuilder = new AlertDialog.Builder(context);
            liveDialogBuilder.setTitle(LocaleController.getString("AskAQuestion", R.string.AskAQuestion));

            final EditText voiceAndTextInput = new EditText(context);
            voiceAndTextInput.setHint(LocaleController.getString("prank_btn_cool", R.string.prank_btn_cool));
            liveDialogBuilder.setView(voiceAndTextInput);

            liveDialogBuilder.setPositiveButton(LocaleController.getString("prank_accept_btn", R.string.prank_accept_btn), (dialog, which) -> {
                String userMsg = voiceAndTextInput.getText().toString().trim();
                if (!userMsg.isEmpty()) {
                    showDynamicJokeDialog(context, "joke_c4_title", "joke_c4_desc");
                }
            });
            liveDialogBuilder.setNegativeButton("✕", null);
            liveDialogBuilder.show();
        });
        linearLayout.addView(techSupportCell);

        // === 6. ХОНДАНИ 25+ КАТЕГОРИЯҲО АЗ XML БЕ МАҲДУДИЯТ ===
        for (int i = 1; i <= 35; i++) {
            String titleKey = "joke_c" + i + "_title";
            String descKey = "joke_c" + i + "_desc";

            int titleResId = context.getResources().getIdentifier(titleKey, "string", context.getPackageName());
            int descResId = context.getResources().getIdentifier(descKey, "string", context.getPackageName());

            if (titleResId != 0 && descResId != 0) {
                TextSettingsCell jokeCategoryCell = new TextSettingsCell(context);
                jokeCategoryCell.setTextAndValue(
                    LocaleController.getString(titleKey, titleResId),
                    LocaleController.getString(descKey, descResId),
                    true
                );
                jokeCategoryCell.setOnClickListener(v -> showDynamicJokeDialog(context, titleKey, descKey));
                linearLayout.addView(jokeCategoryCell);
            }
        }

        // === 7. ЛАЙК ВА ДИЗЛАЙКИ ЗИНДА (ЗАРБАИ РЕАКСИЯ БА КАНАЛ) ===
        TextSettingsCell likeCell = new TextSettingsCell(context);
        likeCell.setTextAndValue(LocaleController.getString("LikeButton", R.string.LikeButton), "", true);
        likeCell.setOnClickListener(v -> {
            int likes = prefs.getInt("tajgram_global_likes", 0) + 1;
            prefs.edit().putInt("tajgram_global_likes", likes).apply();
            
            // Фиристодани реаксияи воқеии 👍 ба Telegram
            sendLiveTelegramReaction(CHANNEL_USERNAME, "👍");
            Toast.makeText(context, "👍 Лайк фиристода шуд! Ҳамагӣ: " + likes, Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(likeCell);

        TextSettingsCell dislikeCell = new TextSettingsCell(context);
        dislikeCell.setTextAndValue(LocaleController.getString("DislikeButton", R.string.DislikeButton), "", true);
        dislikeCell.setOnClickListener(v -> {
            int dislikes = prefs.getInt("tajgram_global_dislikes", 0) + 1;
            prefs.edit().putInt("tajgram_global_dislikes", dislikes).apply();
            
            // Фиристодани реаксияи воқеии 👎 ба Telegram
            sendLiveTelegramReaction(CHANNEL_USERNAME, "👎");
            Toast.makeText(context, "👎 Дизлайк фиристода шуд! Ҳамагӣ: " + dislikes, Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(dislikeCell);

        // === 8. КУРСИ АСЪОРИ ЗИНДА (АЙ ИНТЕРНЕТ АПДЕЙТ МЕШАВАД) ===
        TextSettingsCell currencyCell = new TextSettingsCell(context);
        currencyCell.setTextAndValue(
            LocaleController.getString("TajgramBellCurrencyTitle", R.string.TajgramBellCurrencyTitle),
            "Барои дидани курси асъори зинда пахш кунед",
            true
        );
        currencyCell.setOnClickListener(v -> fetchLiveCurrencyRates(context));
        linearLayout.addView(currencyCell);

        // === 9. ОВОЗИ ДУХТАР ВА БАЧА (ОЗВУЧКАИ ЗИНДА) ===
        TextCheckCell changeVoiceOnSendCell = new TextCheckCell(context);
        boolean isSendVoiceEnabled = prefs.getBoolean("tajgram_enable_voice_change_send", false);
        changeVoiceOnSendCell.setTextAndCheck(LocaleController.getString("VoiceSelection", R.string.VoiceSelection), isSendVoiceEnabled, true);
        changeVoiceOnSendCell.setOnClickListener(v -> {
            boolean current = prefs.getBoolean("tajgram_enable_voice_change_send", false);
            prefs.edit().putBoolean("tajgram_enable_voice_change_send", !current).apply();
            changeVoiceOnSendCell.setChecked(!current);
            MediaController.getInstance().setVoiceChangeMode(prefs.getInt("tajgram_voice_type", 1));
        });
        linearLayout.addView(changeVoiceOnSendCell);

        TextCheckCell readMessageVoiceCell = new TextCheckCell(context);
        boolean isReadVoiceEnabled = prefs.getBoolean("tajgram_enable_voice_read_msg", false);
        readMessageVoiceCell.setTextAndCheck(LocaleController.getString("VoiceSelection", R.string.VoiceSelection), isReadVoiceEnabled, true);
        readMessageVoiceCell.setOnClickListener(v -> {
            boolean current = prefs.getBoolean("tajgram_enable_voice_read_msg", false);
            prefs.edit().putBoolean("tajgram_enable_voice_read_msg", !current).apply();
            readMessageVoiceCell.setChecked(!current);
            MediaController.getInstance().setTtsEnabled(!current);
        });
        linearLayout.addView(readMessageVoiceCell);

        // Танзими динамикии овози Духтар
        TextSettingsCell girlVoiceCell = new TextSettingsCell(context);
        girlVoiceCell.setTextAndValue(LocaleController.getString("VoiceIcon_Girl", R.string.VoiceIcon_Girl), "Овози майин (Духтарона)", true);
        girlVoiceCell.setOnClickListener(v -> {
            prefs.edit().putInt("tajgram_voice_type", 1).apply();
            MediaController.getInstance().setVoicePitchAndSpeed(1.35f, 1.05f); // Тони баланд ва майин
            Toast.makeText(context, "👧 Овози духтарона фаъол шуд!", Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(girlVoiceCell);

        // Танзими динамикии овози Бача
        TextSettingsCell boyVoiceCell = new TextSettingsCell(context);
        boyVoiceCell.setTextAndValue(LocaleController.getString("VoiceIcon_Boy", R.string.VoiceIcon_Boy), "Овози баси паст (Писарона)", true);
        boyVoiceCell.setOnClickListener(v -> {
            prefs.edit().putInt("tajgram_voice_type", 2).apply();
            MediaController.getInstance().setVoicePitchAndSpeed(0.80f, 0.95f); // Тони пасти бас
            Toast.makeText(context, "👦 Овози писарона фаъол шуд!", Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(boyVoiceCell);

        // === 10. ДАЪВАТИ ДӮСТОН ===
        TextSettingsCell inviteCell = new TextSettingsCell(context);
        inviteCell.setTextAndValue(LocaleController.getString("Folk_InviteFriends", R.string.Folk_InviteFriends), "", false);
        inviteCell.setOnClickListener(v -> {
            String shareText = "https://t.me/" + CHANNEL_USERNAME;
            AndroidUtilities.addToClipboard(shareText);
            Toast.makeText(context, "Линки даъват ба буфер нусха шуд!", Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(inviteCell);

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        fragmentView = scrollView;
        return fragmentView;
    }

    // === ФУНКСИЯИ ХОНДАНИ КУРСИ АСЪОРИ ЗИНДА АЗ ИНТЕРНЕТ ===
    private void fetchLiveCurrencyRates(Context context) {
        Toast.makeText(context, "Лутфан сабр кунед, курси асъор боргирӣ шуда истодааст...", Toast.LENGTH_SHORT).show();
        
        Utilities.globalQueue.postRunnable(() -> {
            try {
                URL url = new URL("https://open.er-api.com/v6/latest/USD");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONObject rates = json.getJSONObject("rates");

                double usdToTjs = rates.optDouble("TJS", 10.90);
                double usdToRub = rates.optDouble("RUB", 90.00);
                double usdToEur = rates.optDouble("EUR", 0.92);

                double rubToTjs = usdToTjs / usdToRub;

                String resultText = String.format(
                    "💵 1 USD = %.2f TJS\n💶 1 EUR = %.2f TJS\n₽ 1000 RUB = %.2f TJS",
                    usdToTjs, (usdToTjs / usdToEur), (rubToTjs * 1000)
                );

                AndroidUtilities.runOnUIThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("📊 Курси асъори зинда (Real-Time)");
                    builder.setMessage(resultText);
                    builder.setPositiveButton("OK", null);
                    builder.show();
                });

            } catch (Exception e) {
                AndroidUtilities.runOnUIThread(() -> {
                    Toast.makeText(context, "Хатогӣ дар пайвастшавӣ ба интернет!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // === ФУНКСИЯИ ФИРИСТОДАНИ РЕАКСИЯИ ЗИНДА БА TELEGRAM ===
    private void sendLiveTelegramReaction(String channel, String emoji) {
        TLRPC.TL_messages_sendReaction req = new TLRPC.TL_messages_sendReaction();
        req.peer = MessagesController.getInstance(currentAccount).getInputPeer(channel);
        TLRPC.TL_reactionEmoji reaction = new TLRPC.TL_reactionEmoji();
        reaction.emoticon = emoji;
        req.reaction.add(reaction);

        ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> {
            // Реаксия ба сервер фиристода шуд
        });
    }

    // === ОКНОИ ДИНАМИКИИ XML БО ТУГМАИ "✕" ===
    private void showDynamicJokeDialog(Context context, String titleKey, String descKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        
        int titleResId = context.getResources().getIdentifier(titleKey, "string", context.getPackageName());
        int descResId = context.getResources().getIdentifier(descKey, "string", context.getPackageName());

        builder.setTitle(LocaleController.getString(titleKey, titleResId != 0 ? titleResId : R.string.VipSettingsTitle));
        builder.setMessage(LocaleController.getString(descKey, descResId != 0 ? descResId : R.string.VipSettingsMenuTitle));
        
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("✕", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
