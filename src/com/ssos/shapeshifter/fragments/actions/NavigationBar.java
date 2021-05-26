/*
 * Copyright (C) 2020 ShapeShiftOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ssos.shapeshifter.fragments.actions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import com.ssos.support.preferences.SwitchPreference;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.hwkeys.ActionUtils;

import com.ssos.support.preferences.SystemSettingSwitchPreference;
import com.ssos.support.preferences.SecureSettingSwitchPreference;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class NavigationBar extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener, Indexable {

    private static final String BACK_GESTURE_HAPTIC = "back_gesture_haptic";
    private static final String SHOW_BACK_ARROW_GESTURE = "show_back_arrow_gesture";
    private static final String PIXEL_ANIMATION_NAVIGATION = "pixel_nav_animation";
    private static final String INVERT_NAVIGATION = "sysui_nav_bar_inverse";
    private static final String NAVBAR_VISIBILITY = "navbar_visibility";

    private SwitchPreference mNavbarVisibility;
    private SecureSettingSwitchPreference mShowBackArrowGesture;
    private SystemSettingSwitchPreference mBackGestureHaptic;
    private SystemSettingSwitchPreference mPixelAnimationNavigation;
    private SecureSettingSwitchPreference mInvertNavigation;

    private boolean mIsNavSwitchingMode = false;
    private Handler mHandler;
    private boolean mVibraSupported;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.navigation_bar);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();
        Context mContext = getContext();
        
        mVibraSupported = mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_vibrateOnIconAnimation);
        mPixelAnimationNavigation = findPreference(PIXEL_ANIMATION_NAVIGATION);
        mInvertNavigation = findPreference(INVERT_NAVIGATION);
        mBackGestureHaptic = findPreference(BACK_GESTURE_HAPTIC);
        mShowBackArrowGesture = findPreference(SHOW_BACK_ARROW_GESTURE);
        // On three button nav
        if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.internal.systemui.navbar.threebutton")) {
            mPixelAnimationNavigation.setSummary(getString(R.string.pixel_navbar_anim_summary));
            mInvertNavigation.setSummary(getString(R.string.navigation_bar_invert_layout_summary));
            mShowBackArrowGesture.setSummary(getString(R.string.unsupported_navigation_bar));
            mBackGestureHaptic.setSummary(getString(R.string.unsupported_navigation_bar));
            mShowBackArrowGesture.setEnabled(false);
            mBackGestureHaptic.setEnabled(false);
        // On two button nav
        } else if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.internal.systemui.navbar.twobutton")) {
            mPixelAnimationNavigation.setSummary(getString(R.string.pixel_navbar_anim_summary));
            mInvertNavigation.setSummary(getString(R.string.navigation_bar_invert_layout_summary));
            mShowBackArrowGesture.setSummary(getString(R.string.unsupported_navigation_bar));
            mBackGestureHaptic.setSummary(getString(R.string.unsupported_navigation_bar));
            mShowBackArrowGesture.setEnabled(false);
            mBackGestureHaptic.setEnabled(false);
        // On gesture nav
        } else {
            mShowBackArrowGesture.setSummary(getString(R.string.back_gesture_arrow_summary));
            mBackGestureHaptic.setSummary(getString(R.string.back_gesture_haptic_summary));
            if (mVibraSupported)
                mBackGestureHaptic.setEnabled(false);
            mPixelAnimationNavigation.setSummary(getString(R.string.unsupported_gestures));
            mInvertNavigation.setSummary(getString(R.string.gesture_invert_layout_summary));
            mPixelAnimationNavigation.setEnabled(false);
        }

        mNavbarVisibility = (SwitchPreference) findPreference(NAVBAR_VISIBILITY);

        boolean showing = Settings.System.getInt(getContentResolver(),
                Settings.System.FORCE_SHOW_NAVBAR,
                ActionUtils.hasNavbarByDefault(getActivity()) ? 1 : 0) != 0;
        updateBarVisibleAndUpdatePrefs(showing);
        mNavbarVisibility.setOnPreferenceChangeListener(this);

        mHandler = new Handler();

    }

    private void updateBarVisibleAndUpdatePrefs(boolean showing) {
        mNavbarVisibility.setChecked(showing);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mNavbarVisibility)) {
            if (mIsNavSwitchingMode) {
                return false;
            }
            mIsNavSwitchingMode = true;
            boolean showing = ((Boolean)newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.FORCE_SHOW_NAVBAR,
                    showing ? 1 : 0);
            updateBarVisibleAndUpdatePrefs(showing);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsNavSwitchingMode = false;
                }
            }, 1500);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider() {
            @Override
            public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                    boolean enabled) {
                final ArrayList<SearchIndexableResource> result = new ArrayList<>();
                final SearchIndexableResource sir = new SearchIndexableResource(context);
                sir.xmlResId = R.xml.navigation_bar;
                result.add(sir);
                return result;
            }

            @Override
            public List<String> getNonIndexableKeys(Context context) {
                final List<String> keys = super.getNonIndexableKeys(context);
                return keys;
            }
    };
}
