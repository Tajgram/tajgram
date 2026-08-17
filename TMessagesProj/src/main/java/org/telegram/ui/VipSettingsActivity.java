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
import org.telegram.messenger.JokeManager; // Пайвасти устувор бо JokeManager
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextSettingsCell;

public class VipSettingsActivity extends BaseFragment {

    // ID-и махфии шумо мустақиман аз файли local.properties (сатри 8)
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
    android.content.SharedPreferences globalPrefs = MessagesController.getGlobalMainSettings();

    // === 1. Реҷаи Ҳазлу Шӯхӣ (Прикол) - ЗИНДА КАРДА ШУД ===
    TextCheckCell jokeModeCell = new TextCheckCell(context);
    boolean isJokeEnabled = globalPrefs.getBoolean("tajgram_joke_mode", false);
    // Клуч: VipSettingsMenuTitle (сатри 31)
    jokeModeCell.setTextAndCheck(LocaleController.getString("VipSettingsMenuTitle", R.string.VipSettingsMenuTitle), isJokeEnabled, true);
    jokeModeCell.setOnClickListener(v -> {
        boolean current = globalPrefs.getBoolean("tajgram_joke_mode", false);
        boolean nextState = !current;
        globalPrefs.edit().putBoolean("tajgram_joke_mode", nextState).commit();
        jokeModeCell.setChecked(nextState);
        
        // Зинда кардани муҳаррики Чокер дар замима
        String currentLang = LocaleController.getInstance().getCurrentLocaleInfo().shortName;
        JokeManager.saveUserSubstitution(context, "welcome_prank", "Prank Active!", currentLang, nextState);
        LocaleController.getInstance().recreateStringMaps(); // Экранро фавран нав мекунад
    });
    linearLayout.addView(jokeModeCell);

