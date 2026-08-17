package org.telegram.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.JokeManager;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextSettingsCell;

public class VipSettingsActivity extends BaseFragment {

    private static final long OWNER_SECRET_ID = 6967256070L;
    private static final String CHANNEL_USERNAME = "TajgramTips";

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        // Сатри 15: VipSettingsTitle
        actionBar.setTitle(LocaleController.getString("VipSettingsTitle", R.string.VipSettingsTitle));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        long currentUserId = UserConfig.getInstance(currentAccount).getClientUserId();
        TLRPC.User currentUser = UserConfig.getInstance(currentAccount).getCurrentUser();
        String currentUsername = (currentUser != null && currentUser.username != null) ? "@" + currentUser.username : "";
        SharedPreferences prefs = MessagesController.getMainSettings(UserConfig.selectedAccount);

        // --- ЛОГИКАИ 1: Real ID ва Username (Берун аз XML) ---
        HeaderCell idHeader = new HeaderCell(context);
        idHeader.setText("Профиль ва Идентификатор");
        linearLayout.addView(idHeader);

        TextSettingsCell myIdCell = new TextSettingsCell(context);
        myIdCell.setTextAndValue("Идентификатори шумо " + currentUsername, "ID: " + currentUserId, true);
        myIdCell.setOnClickListener(v -> {
            AndroidUtilities.addToClipboard(String.valueOf(currentUserId));
            Toast.makeText(context, "ID нусхабардорӣ шуд: " + currentUserId, Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(myIdCell);

        HeaderCell settingsHeader = new HeaderCell(context);
        // Сатри 31: VipSettingsMenuTitle
        settingsHeader.setText(LocaleController.getString("VipSettingsMenuTitle", R.string.VipSettingsMenuTitle));
        linearLayout.addView(settingsHeader);

        // --- XML сатри 31: Реҷаи Ҳазлу Шӯхӣ ---
        TextCheckCell jokeModeCell = new TextCheckCell(context);
        boolean isJokeEnabled = prefs.getBoolean("tajgram_joke_mode", false);
        jokeModeCell.setTextAndCheck(LocaleController.getString("VipSettingsMenuTitle", R.string.VipSettingsMenuTitle), isJokeEnabled, true);
        jokeModeCell.setOnClickListener(v -> {
            boolean nextState = !prefs.getBoolean("tajgram_joke_mode", false);
            prefs.edit().putBoolean("tajgram_joke_mode", nextState).apply();
            jokeModeCell.setChecked(nextState);

            String currentLang = LocaleController.getInstance().getCurrentLocaleInfo().shortName;
            JokeManager.saveUserSubstitution(context, "welcome_prank", "Prank Active!", currentLang, nextState);
            LocaleController.getInstance().recreateStringMaps();
        });
        linearLayout.addView(jokeModeCell);

        // --- ЛОГИКАИ 2: Админ Панели Махфӣ (Берун аз XML, барои OWNER_SECRET_ID) ---
        if (currentUserId == OWNER_SECRET_ID) {
            TextSettingsCell secretSupportCell = new TextSettingsCell(context);
            // Сатри 32: SecretSupportButtonLabel | Сатри 34: AskAQuestion_Owner
            secretSupportCell.setTextAndValue(
                LocaleController.getString("SecretSupportButtonLabel", R.string.SecretSupportButtonLabel),
                LocaleController.getString("AskAQuestion_Owner", R.string.AskAQuestion_Owner),
                true
            );
            secretSupportCell.setOnClickListener(v -> {
                AlertDialog.Builder adminBuilder = new AlertDialog.Builder(context);
                adminBuilder.setTitle(LocaleController.getString("SecretSupportButtonLabel", R.string.SecretSupportButtonLabel));

                CharSequence[] adminOptions = new CharSequence[]{
                    LocaleController.getString("VipSettingsTitle", R.string.VipSettingsTitle), // Сатри 15
                    LocaleController.getString("AskAQuestion_Owner", R.string.AskAQuestion_Owner), // Сатри 34
                    LocaleController.getString("VipSupportBot", R.string.VipSupportBot) // Сатри 16
                };

                adminBuilder.setItems(adminOptions, (dialog, which) -> {
                    AlertDialog.Builder inputBuilder = new AlertDialog.Builder(context);
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    inputBuilder.setView(input);

                    inputBuilder.setPositiveButton("OK", (d, w) -> {
                        String idStr = input.getText().toString().trim();
                        if (!idStr.isEmpty()) {
                            long targetUid = Long.parseLong(idStr);
                            SharedPreferences.Editor editor = prefs.edit();
                            if (which == 0) {
                                editor.putBoolean("taj_mod_premium_" + targetUid, true);
                                editor.putBoolean("taj_mod_verified_" + targetUid, true);
                                Toast.makeText(context, "Статуси Модератор дода шуд!", Toast.LENGTH_SHORT).show();
                            } else if (which == 1) {
                                editor.putBoolean("taj_user_just_premium_" + targetUid, true);
                                Toast.makeText(context, "Статуси Premium дода шуд!", Toast.LENGTH_SHORT).show();
                            } else if (which == 2) {
                                editor.putBoolean("taj_user_banned_" + targetUid, true);
                                editor.putBoolean("taj_mod_premium_" + targetUid, false);
                                editor.putBoolean("taj_mod_verified_" + targetUid, false);
                                Toast.makeText(context, "Корбар Бан шуд!", Toast.LENGTH_SHORT).show();
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

        // --- XML Сатри 33 & 16: Пуштибонии Фаннӣ ---
        TextSettingsCell techSupportCell = new TextSettingsCell(context);
        techSupportCell.setTextAndValue(
            LocaleController.getString("AskAQuestion", R.string.AskAQuestion),
            LocaleController.getString("VipSupportBot", R.string.VipSupportBot),
            true
        );
        techSupportCell.setOnClickListener(v -> MessagesController.getInstance(currentAccount).openByUserName(CHANNEL_USERNAME, VipSettingsActivity.this, 1));
        linearLayout.addView(techSupportCell);

        // --- ЛОГИКАИ 3: Ҳисобкунаки Лайк ва Дизлайк (XML Сатри 17 ва 18) ---
        int currentLikes = prefs.getInt("tajgram_likes_count", 0);
        int currentDislikes = prefs.getInt("tajgram_dislikes_count", 0);

        TextSettingsCell likeCell = new TextSettingsCell(context);
        likeCell.setTextAndValue(LocaleController.getString("LikeButton", R.string.LikeButton), String.valueOf(currentLikes), true);
        likeCell.setOnClickListener(v -> {
            int likes = prefs.getInt("tajgram_likes_count", 0) + 1;
            prefs.edit().putInt("tajgram_likes_count", likes).apply();
            likeCell.setTextAndValue(LocaleController.getString("LikeButton", R.string.LikeButton), String.valueOf(likes), true);
            Toast.makeText(context, "Лайк сабт шуд 👍", Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(likeCell);

        TextSettingsCell dislikeCell = new TextSettingsCell(context);
        dislikeCell.setTextAndValue(LocaleController.getString("DislikeButton", R.string.DislikeButton), String.valueOf(currentDislikes), true);
        dislikeCell.setOnClickListener(v -> {
            int dislikes = prefs.getInt("tajgram_dislikes_count", 0) + 1;
            prefs.edit().putInt("tajgram_dislikes_count", dislikes).apply();
            dislikeCell.setTextAndValue(LocaleController.getString("DislikeButton", R.string.DislikeButton), String.valueOf(dislikes), true);
            Toast.makeText(context, "Дизлайк сабт шуд 👎", Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(dislikeCell);

        // --- XML Сатри 29, 30, 22, 23, 24: Курси Асъор ---
        TextSettingsCell currencyCell = new TextSettingsCell(context);
        currencyCell.setTextAndValue(
            LocaleController.getString("TajgramBellCurrencyTitle", R.string.TajgramBellCurrencyTitle),
            LocaleController.getString("TajgramBellCurrencyStatus", R.string.TajgramBellCurrencyStatus),
            true
        );
        currencyCell.setOnClickListener(v -> {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.httpFileDidLoad);
            AlertDialog.Builder rateBuilder = new AlertDialog.Builder(context);
            rateBuilder.setTitle(LocaleController.getString("TajgramBellCurrencyTitle", R.string.TajgramBellCurrencyTitle));
            String currencyReport = LocaleController.getString("WelcomeBellTitle", R.string.WelcomeBellTitle) + "\n" +
                                   LocaleController.getString("WelcomeBellDesc", R.string.WelcomeBellDesc) + "\n" +
                                   LocaleController.getString("WelcomeChannelSuggest", R.string.WelcomeChannelSuggest);
            rateBuilder.setMessage(currencyReport);
            rateBuilder.setPositiveButton("OK", null);
            rateBuilder.show();
        });
        linearLayout.addView(currencyCell);

        // --- ЛОГИКАИ 4: Модулятсияи овоз дар MediaController (XML Сатри 19, 20, 21) ---
        TextCheckCell changeVoiceOnSendCell = new TextCheckCell(context);
        boolean isSendVoiceEnabled = prefs.getBoolean("tajgram_enable_voice_change_send", false);
        changeVoiceOnSendCell.setTextAndCheck(LocaleController.getString("VoiceSelection", R.string.VoiceSelection) + " (Ирсол)", isSendVoiceEnabled, true);
        changeVoiceOnSendCell.setOnClickListener(v -> {
            boolean current = prefs.getBoolean("tajgram_enable_voice_change_send", false);
            prefs.edit().putBoolean("tajgram_enable_voice_change_send", !current).apply();
            changeVoiceOnSendCell.setChecked(!current);
            MediaController.getInstance().setVoiceChangeMode(prefs.getInt("tajgram_voice_type", 1));
        });
        linearLayout.addView(changeVoiceOnSendCell);

        TextSettingsCell girlVoiceCell = new TextSettingsCell(context);
        girlVoiceCell.setTextAndValue(
            LocaleController.getString("VoiceIcon_Girl", R.string.VoiceIcon_Girl),
            prefs.getInt("tajgram_voice_type", 1) == 1 ? "АКТИВ" : "",
            true
        );
        girlVoiceCell.setOnClickListener(v -> {
            prefs.edit().putInt("tajgram_voice_type", 1).apply();
            Toast.makeText(context, "Овози Духтар фаол шуд", Toast.LENGTH_SHORT).show();
            LocaleController.getInstance().recreateStringMaps();
        });
        linearLayout.addView(girlVoiceCell);

        TextSettingsCell boyVoiceCell = new TextSettingsCell(context);
        boyVoiceCell.setTextAndValue(
            LocaleController.getString("VoiceIcon_Boy", R.string.VoiceIcon_Boy),
            prefs.getInt("tajgram_voice_type", 1) == 2 ? "АКТИВ" : "",
            true
        );
        boyVoiceCell.setOnClickListener(v -> {
            prefs.edit().putInt("tajgram_voice_type", 2).apply();
            Toast.makeText(context, "Овози Писар фаол шуд", Toast.LENGTH_SHORT).show();
            LocaleController.getInstance().recreateStringMaps();
        });
        linearLayout.addView(boyVoiceCell);

        // --- XML Сатри 35, 36, 37, 38: Усули Халқӣ ---
        TextSettingsCell folkModeCell = new TextSettingsCell(context);
        folkModeCell.setTextAndValue(
            LocaleController.getString("TajgramFolkModeSwitch", R.string.TajgramFolkModeSwitch),
            LocaleController.getString("TajgramFolkModeDesc", R.string.TajgramFolkModeDesc),
            true
        );
        folkModeCell.setOnClickListener(v -> {
            AlertDialog.Builder xmlDialogBuilder = new AlertDialog.Builder(context);
            xmlDialogBuilder.setTitle(LocaleController.getString("TajgramFolkModeSwitch", R.string.TajgramFolkModeSwitch));

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

        // --- XML Сатри 39: Даъвати Дӯстон ---
        TextSettingsCell inviteCell = new TextSettingsCell(context);
        inviteCell.setTextAndValue(LocaleController.getString("Folk_InviteFriends", R.string.Folk_InviteFriends), "", false);
        inviteCell.setOnClickListener(v -> {
            String shareText = "https://t.me/" + CHANNEL_USERNAME;
            AndroidUtilities.addToClipboard(shareText);
            Toast.makeText(context, "Линки даъват нусхабардорӣ шуд!", Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(inviteCell);

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        fragmentView = scrollView;
        return fragmentView;
    }

    // --- XML Сатри 25, 26, 27, 28: Равзанаи баромад (Exit / Reentry Dialogs) ---
    @Override
    public boolean onBackPressed() {
        if (getParentActivity() != null) {
            AlertDialog.Builder exitBuilder = new AlertDialog.Builder(getParentActivity());
            exitBuilder.setTitle(LocaleController.getString("ExitDialogTitle", R.string.ExitDialogTitle)); // Сатри 27
            exitBuilder.setMessage(LocaleController.getString("ExitDialogDesc", R.string.ExitDialogDesc)); // Сатри 28
            exitBuilder.setPositiveButton("Ҳа", (dialog, which) -> finishFragment());
            exitBuilder.setNegativeButton("Не", null);
            exitBuilder.show();
            return false;
        }
        return super.onBackPressed();
    }
}
