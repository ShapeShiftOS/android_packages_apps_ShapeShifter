/*
 * Copyright (C) 2021 ShapeShiftOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ssos.shapeshifter.fragments.ui;

import static android.os.UserHandle.USER_SYSTEM;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.settings.R;
import com.ssos.shapeshifter.preferences.PreviewLayoutPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StyleSystemColorPreferenceFragment extends PreviewLayoutPreferenceFragment implements View.OnClickListener {
    private static final int[] COLOR_OVERLAY = {-1, R.color.accent_color_default_overlay_color, R.color.accent_color_cinnamon_overlay_color, R.color.colorBlack, R.color.accent_color_green_overlay_color, R.color.accent_color_ocean_overlay_color, R.color.accent_color_space_overlay_color, R.color.accent_color_orchid_overlay_color, R.color.accent_color_asus_overlay_color};
    private static final int[] DRAWABLE_COLOR_OVERLAY = {-1, R.drawable.preview_sample_system_color_default, R.drawable.preview_sample_system_color_cinnamon, R.drawable.preview_sample_system_color_black, R.drawable.preview_sample_system_color_green, R.drawable.preview_sample_system_color_ocean, R.drawable.preview_sample_system_color_space, R.drawable.preview_sample_system_color_orchid, R.drawable.preview_sample_system_color_asus};
    private Button applyBt;
    private List<String> labels = new ArrayList();
    private IOverlayManager mOverlayManager;
    private PackageManager mPackageManager;
    private RadioButton mRb1;
    private RadioButton mRb2;
    private RadioButton mRb3;
    private RadioButton mRb4;
    private RadioButton mRb5;
    private RadioButton mRb6;
    private RadioButton mRb7;
    private RadioButton mRb8;
    private RadioGroup mRgDown;
    private RadioGroup mRgUp;
    private ImageView mSampleContent;
    private WeakReference<Context> mWeakContext;
    private List<String> pkgs = new ArrayList();
    private int recentId = 1;

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }

    @Override
    public int getPreviewSampleResIds() {
        if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.cinnamon")) {
            return R.layout.style_preview_sample_color_cinnamon;
        } else if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.black")) {
            return R.layout.style_preview_sample_color_black;
        } else if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.green")) {
            return R.layout.style_preview_sample_color_green;
        } else if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.ocean")) {
            return R.layout.style_preview_sample_color_ocean;
        } else if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.space")) {
            return R.layout.style_preview_sample_color_space;
        } else if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.orchid")) {
            return R.layout.style_preview_sample_color_orchid;
        } else if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.asus")) {
            return R.layout.style_preview_sample_color_asus;
        } else {
            return R.layout.style_preview_sample_color;
        }
    }

    @Override
    public int getPreviewTitleIds() {
        return R.string.display_style_color_hint_title;
    }

    @Override
    public int getPreviewItemResIds() {
        return R.layout.style_preview_item_theme_color;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(R.string.prebuilt_accents_title);
        this.mPackageManager = getContext().getPackageManager();
        this.mWeakContext = new WeakReference<>(getContext());
        mOverlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE));
        this.recentId = getIdFromPkg();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        getContext();
        this.applyBt = (Button) onCreateView.findViewById(R.id.button_apply);
        this.mSampleContent = (ImageView) onCreateView.findViewById(R.id.preview_sample_content);
        tintPreview(this.recentId);
        this.mRgUp = (RadioGroup) onCreateView.findViewById(R.id.radiogroup_up);
        this.mRgDown = (RadioGroup) onCreateView.findViewById(R.id.radiogroup_down);
        this.mRgUp.clearCheck();
        this.mRgDown.clearCheck();
        RadioButton radioButton = (RadioButton) onCreateView.findViewById(R.id.radio_1);
        this.mRb1 = radioButton;
        radioButton.setOnClickListener(this);
        if (this.recentId == 1) {
            this.mRb1.toggle();
        }
        RadioButton radioButton2 = (RadioButton) onCreateView.findViewById(R.id.radio_2);
        this.mRb2 = radioButton2;
        radioButton2.setOnClickListener(this);
        if (this.recentId == 2) {
            this.mRb2.toggle();
        }
        RadioButton radioButton3 = (RadioButton) onCreateView.findViewById(R.id.radio_3);
        this.mRb3 = radioButton3;
        radioButton3.setOnClickListener(this);
        if (this.recentId == 3) {
            this.mRb3.toggle();
        }
        RadioButton radioButton4 = (RadioButton) onCreateView.findViewById(R.id.radio_4);
        this.mRb4 = radioButton4;
        radioButton4.setOnClickListener(this);
        if (this.recentId == 4) {
            this.mRb4.toggle();
        }
        RadioButton radioButton5 = (RadioButton) onCreateView.findViewById(R.id.radio_5);
        this.mRb5 = radioButton5;
        radioButton5.setOnClickListener(this);
        if (this.recentId == 5) {
            this.mRb5.toggle();
        }
        RadioButton radioButton6 = (RadioButton) onCreateView.findViewById(R.id.radio_6);
        this.mRb6 = radioButton6;
        radioButton6.setOnClickListener(this);
        if (this.recentId == 6) {
            this.mRb6.toggle();
        }
        RadioButton radioButton7 = (RadioButton) onCreateView.findViewById(R.id.radio_7);
        this.mRb7 = radioButton7;
        radioButton7.setOnClickListener(this);
        if (this.recentId == 7) {
            this.mRb7.toggle();
        }
        RadioButton radioButton8 = (RadioButton) onCreateView.findViewById(R.id.radio_8);
        this.mRb8 = radioButton8;
        radioButton8.setOnClickListener(this);
        if (this.recentId == 8) {
            this.mRb8.toggle();
        }
        return onCreateView;
    }

    public static final String[] ACCENTS = {
        "com.android.theme.color.cinnamon",
        "com.android.theme.color.black",
        "com.android.theme.color.green",
        "com.android.theme.color.ocean",
        "com.android.theme.color.space",
        "com.android.theme.color.orchid",
        "com.android.theme.color.asus"
    };

    @Override
    public void commit() {
        if (this.recentId == 1) {
            setDefaultAccentColor(mOverlayManager);
            this.recentId = 1;
        } else if (this.recentId == 2) {
            enableAccentColor(mOverlayManager, "com.android.theme.color.cinnamon");
            this.recentId = 2;
        } else if (this.recentId == 3) {
            enableAccentColor(mOverlayManager, "com.android.theme.color.black");
            this.recentId = 3;
        } else if (this.recentId == 4) {
            enableAccentColor(mOverlayManager, "com.android.theme.color.green");
            this.recentId = 4;
        } else if (this.recentId == 5) {
            enableAccentColor(mOverlayManager, "com.android.theme.color.ocean");
            this.recentId = 5;
        } else if (this.recentId == 6) {
            enableAccentColor(mOverlayManager, "com.android.theme.color.space");
            this.recentId = 6;
        } else if (this.recentId == 7) {
            enableAccentColor(mOverlayManager, "com.android.theme.color.orchid");
            this.recentId = 7;
        } else if (this.recentId == 8) {
            enableAccentColor(mOverlayManager, "com.android.theme.color.asus");
            this.recentId = 8;
        }
    }


    public static void setDefaultAccentColor(IOverlayManager overlayManager) {
        for (int i = 0; i < ACCENTS.length; i++) {
            String accent = ACCENTS[i];
            try {
                overlayManager.setEnabled(accent, false, USER_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void enableAccentColor(IOverlayManager overlayManager, String accentPicker) {
        try {
            setDefaultAccentColor(overlayManager);
            overlayManager.setEnabled(accentPicker, true, USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void handleOverlays(String packagename, Boolean state, IOverlayManager mOverlayManager) {
        try {
            mOverlayManager.setEnabled(packagename,
                    state, USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getHelpResource() {
        return R.string.prebuilt_accents_title;
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.radio_1) {
            this.recentId = 1;
            tintPreview(1);
            tintButton(this.recentId);
            this.mRgDown.clearCheck();
        } else if (id == R.id.radio_2) {
            this.recentId = 2;
            tintPreview(2);
            tintButton(this.recentId);
            this.mRgDown.clearCheck();
        } else if (id == R.id.radio_3) {
            this.recentId = 3;
            tintPreview(3);
            tintButton(this.recentId);
            this.mRgDown.clearCheck();
        } else if (id == R.id.radio_4) {
            this.recentId = 4;
            tintPreview(4);
            tintButton(this.recentId);
            this.mRgDown.clearCheck();
        } else if (id == R.id.radio_5) {
            this.recentId = 5;
            tintPreview(5);
            tintButton(this.recentId);
            this.mRgUp.clearCheck();
        } else if (id == R.id.radio_6) {
            this.recentId = 6;
            tintPreview(6);
            tintButton(this.recentId);
            this.mRgUp.clearCheck();
        } else if (id == R.id.radio_7) {
            this.recentId = 7;
            tintPreview(7);
            tintButton(this.recentId);
            this.mRgUp.clearCheck();
        } else if (id == R.id.radio_8) {
            this.recentId = 8;
            tintPreview(8);
            tintButton(this.recentId);
            this.mRgUp.clearCheck();
        }
    }

    private int getIdFromPkg() {
        if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.cinnamon")) {
            return 2;
        } else if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.black")) {
            return 3;
        } else if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.green")) {
            return 4;
        } else if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.ocean")) {
            return 5;
        } else if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.space")) {
            return 6;
        } else if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.orchid")) {
            return 7;
        } else if (com.android.internal.util.ssos.Utils.isThemeEnabled("com.android.theme.color.asus")) {
            return 8;
        } else {
            return 1;
        }
    }

    public void tintPreview(int i) {
        this.mSampleContent.setImageResource(DRAWABLE_COLOR_OVERLAY[i]);
    }

    public void tintButton(int i) {
        Button button = this.applyBt;
        if (button != null && i > 0 && i < COLOR_OVERLAY.length) {
            button.getBackground().setColorFilter(getContext().getColor(COLOR_OVERLAY[i]), PorterDuff.Mode.SRC);
        }
    }

}
