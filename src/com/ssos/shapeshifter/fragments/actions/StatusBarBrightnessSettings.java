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

import android.app.ActionBar;
import android.os.Bundle;
import android.content.Context;
import com.android.settings.R;

import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class StatusBarBrightnessSettings extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.brightness_control);

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setTitle(getResources().getString(R.string.status_bar_toggle_brightness));
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.brightness_control);

}