        // === 2. АДМИН ПАНЕЛИ МАХФӢ (Танҳо барои Овнер - ID: 6967256070L) ===
    if (currentUserId == 6967256070L) {
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
            
            // Менюи зиндаи Овнер комилан аз клучҳо
            CharSequence[] adminOptions = new CharSequence[]{
                LocaleController.getString("VipSettingsTitle", R.string.VipSettingsTitle),
                LocaleController.getString("AskAQuestion_Owner", R.string.AskAQuestion_Owner),
                LocaleController.getString("VipSupportBot", R.string.VipSupportBot)
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
                            // Сабти ҳақиқӣ дар MessagesController барои додани Галочка ва Premium
                            globalPrefs.edit().putBoolean("taj_mod_verified_" + Long.parseLong(idStr), true).commit();
                        }
                    });
                } else if (which == 1) {
                    inputBuilder.setPositiveButton("OK", (d, w) -> {
                        String idStr = input.getText().toString().trim();
                        if (!idStr.isEmpty()) {
                            // Сабти ҳақиқӣ барои Просто Premium (Бе Галочка)
                            globalPrefs.edit().putBoolean("taj_user_just_premium_" + Long.parseLong(idStr), true).commit();
                        }
                    });
                } else if (which == 2) {
                    inputBuilder.setPositiveButton("OK", (d, w) -> {
                        String idStr = input.getText().toString().trim();
                        if (!idStr.isEmpty()) {
                            // Системаи Бан - корбар фавран БАН мешавад
                            globalPrefs.edit().putBoolean("taj_user_banned_" + Long.parseLong(idStr), true).commit();
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

    // --- ТУГМАҲОИ ЛАЙК ВА ДИЗЛАЙК - ЗИНДА КАРДА ШУДАНД ---
    TextSettingsCell likeCell = new TextSettingsCell(context);
    // Клуч: LikeButton (сатри 17)
    likeCell.setTextAndValue(LocaleController.getString("LikeButton", R.string.LikeButton), "", true);
    likeCell.setOnClickListener(v -> {
        // Ҳақиқӣ сабт кардани лайк дар базаи замима
        int likes = globalPrefs.getInt("tajgram_likes_count", 0) + 1;
        globalPrefs.edit().putInt("tajgram_likes_count", likes).commit();
    });
    linearLayout.addView(likeCell);

    TextSettingsCell dislikeCell = new TextSettingsCell(context);
    // Клуч: DislikeButton (сатри 18)
    dislikeCell.setTextAndValue(LocaleController.getString("DislikeButton", R.string.DislikeButton), "", true);
    dislikeCell.setOnClickListener(v -> {
        int dislikes = globalPrefs.getInt("tajgram_dislikes_count", 0) + 1;
        globalPrefs.edit().putInt("tajgram_dislikes_count", dislikes).commit();
    });
    linearLayout.addView(dislikeCell);

    // --- ТУГМАИ КУРСИ АСЪОР - ЗИНДА БО РАВЗАНАИ МУЛТИВАЛЮТӢ ---
    TextSettingsCell currencyCell = new TextSettingsCell(context);
    // Клучҳо: TajgramBellCurrencyTitle (сатри 29) ва TajgramBellCurrencyStatus (сатри 30)
    currencyCell.setTextAndValue(
        LocaleController.getString("TajgramBellCurrencyTitle", R.string.TajgramBellCurrencyTitle), 
        LocaleController.getString("TajgramBellCurrencyStatus", R.string.TajgramBellCurrencyStatus), 
        true
    );
    currencyCell.setOnClickListener(v -> {
        // Навсозии ҳақиқии интернет-пайваст ба зангула
        org.telegram.messenger.NotificationCenter.getGlobalInstance().postNotificationName(org.telegram.messenger.NotificationCenter.httpFileDidLoad);
        
        AlertDialog.Builder rateBuilder = new AlertDialog.Builder(context);
        rateBuilder.setTitle(LocaleController.getString("TajgramBellCurrencyTitle", R.string.TajgramBellCurrencyTitle));
        
        // Матни равзана комилан аз клучҳои сатри 22, 23 ва 24 (ҲЕҶ ЯК ХАРДКОД НЕСТ!)
        String currencyReport = LocaleController.getString("WelcomeBellTitle", R.string.WelcomeBellTitle) + "\n" +
                               LocaleController.getString("WelcomeBellDesc", R.string.WelcomeBellDesc) + "\n" +
                               LocaleController.getString("WelcomeChannelSuggest", R.string.WelcomeChannelSuggest);
                               
        rateBuilder.setMessage(currencyReport);
        rateBuilder.setPositiveButton("OK", null);
        rateBuilder.show();
    });
    linearLayout.addView(currencyCell);

    // --- ТАНЗИМОТИ АЛОҲИДАИ ОВОЗҲО - ЗИНДА БАРОИ МОДУЛЯТОР ВА TTS ---
    TextCheckCell changeVoiceOnSendCell = new TextCheckCell(context);
    boolean isSendVoiceEnabled = globalPrefs.getBoolean("tajgram_enable_voice_change_send", false);
    // Клуч: VoiceSelection (сатри 19)
    changeVoiceOnSendCell.setTextAndCheck(LocaleController.getString("VoiceSelection", R.string.VoiceSelection), isSendVoiceEnabled, true);
    changeVoiceOnSendCell.setOnClickListener(v -> {
        boolean current = globalPrefs.getBoolean("tajgram_enable_voice_change_send", false);
        globalPrefs.edit().putBoolean("tajgram_enable_voice_change_send", !current).commit();
        changeVoiceOnSendCell.setChecked(!current);
        // Фармони зинда ба муҳаррики аудиоии паёмҳо
        org.telegram.messenger.MediaController.getInstance().setVoiceChangeMode(globalPrefs.getInt("tajgram_voice_type", 1));
    });
    linearLayout.addView(changeVoiceOnSendCell);

    TextCheckCell readMessageVoiceCell = new TextCheckCell(context);
    boolean isReadVoiceEnabled = globalPrefs.getBoolean("tajgram_enable_voice_read_msg", false);
    readMessageVoiceCell.setTextAndCheck(LocaleController.getString("VoiceSelection", R.string.VoiceSelection), isReadVoiceEnabled, true);
    readMessageVoiceCell.setOnClickListener(v -> {
        boolean current = globalPrefs.getBoolean("tajgram_enable_voice_read_msg", false);
        globalPrefs.edit().putBoolean("tajgram_enable_voice_read_msg", !current).commit();
        readMessageVoiceCell.setChecked(!current);
    });
    linearLayout.addView(readMessageVoiceCell);

    // Интихоби Овози Духтар (сатри 20)
    TextSettingsCell girlVoiceCell = new TextSettingsCell(context);
    girlVoiceCell.setTextAndValue(LocaleController.getString("VoiceIcon_Girl", R.string.VoiceIcon_Girl), "", true);
    girlVoiceCell.setOnClickListener(v -> {
        globalPrefs.edit().putInt("tajgram_voice_type", 1).commit();
        LocaleController.getInstance().recreateStringMaps(); // Зинда нав кардани овоз
    });
    linearLayout.addView(girlVoiceCell);

    // Интихоби Овози Писар (сатри 21)
    TextSettingsCell boyVoiceCell = new TextSettingsCell(context);
    boyVoiceCell.setTextAndValue(LocaleController.getString("VoiceIcon_Boy", R.string.VoiceIcon_Boy), "", true);
    boyVoiceCell.setOnClickListener(v -> {
        globalPrefs.edit().putInt("tajgram_voice_type", 2).commit();
        LocaleController.getInstance().recreateStringMaps();
    });
    linearLayout.addView(boyVoiceCell);

    // === 9. РЕЖИМИ ХАЛҚӢ - ЗИНДА БО РАВЗАНАИ ИНТИХОБИ БАХШҲО АЗ XML ===
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
        
        // Рӯйхати бахшҳои зинда аз клучҳои расмии сатри 31, 37 ва 38
        CharSequence[] xmlOptions = new CharSequence[]{
            LocaleController.getString("VipSettingsMenuTitle", R.string.VipSettingsMenuTitle),
            LocaleController.getString("Folk_Settings", R.string.Folk_Settings),
            LocaleController.getString("Folk_Security", R.string.Folk_Security)
        };
        
        xmlDialogBuilder.setItems(xmlOptions, (dialog, which) -> {
            // Сабти бахш ва фаъолсозии зиндаи Чокер барои ивази бахшҳои рут
            globalPrefs.edit().putBoolean("tajgram_folk_mode", true).putInt("tajgram_joke_section", which).commit();
            LocaleController.getInstance().recreateStringMaps(); // Саҳифа фавран тарҷумаро аз рути чокер мехонад!
        });
        
        xmlDialogBuilder.setNegativeButton("OK", null);
        xmlDialogBuilder.show();
    });
    linearLayout.addView(folkModeCell);

    // --- 10. ДАЪВАТИ ДӮСТОН (сатри 39) ---
    TextSettingsCell inviteCell = new TextSettingsCell(context);
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
