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
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
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
    private static final String SUPPORT_BOT_USERNAME = "TajgramSupportBot";

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("VipSettingsTitle", R.string.VipSettingsTitle));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(LocaleController.getString("ExitDialogTitle", R.string.ExitDialogTitle));
                    builder.setMessage(LocaleController.getString("ExitDialogDesc", R.string.ExitDialogDesc));
                    builder.setPositiveButton(LocaleController.getString("ExitDialogBtnLeave", R.string.ExitDialogBtnLeave), (dialog, which) -> finishFragment());
                    builder.setNegativeButton(LocaleController.getString("ExitDialogBtnStay", R.string.ExitDialogBtnStay), null);
                    builder.show();
                }
            }
        });

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        long currentUserId = UserConfig.getInstance(currentAccount).getClientUserId();
        SharedPreferences prefs = MessagesController.getMainSettings(UserConfig.selectedAccount);

        // 1. Хуш омадед ва пешниҳоди канал (WelcomeChannelSuggest)
        boolean isReentry = prefs.getBoolean("tajgram_visited_vip", false);
        if (!isReentry) {
            String welcomeFullMsg = LocaleController.getString("WelcomeBellDesc", R.string.WelcomeBellDesc) + "\n\n" +
                                    LocaleController.getString("WelcomeChannelSuggest", R.string.WelcomeChannelSuggest);
            showCustomMessageDialog(context, LocaleController.getString("WelcomeBellTitle", R.string.WelcomeBellTitle), welcomeFullMsg);
            prefs.edit().putBoolean("tajgram_visited_vip", true).apply();
        } else {
            showDynamicJokeDialog(context, "WelcomeBellTitle_Reentry", "WelcomeBellDesc_Reentry");
        }

        // 2. ID-и корбар
        TextSettingsCell userIdCell = new TextSettingsCell(context);
        userIdCell.setTextAndValue(
            LocaleController.getString("UserIdLabel", R.string.UserIdLabel), 
            String.valueOf(currentUserId), 
            true
        );
        userIdCell.setOnClickListener(v -> {
            AndroidUtilities.addToClipboard(String.valueOf(currentUserId));
            Toast.makeText(context, LocaleController.getString("UserIdCopied", R.string.UserIdCopied), Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(userIdCell);

        // 3. Идораи Премиум
        TextCheckCell premiumControlCell = new TextCheckCell(context);
        boolean isPremiumEnabled = prefs.getBoolean("tajgram_custom_premium_active", true);
        premiumControlCell.setTextAndCheck(
            LocaleController.getString("TajgramPremiumControl", R.string.TajgramPremiumControl), 
            isPremiumEnabled, 
            true
        );
        premiumControlCell.setOnClickListener(v -> {
            boolean nextState = !prefs.getBoolean("tajgram_custom_premium_active", true);
            prefs.edit().putBoolean("tajgram_custom_premium_active", nextState).apply();
            premiumControlCell.setChecked(nextState);
            Toast.makeText(context, LocaleController.getString("TajgramPremiumChanged", R.string.TajgramPremiumChanged), Toast.LENGTH_LONG).show();
        });
        linearLayout.addView(premiumControlCell);

        // 4. Танзимоти Tajgram (VipSettingsMenuTitle)
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
            LocaleController.getInstance().checkCurrentLocale();
            showDynamicJokeDialog(context, "prank_instruction_title", "prank_instruction_desc");
        });
        linearLayout.addView(jokeModeCell);

        // 5. Админ панел (SecretSupportButtonLabel ва AskAQuestion_Owner)
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

        // 6. Пуштибонии фаннӣ (AskAQuestion ва VipSupportBot)
        TextSettingsCell techSupportCell = new TextSettingsCell(context);
        techSupportCell.setTextAndValue(
            LocaleController.getString("AskAQuestion", R.string.AskAQuestion),
            LocaleController.getString("VipSupportBot", R.string.VipSupportBot),
            true
        );
        techSupportCell.setOnClickListener(v -> {
            Browser.openUrl(context, "https://t.me/" + SUPPORT_BOT_USERNAME);
        });
        linearLayout.addView(techSupportCell);

        // 7. Усули халқӣ (TajgramFolkModeSwitch ва TajgramFolkModeDesc)
        TextCheckCell folkModeCell = new TextCheckCell(context);
        boolean isFolkActive = prefs.getBoolean("tajgram_folk_mode_active", false);
        folkModeCell.setTextAndCheck(
            LocaleController.getString("TajgramFolkModeSwitch", R.string.TajgramFolkModeSwitch),
            isFolkActive,
            true
        );
        folkModeCell.setOnClickListener(v -> {
            boolean nextState = !prefs.getBoolean("tajgram_folk_mode_active", false);
            prefs.edit().putBoolean("tajgram_folk_mode_active", nextState).apply();
            folkModeCell.setChecked(nextState);
            Toast.makeText(context, LocaleController.getString("TajgramFolkModeDesc", R.string.TajgramFolkModeDesc), Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(folkModeCell);

        // 8. Таъмири барнома ва амният (Folk_Settings ва Folk_Security)
        TextSettingsCell folkSettingsCell = new TextSettingsCell(context);
        folkSettingsCell.setTextAndValue(
            LocaleController.getString("Folk_Settings", R.string.Folk_Settings),
            LocaleController.getString("TajgramFolkModeDesc", R.string.TajgramFolkModeDesc),
            true
        );
        folkSettingsCell.setOnClickListener(v -> Toast.makeText(context, LocaleController.getString("Folk_Settings", R.string.Folk_Settings), Toast.LENGTH_SHORT).show());
        linearLayout.addView(folkSettingsCell);

        TextSettingsCell folkSecurityCell = new TextSettingsCell(context);
        folkSecurityCell.setTextAndValue(
            LocaleController.getString("Folk_Security", R.string.Folk_Security),
            "",
            true
        );
        folkSecurityCell.setOnClickListener(v -> Toast.makeText(context, LocaleController.getString("Folk_Security", R.string.Folk_Security), Toast.LENGTH_SHORT).show());
        linearLayout.addView(folkSecurityCell);

        // 9. Категорияҳои динамикӣ аз XML
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

        // 10. Лайк ва Дизлайк (LikeButton ва DislikeButton) бо ҳисобкунаки рақамӣ ва тугмаи такрорӣ (Toggle)
        TextSettingsCell likeCell = new TextSettingsCell(context);
        TextSettingsCell dislikeCell = new TextSettingsCell(context);

        Runnable updateLikeDislikeUI = () -> {
            int likes = prefs.getInt("tajgram_global_likes", 0);
            int dislikes = prefs.getInt("tajgram_global_dislikes", 0);
            boolean hasLiked = prefs.getBoolean("tajgram_user_liked", false);
            boolean hasDisliked = prefs.getBoolean("tajgram_user_disliked", false);

            likeCell.setTextAndValue(
                LocaleController.getString("LikeButton", R.string.LikeButton),
                (hasLiked ? "👍 " : "") + likes,
                true
            );
            dislikeCell.setTextAndValue(
                LocaleController.getString("DislikeButton", R.string.DislikeButton),
                (hasDisliked ? "👎 " : "") + dislikes,
                true
            );
        };

        likeCell.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            int likes = prefs.getInt("tajgram_global_likes", 0);
            int dislikes = prefs.getInt("tajgram_global_dislikes", 0);
            boolean hasLiked = prefs.getBoolean("tajgram_user_liked", false);
            boolean hasDisliked = prefs.getBoolean("tajgram_user_disliked", false);

            if (hasLiked) {
                likes = Math.max(0, likes - 1);
                editor.putBoolean("tajgram_user_liked", false);
            } else {
                if (hasDisliked) {
                    dislikes = Math.max(0, dislikes - 1);
                    editor.putBoolean("tajgram_user_disliked", false);
                }
                likes++;
                editor.putBoolean("tajgram_user_liked", true);
                sendLiveTelegramReaction(CHANNEL_USERNAME, "👍");
            }
            editor.putInt("tajgram_global_likes", likes);
            editor.putInt("tajgram_global_dislikes", dislikes);
            editor.apply();

            updateLikeDislikeUI.run();
            Toast.makeText(context, LocaleController.getString("LikeSentToast", R.string.LikeSentToast), Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(likeCell);

        dislikeCell.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            int likes = prefs.getInt("tajgram_global_likes", 0);
            int dislikes = prefs.getInt("tajgram_global_dislikes", 0);
            boolean hasLiked = prefs.getBoolean("tajgram_user_liked", false);
            boolean hasDisliked = prefs.getBoolean("tajgram_user_disliked", false);

            if (hasDisliked) {
                dislikes = Math.max(0, dislikes - 1);
                editor.putBoolean("tajgram_user_disliked", false);
            } else {
                if (hasLiked) {
                    likes = Math.max(0, likes - 1);
                    editor.putBoolean("tajgram_user_liked", false);
                }
                dislikes++;
                editor.putBoolean("tajgram_user_disliked", true);
                sendLiveTelegramReaction(CHANNEL_USERNAME, "👎");
            }
            editor.putInt("tajgram_global_likes", likes);
            editor.putInt("tajgram_global_dislikes", dislikes);
            editor.apply();

            updateLikeDislikeUI.run();
            Toast.makeText(context, LocaleController.getString("DislikeSentToast", R.string.DislikeSentToast), Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(dislikeCell);

        updateLikeDislikeUI.run();

        // 11. Курси асъори зинда (TajgramBellCurrencyTitle ва TajgramBellCurrencyStatus)
        TextSettingsCell currencyCell = new TextSettingsCell(context);
        currencyCell.setTextAndValue(
            LocaleController.getString("TajgramBellCurrencyTitle", R.string.TajgramBellCurrencyTitle),
            LocaleController.getString("TajgramBellCurrencyStatus", R.string.TajgramBellCurrencyStatus),
            true
        );
        currencyCell.setOnClickListener(v -> fetchLiveCurrencyRates(context));
        linearLayout.addView(currencyCell);

        // 12. Интихоби овозу лаҳҷаҳо (VoiceSelection, VoiceIcon_Girl, VoiceIcon_Boy)
        TextCheckCell changeVoiceOnSendCell = new TextCheckCell(context);
        boolean isSendVoiceEnabled = prefs.getBoolean("tajgram_enable_voice_change_send", false);
        changeVoiceOnSendCell.setTextAndCheck(
            LocaleController.getString("VoiceSelection", R.string.VoiceSelection), 
            isSendVoiceEnabled, 
            true
        );
        changeVoiceOnSendCell.setOnClickListener(v -> {
            boolean current = prefs.getBoolean("tajgram_enable_voice_change_send", false);
            prefs.edit().putBoolean("tajgram_enable_voice_change_send", !current).apply();
            changeVoiceOnSendCell.setChecked(!current);
            prefs.edit().putInt("tajgram_voice_type", prefs.getInt("tajgram_voice_type", 1)).apply();
        });
        linearLayout.addView(changeVoiceOnSendCell);

        TextSettingsCell girlVoiceCell = new TextSettingsCell(context);
        girlVoiceCell.setTextAndValue(
            LocaleController.getString("VoiceIcon_Girl", R.string.VoiceIcon_Girl), 
            LocaleController.getString("VoiceDesc_Girl", R.string.VoiceDesc_Girl), 
            true
        );
        girlVoiceCell.setOnClickListener(v -> {
            prefs.edit().putInt("tajgram_voice_type", 1).apply();
            Toast.makeText(context, LocaleController.getString("VoiceGirlActivated", R.string.VoiceGirlActivated), Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(girlVoiceCell);

        TextSettingsCell boyVoiceCell = new TextSettingsCell(context);
        boyVoiceCell.setTextAndValue(
            LocaleController.getString("VoiceIcon_Boy", R.string.VoiceIcon_Boy), 
            LocaleController.getString("VoiceDesc_Boy", R.string.VoiceDesc_Boy), 
            true
        );
        boyVoiceCell.setOnClickListener(v -> {
            prefs.edit().putInt("tajgram_voice_type", 2).apply();
            Toast.makeText(context, LocaleController.getString("VoiceBoyActivated", R.string.VoiceBoyActivated), Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(boyVoiceCell);

        // 13. Даъвати дӯстон (Folk_InviteFriends)
        TextSettingsCell inviteCell = new TextSettingsCell(context);
        inviteCell.setTextAndValue(
            LocaleController.getString("Folk_InviteFriends", R.string.Folk_InviteFriends), 
            "", 
            false
        );
        inviteCell.setOnClickListener(v -> {
            String shareText = "https://t.me/" + CHANNEL_USERNAME;
            AndroidUtilities.addToClipboard(shareText);
            Toast.makeText(context, LocaleController.getString("InviteLinkCopied", R.string.InviteLinkCopied), Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(inviteCell);

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        fragmentView = scrollView;
        return fragmentView;
    }

    private void fetchLiveCurrencyRates(Context context) {
        Toast.makeText(context, LocaleController.getString("CurrencyLoading", R.string.CurrencyLoading), Toast.LENGTH_SHORT).show();
        Utilities.globalQueue.postRunnable(() -> {
            try {
                URL url = new URL("https://open.er-api.com/v6/latest/USD");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
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
                    builder.setTitle(LocaleController.getString("CurrencyDialogTitle", R.string.CurrencyDialogTitle));
                    builder.setMessage(resultText);
                    builder.setPositiveButton("OK", null);
                    builder.show();
                });
            } catch (Exception e) {
                AndroidUtilities.runOnUIThread(() -> {
                    Toast.makeText(context, LocaleController.getString("CurrencyError", R.string.CurrencyError), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void sendLiveTelegramReaction(String channel, String emoji) {
        TLRPC.TL_messages_sendReaction req = new TLRPC.TL_messages_sendReaction();
        long dialogId = MessagesController.getInstance(currentAccount).getDialogId(channel);
        TLRPC.InputPeer inputPeer = MessagesController.getInstance(currentAccount).getInputPeer(dialogId);
        if (inputPeer != null) {
            req.peer = inputPeer;
            TLRPC.TL_reactionEmoji reaction = new TLRPC.TL_reactionEmoji();
            reaction.emoticon = emoji;
            req.reaction.add(reaction);
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> {});
        }
    }

    private void showDynamicJokeDialog(Context context, String titleKey, String descKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        int titleResId = context.getResources().getIdentifier(titleKey, "string", context.getPackageName());
        int descResId = context.getResources().getIdentifier(descKey, "string", context.getPackageName());

        builder.setTitle(LocaleController.getString(titleKey, titleResId != 0 ? titleResId : R.string.VipSettingsTitle));
        builder.setMessage(LocaleController.getString(descKey, descResId != 0 ? descResId : R.string.VipSettingsMenuTitle));
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void showCustomMessageDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
