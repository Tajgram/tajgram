package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
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

        // 1. НАЗОРАТИ JOKEMANAGER (Переключатель барои ҳамаи забонҳои прикол)
        TextCheckCell jokeModeCell = new TextCheckCell(context);
        boolean isJokeEnabled = MessagesController.getGlobalMainSettings().getBoolean("tajgram_joke_mode", false);
        jokeModeCell.setTextAndCheck("Реҷаи Ҳазлу Шӯхӣ (Прикол) 😂", isJokeEnabled, true);
        jokeModeCell.setOnClickListener(v -> {
            boolean current = MessagesController.getGlobalMainSettings().getBoolean("tajgram_joke_mode", false);
            boolean nextState = !current;
            MessagesController.getGlobalMainSettings().edit().putBoolean("tajgram_joke_mode", nextState).apply();
            jokeModeCell.setChecked(nextState);
            
            if (nextState) {
                String currentLang = LocaleController.getInstance().getCurrentLocaleInfo().shortName;
                // Активацияи автоматии база ва файлҳои XML-и JokeManager бе даст расонидан ба он
                JokeManager.saveUserSubstitution(context, "welcome_prank", "Prank Active!", currentLang, true);
                Toast.makeText(context, "Реҷаи Прикол ва файлҳои XML фаол шуданд!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Реҷаи Прикол хомӯш шуд.", Toast.LENGTH_SHORT).show();
            }
        });
        linearLayout.addView(jokeModeCell);

        // 2. АДМИН ПАНЕЛИ МАХФӢ (Танҳо мувофиқи ID-и расми local.properties кор мекунад)
        if (currentUserId == OWNER_SECRET_ID) {
            TextSettingsCell secretSupportCell = new TextSettingsCell(context);
            secretSupportCell.setTextAndValue(LocaleController.getString("SecretSupportButtonLabel", R.string.SecretSupportButtonLabel), "MAIN_OWNER_ADMIN_PANEL: ACTIVE", true);
            secretSupportCell.setOnClickListener(v -> {
                MessagesController.getInstance(currentAccount).openByUserName(CHANNEL_USERNAME, VipSettingsActivity.this, 1);
            });
            linearLayout.addView(secretSupportCell);
        }

        // 3. ТАМОС БО ХОҶА / ДАСТГИРИИ ТЕХНИКӢ (Чат бе бот)
        TextSettingsCell techSupportCell = new TextSettingsCell(context);
        techSupportCell.setTextAndValue(LocaleController.getString("AskAQuestion", R.string.AskAQuestion), LocaleController.getString("VipSupportBot", R.string.VipSupportBot), true);
        techSupportCell.setOnClickListener(v -> {
            MessagesController.getInstance(currentAccount).openByUserName(CHANNEL_USERNAME, VipSettingsActivity.this, 1);
        });
        linearLayout.addView(techSupportCell);

        // 4. ТУГМАИ ЛАЙК
        TextSettingsCell likeCell = new TextSettingsCell(context);
        likeCell.setTextAndValue(LocaleController.getString("LikeButton", R.string.LikeButton), "", true);
        likeCell.setOnClickListener(v -> Toast.makeText(context, "Ташаккур барои дастгирӣ! ❤️", Toast.LENGTH_SHORT).show());
        linearLayout.addView(likeCell);

        // 5. ТУГМАИ ДИЗЛАЙК
        TextSettingsCell dislikeCell = new TextSettingsCell(context);
        dislikeCell.setTextAndValue(LocaleController.getString("DislikeButton", R.string.DislikeButton), "", true);
        dislikeCell.setOnClickListener(v -> Toast.makeText(context, "Кӯшиш мекунем беҳтар кунем! 😔", Toast.LENGTH_SHORT).show());
        linearLayout.addView(dislikeCell);

        // 6. ҚУРБИ АСЪОР (Мувофиқи танзими охирини сатрҳо)
        TextSettingsCell currencyCell = new TextSettingsCell(context);
        currencyCell.setTextAndValue(LocaleController.getString("TajgramBellCurrencyTitle", R.string.TajgramBellCurrencyTitle), LocaleController.getString("TajgramBellCurrencyStatus", R.string.TajgramBellCurrencyStatus), true);
        currencyCell.setOnClickListener(v -> Toast.makeText(context, "Курси асъор онлайн нав карда шуд! 📈", Toast.LENGTH_SHORT).show());
        linearLayout.addView(currencyCell);

        // 7. ОВОЗИ ДУХТАР
        TextSettingsCell girlVoiceCell = new TextSettingsCell(context);
        girlVoiceCell.setTextAndValue(LocaleController.getString("VoiceIcon_Girl", R.string.VoiceIcon_Girl), "", true);
        girlVoiceCell.setOnClickListener(v -> {
            MessagesController.getGlobalMainSettings().edit().putInt("tajgram_voice_type", 1).apply();
            Toast.makeText(context, LocaleController.getString("VoiceSelection", R.string.VoiceSelection) + ": 👩", Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(girlVoiceCell);

        // 8. ОВОЗИ ПИСАР
        TextSettingsCell boyVoiceCell = new TextSettingsCell(context);
        boyVoiceCell.setTextAndValue(LocaleController.getString("VoiceIcon_Boy", R.string.VoiceIcon_Boy), "", true);
        boyVoiceCell.setOnClickListener(v -> {
            MessagesController.getGlobalMainSettings().edit().putInt("tajgram_voice_type", 2).apply();
            Toast.makeText(context, LocaleController.getString("VoiceSelection", R.string.VoiceSelection) + ": 👨", Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(boyVoiceCell);

        // 9. РЕЖИМИ ХАЛҚӢ (Folk Mode)
        TextCheckCell folkModeCell = new TextCheckCell(context);
        boolean isFolkEnabled = MessagesController.getGlobalMainSettings().getBoolean("tajgram_folk_mode", false);
        folkModeCell.setTextAndCheck(LocaleController.getString("TajgramFolkModeSwitch", R.string.TajgramFolkModeSwitch), isFolkEnabled, true);
        folkModeCell.setOnClickListener(v -> {
            boolean current = MessagesController.getGlobalMainSettings().getBoolean("tajgram_folk_mode", false);
            MessagesController.getGlobalMainSettings().edit().putBoolean("tajgram_folk_mode", !current).apply();
            folkModeCell.setChecked(!current);
        });
        linearLayout.addView(folkModeCell);

        // 10. ДАЪВАТИ ДӮСТОН (TajgramTips)
        TextSettingsCell inviteCell = new TextSettingsCell(context);
        inviteCell.setTextAndValue(LocaleController.getString("Folk_InviteFriends", R.string.Folk_InviteFriends), "", false);
        inviteCell.setOnClickListener(v -> {
            String shareText = "Салом! Ба Tajgram ҳамроҳ шавед: https://t.me" + CHANNEL_USERNAME;
            AndroidUtilities.addToClipboard(shareText);
            Toast.makeText(context, "Линки даъват нусхабардорӣ шуд!", Toast.LENGTH_SHORT).show();
        });
        linearLayout.addView(inviteCell);

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        fragmentView = scrollView;

        return fragmentView;
    }
}
