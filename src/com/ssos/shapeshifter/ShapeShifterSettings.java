/*
 * Copyright (C) 2022 ShapeShiftOS
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

package com.ssos.shapeshifter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.viewpager.widget.ViewPager;

import com.ssos.shapeshifter.tabs.ActionsTab;
import com.ssos.shapeshifter.tabs.InterfaceTab;
import com.ssos.shapeshifter.tabs.StatusBarTab;
import com.ssos.shapeshifter.tabs.LockScreenTab;
import com.ssos.shapeshifter.tabs.SystemMiscTab;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.internal.logging.nano.MetricsProto;

import com.bottombar.navigation.material.bottomnavigation.BottomNavigationView;
import com.bottombar.navigation.material.bottomnavigation.LabelVisibilityMode;

import java.util.ArrayList;
import java.util.List;

public class ShapeShifterSettings extends SettingsPreferenceFragment {

    private static final String TAG = "ShapeShifterSettings";
    Context mContext;

    ActionsTab firstFragment = new ActionsTab();
    InterfaceTab secondFragment = new InterfaceTab();
    StatusBarTab thirdFragment = new StatusBarTab();
    LockScreenTab fourthFragment = new LockScreenTab();
    SystemMiscTab fifthFragment = new SystemMiscTab();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Resources res = getResources();
        Window win = getActivity().getWindow();
        mContext = getActivity();

        getActivity().setTitle(R.string.shapeshifter_title);
        View view = inflater.inflate(R.layout.shapeshifter, container, false);

        final BottomNavigationView bottomNavigation = (BottomNavigationView) view.findViewById(R.id.bottomNavigationView);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
	    @Override
	    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == bottomNavigation.getSelectedItemId()) {
                    return false;
                } else {
                    if (item.getItemId() == R.id.actions) {
                        switchFragment(firstFragment);
                    } else if (item.getItemId() == R.id.tab_ssos_interface) {
                        switchFragment(secondFragment);
                    } else if (item.getItemId() == R.id.statusbar) {
                        switchFragment(thirdFragment);
                    } else if (item.getItemId() == R.id.lockscreen) {
                        switchFragment(fourthFragment);
                    } else if (item.getItemId() == R.id.misc) {
                        switchFragment(fifthFragment);
                    }
                    return true;
	        }
	    }
        });

        bottomNavigation.setSelectedItemId(R.id.actions);
        switchFragment(firstFragment);
        bottomNavigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        return view;
    }

    private void switchFragment(Fragment frag) {
        getFragmentManager().beginTransaction().replace(R.id.flFragment, frag).commit();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }
}
