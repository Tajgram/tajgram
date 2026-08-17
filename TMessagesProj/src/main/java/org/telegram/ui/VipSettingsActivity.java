package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.EditText;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.JokeManager; 
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextSettingsCell;

public class VipSettingsActivity extends BaseFragment {

    private static final long OWNER_SECRET_ID = 6967256070L;
    private static final String CHANNEL_USERNAME = "TajgramTips";

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        // Клуч: VipSettingsTitle (сатри 15)
        actionBar.setTitle(LocaleController.getString("VipSettingsTitle", R.string.VipSettingsTitle));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment(); // Бозгашти бехатар
                }
            }
        });

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        long currentUserId = UserConfig.getInstance(currentAccount).getClientUserId();
        // Сабти ҳақиқӣ мувофиқи саҳифаи 4-и PDF
        android.content.SharedPreferences prefs = MessagesController.getMainSettings(UserConfig.selectedAccount);

        // === 1. Назорати Реҷаи Ҳазлу Шӯхӣ (Прикол) ===
        TextCheckCell jokeModeCell = new TextCheckCell(context);
        boolean isJokeEnabled = prefs.getBoolean("tajgram_joke_mode", false);
        // Клуч: VipSettingsMenuTitle (сатри 31)
        jokeModeCell.setTextAndCheck(LocaleController.getString("VipSettingsMenuTitle", R.string.VipSettingsMenuTitle), isJokeEnabled, true);
        jokeModeCell.setOnClickListener(v -> {
            boolean current = prefs.getBoolean("tajgram_joke_mode", false);
            boolean nextState = !current;
            prefs.edit().putBoolean("tajgram_joke_mode", nextState).apply();
            jokeModeCell.setChecked(nextState);
            
            // Зинда кардани муҳаррики Чокер
            String currentLang = LocaleController.getInstance().getCurrentLocaleInfo().shortName;
            JokeManager.saveUserSubstitution(context, "welcome_prank", "Prank Active!", currentLang, nextState);
            LocaleController.getInstance().recreateStringMaps(); // Фавран экранро нав мекунад
        });
        linearLayout.addView(jokeModeCell);

            // === 2. АДМИН ПАНЕЛИ МАХФӢ (Танҳо барои Овнер - ID: 6967256070L) ===
        if (currentUserId == OWNER_SECRET_ID) {
            TextSettingsCell secretSupportCell = new TextSettingsCell(context);
            // Клучҳо: SecretSupportButtonLabel (сатри 32) ва AskAQuestion_Owner (сатри 34)
            secretSupportCell.setTextAndValue(
                LocaleController.getString("SecretSupportButtonLabel", R.string.SecretSupportButtonLabel),
                LocaleController.getString("AskAQuestion_Owner", R.string.AskAQuestion_Owner), 
                true
            );
            secretSupportCell.setOnClickListener(v -> {
                AlertDialog.Builder adminBuilder = new AlertDialog.Builder(context);
                adminBuilder.setTitle(LocaleController.getString("SecretSupportButtonLabel", R.string.SecretSupportButtonLabel));
                
                CharSequence[] adminOptions = new CharSequence[]{
                    LocaleController.getString("VipSettingsTitle", R.string.VipSettingsTitle), // Модератор
                    LocaleController.getString("AskAQuestion_Owner", R.string.AskAQuestion_Owner), // Просто Premium
                    LocaleController.getString("VipSupportBot", R.string.VipSupportBot) // Бан (Блок)
                };
                
                adminBuilder.setItems(adminOptions, (dialog, which) -> {
                    AlertDialog.Builder inputBuilder = new AlertDialog.Builder(context);
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    inputBuilder.setView(input);

                    if (which == 0) {
                        inputBuilder.setPositiveButton("OK", (d, w) -> {
                            String idStr = input.getText().toString().trim();
                            if (!idStr.isEmpty()) {
                                long modId = Long.parseLong(idStr);
                                android.content.SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("taj_mod_premium_" + modId, true);
                                editor.putBoolean("taj_mod_verified_" + modId, true);
                                editor.commit(); 
                            }
                        });
                    } else if (which == 1) {
                        inputBuilder.setPositiveButton("OK", (d, w) -> {
                            String idStr = input.getText().toString().trim();
                            if (!idStr.isEmpty()) {
                                long targetUid = Long.parseLong(idStr);
                                android.content.SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("taj_user_just_premium_" + targetUid, true);
                                editor.commit();
                            }
                        });
                    } else if (which == 2) {
                        inputBuilder.setPositiveButton("OK", (d, w) -> {
                            String idStr = input.getText().toString().trim();
                            if (!idStr.isEmpty()) {
                                long targetUserId = Long.parseLong(idStr);
                                android.content.SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("taj_user_banned_" + targetUserId, true);
                                editor.putBoolean("taj_mod_premium_" + targetUserId, false);
                                editor.putBoolean("taj_mod_verified_" + targetUserId, false);
                                editor.commit(); 
                            }
                        });
                    }
                    inputBuilder.show();
                });
                adminBuilder.show();
            });
            linearLayout.addView(secretSupportCell);
        }

            // --- ТУГМАИ ПУШТИБОНИИ ФАННӢ ---
        TextSettingsCell techSupportCell = new TextSettingsCell(context);
        // Клучҳо: AskAQuestion (сатри 33) ва VipSupportBot (сатри 16)
        techSupportCell.setTextAndValue(
            LocaleController.getString("AskAQuestion", R.string.AskAQuestion), 
            LocaleController.getString("VipSupportBot", R.string.VipSupportBot), 
            true
        );
        techSupportCell.setOnClickListener(v -> MessagesController.getInstance(currentAccount).openByUserName(CHANNEL_USERNAME, VipSettingsActivity.this, 1));
        linearLayout.addView(techSupportCell);

        // --- ТУГМАҲОИ ЛАЙК ВА ДИЗЛАЙК ---
        TextSettingsCell likeCell = new TextSettingsCell(context);
        // Клуч: LikeButton (сатри 17)
        likeCell.setTextAndValue(LocaleController.getString("LikeButton", R.string.LikeButton), "", true);
        likeCell.setOnClickListener(v -> {
            int likes = prefs.getInt("tajgram_likes_count", 0) + 1;
            prefs.edit().putInt("tajgram_likes_count", likes).apply();
        });
        linearLayout.addView(likeCell);

        TextSettingsCell dislikeCell = new TextSettingsCell(context);
        // Клуч: DislikeButton (сатри 18)
        dislikeCell.setTextAndValue(LocaleController.getString("DislikeButton", R.string.DislikeButton), "", true);
        dislikeCell.setOnClickListener(v -> {
            int dislikes = prefs.getInt("tajgram_dislikes_count", 0) + 1;
            prefs.edit().putInt("tajgram_dislikes_count", dislikes).apply();
        });
        linearLayout.addView(dislikeCell);

        // --- ТУГМАИ КУРСИ АСЪОР (ЗИНДА ВА КОМИЛАН АЗ КЛУЧҲО) ---
        TextSettingsCell currencyCell = new TextSettingsCell(context);
        // Клучҳо: TajgramBellCurrencyTitle (сатри 29) ва TajgramBellCurrencyStatus (сатри 30)
        currencyCell.setTextAndValue(
            LocaleController.getString("TajgramBellCurrencyTitle", R.string.TajgramBellCurrencyTitle), 
            LocaleController.getString("TajgramBellCurrencyStatus", R.string.TajgramBellCurrencyStatus), 
            true
        );
        currencyCell.setOnClickListener(v -> {
            org.telegram.messenger.NotificationCenter.getGlobalInstance().postNotificationName(org.telegram.messenger.NotificationCenter.httpFileDidLoad);
            
            AlertDialog.Builder rateBuilder = new AlertDialog.Builder(context);
            rateBuilder.setTitle(LocaleController.getString("TajgramBellCurrencyTitle", R.string.TajgramBellCurrencyTitle));
            
            // Матни дохили равзана пурра ва бе хардкод аз клучҳои сатри 22, 23 ва 24 хонда мешавад!
            String currencyReport = LocaleController.getString("WelcomeBellTitle", R.string.WelcomeBellTitle) + "\n" +
                                   LocaleController.getString("WelcomeBellDesc", R.string.WelcomeBellDesc) + "\n" +
                                   LocaleController.getString("WelcomeChannelSuggest", R.string.WelcomeChannelSuggest);
                                   
            rateBuilder.setMessage(currencyReport);
            rateBuilder.setPositiveButton("OK", null);
            rateBuilder.show();
        });
        linearLayout.addView(currencyCell);

        // --- ТАНЗИМОТИ АЛОҲИДАИ ОВОЗҲО (ҲАМ МОДУЛЯТОР ВА ҲАМ TTS) ---
        TextCheckCell changeVoiceOnSendCell = new TextCheckCell(context);
        boolean isSendVoiceEnabled = prefs.getBoolean("tajgram_enable_voice_change_send", false);
        // Клуч: VoiceSelection (сатри 19)
        changeVoiceOnSendCell.setTextAndCheck(LocaleController.getString("VoiceSelection", R.string.VoiceSelection), isSendVoiceEnabled, true);
        changeVoiceOnSendCell.setOnClickListener(v -> {
            boolean current = prefs.getBoolean("tajgram_enable_voice_change_send", false);
            prefs.edit().putBoolean("tajgram_enable_voice_change_send", !current).apply();
            changeVoiceOnSendCell.setChecked(!current);
            org.telegram.messenger.MediaController.getInstance().setVoiceChangeMode(prefs.getInt("tajgram_voice_type", 1));
        });
        linearLayout.addView(changeVoiceOnSendCell);

        TextCheckCell readMessageVoiceCell = new TextCheckCell(context);
        boolean isReadVoiceEnabled = prefs.getBoolean("tajgram_enable_voice_read_msg", false);
        readMessageVoiceCell.setTextAndCheck(LocaleController.getString("VoiceSelection", R.string.VoiceSelection), isReadVoiceEnabled, true);
        readMessageVoiceCell.setOnClickListener(v -> {
            boolean current = prefs.getBoolean("tajgram_enable_voice_read_msg", false);
            prefs.edit().putBoolean("tajgram_enable_voice_read_msg", !current).apply();
            readMessageVoiceCell.setChecked(!current);
        });
        linearLayout.addView(readMessageVoiceCell);

        // Интихоби Овози Духтар
        TextSettingsCell girlVoiceCell = new TextSettingsCell(context);
        // Клуч: VoiceIcon_Girl (сатри 20)
        girlVoiceCell.setTextAndValue(LocaleController.getString("VoiceIcon_Girl", R.string.VoiceIcon_Girl), "", true);
        girlVoiceCell.setOnClickListener(v -> {
            prefs.edit().putInt("tajgram_voice_type", 1).apply();
            LocaleController.getInstance().recreateStringMaps();
        });
        linearLayout.addView(girlVoiceCell);

        // Интихоби Овози Писар
        TextSettingsCell boyVoiceCell = new TextSettingsCell(context);
        // Клуч: VoiceIcon_Boy (сатри 21)
        boyVoiceCell.setTextAndValue(LocaleController.getString("VoiceIcon_Boy", R.string.VoiceIcon_Boy), "", true);
        boyVoiceCell.setOnClickListener(v -> {
            prefs.edit().putInt("tajgram_voice_type", 2).apply();
            LocaleController.getInstance().recreateStringMaps();
        });
        linearLayout.addView(boyVoiceCell);

        // === 9. РЕЖИМИ ХАЛҚӢ (РАВЗАНАИ ИНТИХОБИ БАХШҲО АЗ XML) ===
        TextSettingsCell folkModeCell = new TextSettingsCell(context);
        // Клучҳо: TajgramFolkModeSwitch (сатри 35) ва TajgramFolkModeDesc (сатри 36)
        folkModeCell.setTextAndValue(
            LocaleController.getString("TajgramFolkModeSwitch", R.string.TajgramFolkModeSwitch), 
            LocaleController.getString("TajgramFolkModeDesc", R.string.TajgramFolkModeDesc), 
            true
        );
        folkModeCell.setOnClickListener(v -> {
            AlertDialog.Builder xmlDialogBuilder = new AlertDialog.Builder(context);
            xmlDialogBuilder.setTitle(LocaleController.getString("TajgramFolkModeSwitch", R.string.TajgramFolkModeSwitch));
            
            // Бахшҳо пурра ва тоза аз клучҳои расмии сатри 31, 37 ва 38
            CharSequence[] xmlOptions = new CharSequence[]{
                LocaleController.getString("VipSettingsMenuTitle", R.string.VipSettingsMenuTitle),
                LocaleController.getString("Folk_Settings", R.string.Folk_Settings),
                LocaleController.getString("Folk_Security", R.string.Folk_Security)
            };
            
            xmlDialogBuilder.setItems(xmlOptions, (dialog, which) -> {
                prefs.edit().putBoolean("tajgram_folk_mode", true).putInt("tajgram_joke_section", which).apply();
                LocaleController.getInstance().recreateStringMaps();
            });
            
            xmlDialogBuilder.setNegativeButton("OK", null);
            xmlDialogBuilder.show();
        });
        linearLayout.addView(folkModeCell);

        // --- 10. ДАЪВАТИ ДӮСТОН ---
        TextSettingsCell inviteCell = new TextSettingsCell(context);
        // Клуч: Folk_InviteFriends (сатри 39)
        inviteCell.setTextAndValue(LocaleController.getString("Folk_InviteFriends", R.string.Folk_InviteFriends), "", false);
        inviteCell.setOnClickListener(v -> {
            String shareText = "https://t.me/" + CHANNEL_USERNAME;
            AndroidUtilities.addToClipboard(shareText);
        });
        linearLayout.addView(inviteCell);

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        fragmentView = scrollView;
        return fragmentView;
    }
}
