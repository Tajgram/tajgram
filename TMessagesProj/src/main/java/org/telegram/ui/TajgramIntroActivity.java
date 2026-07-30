package org.telegram.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;

public class TajgramIntroActivity extends BaseFragment {

    private String[] customTitles;
    private String[] customDescs;

    private ViewPager viewPager;
    private TextView skipButton;
    private TextView startButton;

    @Override
    public boolean onFragmentCreate() {
        // Инициализацияи сатрҳо аз strings.xml
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
        // 1. Контейнери асосии экран
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(0xFF0F172A); // Ранги замина (Dark Navy)
        fragmentView = frameLayout;

        // 2. ViewPager барои варақзании 5 слайд
        viewPager = new ViewPager(context);
        viewPager.setAdapter(new IntroAdapter());
        frameLayout.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // 3. Тугмаи "Skip (X)" дар кунҷи болоии рост
        skipButton = new TextView(context);
        skipButton.setText(LocaleController.getString("skip_button", R.string.skip_button));
        skipButton.setTextColor(0xFF94A3B8);
        skipButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        skipButton.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));
        skipButton.setOnClickListener(v -> finishAndOpenLogin());
        frameLayout.addView(skipButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.RIGHT, 10, 10, 10, 0));

        // 4. Тугмаи "Start / Next" дар поён
        startButton = new TextView(context);
        startButton.setText("Next");
        startButton.setTextColor(Color.WHITE);
        startButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        startButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        startButton.setGravity(Gravity.CENTER);
        startButton.setBackgroundColor(0xFF2563EB); // Ранги кабуди тугма
        startButton.setPadding(AndroidUtilities.dp(24), AndroidUtilities.dp(12), AndroidUtilities.dp(24), AndroidUtilities.dp(12));
        
        startButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < customTitles.length - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            } else {
                finishAndOpenLogin();
            }
        });

        frameLayout.addView(startButton, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 32, 0, 32, 40));

        // Мониторинги тағйири саҳифаҳо
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                // Дар слайди охирин матни тугма "Start" мешавад
                if (position == customTitles.length - 1) {
                    startButton.setText(LocaleController.getString("start_button", R.string.start_button));
                } else {
                    startButton.setText("Next");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        return fragmentView;
    }

    private void finishAndOpenLogin() {
        // Гузариши мустақим ба экрани ворид кардани рақам ва забон
        presentFragment(new LoginActivity(), true);
    }

    public void openFactoryIntro() {
        presentFragment(new IntroActivity(), true);
    }

    // Адаптери слайдер барои намоиши матнҳо
    private class IntroAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return customTitles != null ? customTitles.length : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LinearLayout layout = new LinearLayout(container.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER);
            layout.setPadding(AndroidUtilities.dp(32), 0, AndroidUtilities.dp(32), 0);

            // Title
            TextView titleView = new TextView(container.getContext());
            titleView.setText(customTitles[position]);
            titleView.setTextColor(Color.WHITE);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            titleView.setGravity(Gravity.CENTER);
            layout.addView(titleView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 0, 0, 16));

            // Description
            TextView descView = new TextView(container.getContext());
            descView.setText(customDescs[position]);
            descView.setTextColor(0xFF94A3B8);
            descView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            descView.setGravity(Gravity.CENTER);
            layout.addView(descView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
@Override
public View createView(Context context) {
    // Ягона сатре, ки барои пайваст кардани файли шумо лозим аст:
    presentFragment(new TajgramIntroActivity(), true);
    
    return fragmentView = new FrameLayout(context);
    
    // Коди заводии поёниро бетағйир мегузоред...
}
