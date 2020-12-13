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

package com.ssos.shapeshifter.fragments.system_misc;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.SearchIndexableResource;
import android.text.TextUtils;

import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.R;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import com.ssos.support.preferences.CustomSeekBarPreference;
import com.ssos.support.preferences.SystemSettingListPreference;
import com.ssos.support.preferences.SystemSettingMasterSwitchPreference;
import com.ssos.support.preferences.SystemSettingSwitchPreference;

import com.ssos.shapeshifter.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SearchIndexable
public class GeneralNotifications extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String KEY_EDGE_LIGHTNING = "pulse_ambient_light";
    private static final String NOTIFICATION_HEADERS = "notification_headers";
    private static final String CENTER_NOTIFICATION_HEADERS = "center_notification_headers";

    private SystemSettingSwitchPreference mCenterNotificationHeader;
    private SystemSettingSwitchPreference mNotificationHeader;
    private SystemSettingMasterSwitchPreference mEdgeLightning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_notifications);
        PreferenceScreen prefScreen = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

        mEdgeLightning = (SystemSettingMasterSwitchPreference)
                findPreference(KEY_EDGE_LIGHTNING);
        boolean enabled = Settings.System.getIntForUser(resolver,
                KEY_EDGE_LIGHTNING, 0, UserHandle.USER_CURRENT) == 1;
        mEdgeLightning.setChecked(enabled);
        mEdgeLightning.setOnPreferenceChangeListener(this);

        mNotificationHeader = findPreference(NOTIFICATION_HEADERS);
        mNotificationHeader.setChecked((Settings.System.getInt(resolver,
                Settings.System.NOTIFICATION_HEADERS, 1) == 1));
        mNotificationHeader.setOnPreferenceChangeListener(this);

        mCenterNotificationHeader = findPreference(CENTER_NOTIFICATION_HEADERS);
        mCenterNotificationHeader.setChecked((Settings.System.getInt(resolver,
                Settings.System.CENTER_NOTIFICATION_HEADERS, 1) == 1));
        mCenterNotificationHeader.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mEdgeLightning) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver, KEY_EDGE_LIGHTNING,
                    value ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mNotificationHeader) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.NOTIFICATION_HEADERS, value ? 1 : 0);
            Utils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mCenterNotificationHeader) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.CENTER_NOTIFICATION_HEADERS, value ? 1 : 0);
            Utils.showSystemUiRestartDialog(getContext());
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
                sir.xmlResId = R.xml.general_notifications;
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
