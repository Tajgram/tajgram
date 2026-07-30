package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;

public class TajgramIntroActivity extends BaseFragment {

    private String[] customTitles;
    private String[] customDescs;

    @Override
    public boolean onFragmentCreate() {
        // Инициализацияи сатрҳо баъд аз сохта шудани фрагмент
        customTitles = new String[] {
            LocaleController.getString("onboarding_title_1", R.string.onboarding_title_1),
            LocaleController.getString("onboarding_title_2", R.string.onboarding_title_2),
            LocaleController.getString("onboarding_title_3", R.string.onboarding_title_3),
            LocaleController.getString("onboarding_title_4", R.string.onboarding_title_4),
            LocaleController.getString("onboarding_title_5", R.string.onboarding_title_5)
        };

        customDescs = new String[] {
            LocaleController.getString("onboarding_desc_1", R.string.onboarding_desc_1),
            LocaleController.getString("onboarding_desc_2", R.string.onboarding_desc_2),
            LocaleController.getString("onboarding_desc_3", R.string.onboarding_desc_3),
            LocaleController.getString("onboarding_desc_4", R.string.onboarding_desc_4),
            LocaleController.getString("onboarding_desc_5", R.string.onboarding_desc_5)
        };

        return super.onFragmentCreate();
    }

    @Override
    public View createView(Context context) {
        // Контейнери асосии экран
        fragmentView = new FrameLayout(context);
        
        // Ин ҷо дизайну View-ҳои Интрои худатро месозӣ ва пайваст мекунӣ.
        // Масалан тугмаи "Start" ё "Skip"

        return fragmentView;
    }

    // ТАНҲО ҲАМИН ФУНКСИЯРО БАРОИ ГУЗАРИШ БА КИСМИ ЗАВОДӢ ПАХШ МЕКУНӢ:
    public void openFactoryIntro() {
        // Ин фармон рост ба Интрои заводии Дуров (IntroActivity) мебарад
        presentFragment(new IntroActivity(), true);
    }
}
