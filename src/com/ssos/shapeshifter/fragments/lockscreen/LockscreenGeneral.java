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

package com.ssos.shapeshifter.fragments.lockscreen;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import androidx.preference.*;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.internal.logging.nano.MetricsProto;

import android.hardware.biometrics.BiometricSourceType;
import android.hardware.fingerprint.FingerprintManager;

import com.ssos.support.preferences.SystemSettingSwitchPreference;
import com.android.internal.util.custom.FodUtils;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class LockscreenGeneral extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String LOCK_FP_ICON = "lock_fp_icon";

    private SystemSettingSwitchPreference mLockFPIcon;

    private boolean mHasFod;
    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lockscreen_general);
        PreferenceScreen prefScreen = getPreferenceScreen();
        PreferenceCategory overallPreferences = (PreferenceCategory) findPreference("fod_category");
        mResolver = getActivity().getContentResolver();
        Context mContext = getContext();

        boolean enableScreenOffFOD = getContext().getResources().
                getBoolean(com.android.internal.R.bool.config_supportScreenOffFod);
        Preference ScreenOffFODPref = (Preference) findPreference("fod_gesture");

        if (!enableScreenOffFOD){
            overallPreferences.removePreference(ScreenOffFODPref);
        }

        if (!getResources().getBoolean(com.android.internal.R.bool.config_supportsInDisplayFingerprint)) {
            prefScreen.removePreference(findPreference("fod_category"));
        }

        mLockFPIcon = findPreference(LOCK_FP_ICON);
 	FingerprintManager fingerprintManager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);
        mHasFod = FodUtils.hasFodSupport(mContext);

        if (fingerprintManager == null) {
            mLockFPIcon.setSummary(getString(R.string.unsupported_feature_summary));
            mLockFPIcon.setEnabled(false);
        } else if (!fingerprintManager.isHardwareDetected()) {
            mLockFPIcon.setSummary(getString(R.string.lock_fp_icon_no_fp_summary));
            mLockFPIcon.setEnabled(false);
        } else if (mHasFod) {
            mLockFPIcon.setSummary(getString(R.string.lock_fp_icon_fod_summary));
            mLockFPIcon.setEnabled(false);
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            mLockFPIcon.setSummary(getString(R.string.lock_fp_icon_rart_user_summary));
            mLockFPIcon.setEnabled(false);
        } else {
            mLockFPIcon.setSummary(getString(R.string.lock_fp_icon_summary));
            mLockFPIcon.setEnabled(true);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
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
                sir.xmlResId = R.xml.lockscreen_general;
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
