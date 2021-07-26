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

import android.content.Context;
import android.content.om.IOverlayManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.ssos.shapeshifter.preferences.PreviewLayoutPreferenceFragment;

import java.util.Arrays;
import java.util.stream.IntStream;

public class StyleSystemColorPreferenceFragment extends PreviewLayoutPreferenceFragment implements View.OnClickListener {
    public static final String[] ACCENTS = {
            "com.android.theme.color.cinnamon",
            "com.android.theme.color.black",
            "com.android.theme.color.green",
            "com.android.theme.color.ocean",
            "com.android.theme.color.space",
            "com.android.theme.color.orchid",
            "com.android.theme.color.asus"
    };
    public static final int[] BUTTON_IDS = {
            R.id.radio_1,
            R.id.radio_2,
            R.id.radio_3,
            R.id.radio_4,
            R.id.radio_5,
            R.id.radio_6,
            R.id.radio_7,
            R.id.radio_8
    };
    public static final int[] PREVIEW_LAYOUTS = {
            R.layout.style_preview_sample_color_cinnamon,
            R.layout.style_preview_sample_color_black,
            R.layout.style_preview_sample_color_green,
            R.layout.style_preview_sample_color_ocean,
            R.layout.style_preview_sample_color_space,
            R.layout.style_preview_sample_color_orchid,
            R.layout.style_preview_sample_color_asus
    };
    private static final int[] COLOR_OVERLAY = {
            -1,
            R.color.accent_color_default_overlay_color,
            R.color.accent_color_cinnamon_overlay_color,
            R.color.colorBlack,
            R.color.accent_color_green_overlay_color,
            R.color.accent_color_ocean_overlay_color,
            R.color.accent_color_space_overlay_color,
            R.color.accent_color_orchid_overlay_color,
            R.color.accent_color_asus_overlay_color
    };
    private static final int[] DRAWABLE_COLOR_OVERLAY = {
            -1,
            R.drawable.preview_sample_system_color_default,
            R.drawable.preview_sample_system_color_cinnamon,
            R.drawable.preview_sample_system_color_black,
            R.drawable.preview_sample_system_color_green,
            R.drawable.preview_sample_system_color_ocean,
            R.drawable.preview_sample_system_color_space,
            R.drawable.preview_sample_system_color_orchid,
            R.drawable.preview_sample_system_color_asus
    };
    private Button applyBt;
    private IOverlayManager overlayManager;
    private RadioGroup radioGroupDown;
    private RadioGroup radioGroupUp;
    private ImageView sampleContent;
    private int recentId = 1;

    public static void setDefaultAccentColor(IOverlayManager overlayManager) {
        Arrays.stream(ACCENTS).forEachOrdered(accent -> {
            try {
                overlayManager.setEnabled(accent, false, UserHandle.USER_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    public static void enableAccentColor(IOverlayManager overlayManager, String accentPicker) {
        try {
            setDefaultAccentColor(overlayManager);
            overlayManager.setEnabled(accentPicker, true, UserHandle.USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void handleOverlays(String packageName, Boolean state, IOverlayManager overlayManager) {
        try {
            overlayManager.setEnabled(packageName, state, UserHandle.USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }

    @Override
    public int getPreviewSampleResIds() {
        for (int i = 0; i < ACCENTS.length; i++) {
            if (com.android.internal.util.ssos.Utils.isThemeEnabled(ACCENTS[i])) {
                return PREVIEW_LAYOUTS[i];
            }
        }
        return R.layout.style_preview_sample_color;
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
        overlayManager = IOverlayManager.Stub.asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));
        recentId = getIdFromPkg();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = super.onCreateView(layoutInflater, viewGroup, bundle);
        applyBt = view.findViewById(R.id.button_apply);
        sampleContent = view.findViewById(R.id.preview_sample_content);
        tintPreview(recentId);
        radioGroupUp = view.findViewById(R.id.radiogroup_up);
        radioGroupDown = view.findViewById(R.id.radiogroup_down);
        radioGroupUp.clearCheck();
        radioGroupDown.clearCheck();

        RadioButton[] radioButtons = new RadioButton[BUTTON_IDS.length];
        IntStream.range(0, BUTTON_IDS.length).forEachOrdered(i -> {
            radioButtons[i] = view.findViewById(BUTTON_IDS[i]);
            radioButtons[i].setOnClickListener(this);
            if (recentId == i + 1) {
                radioButtons[i].toggle();
            }
        });
        return view;
    }

    @Override
    public void commit() {
        if (recentId == 1) {
            setDefaultAccentColor(overlayManager);
        }
        IntStream.range(0, ACCENTS.length).filter(i -> recentId == i + 2)
                 .findFirst().ifPresent(i -> enableAccentColor(overlayManager, ACCENTS[i]));
    }

    @Override
    public int getHelpResource() {
        return R.string.prebuilt_accents_title;
    }

    public void onClick(View view) {
        IntStream.range(0, 4).filter(i -> view.getId() == BUTTON_IDS[i]).forEachOrdered(i -> {
            recentId = i + 1;
            tintPreview(i + 1);
            tintButton(recentId);
            radioGroupDown.clearCheck();
        });
        IntStream.range(4, 8).filter(i -> view.getId() == BUTTON_IDS[i]).forEachOrdered(i -> {
            recentId = i + 1;
            tintPreview(i + 1);
            tintButton(recentId);
            radioGroupUp.clearCheck();
        });
    }

    private int getIdFromPkg() {
        for (int i = 0; i < ACCENTS.length; i++) {
            if (com.android.internal.util.ssos.Utils.isThemeEnabled(ACCENTS[i])) {
                return i + 2;
            }
        }
        return 1;
    }

    public void tintPreview(int i) {
        sampleContent.setImageResource(DRAWABLE_COLOR_OVERLAY[i]);
    }

    public void tintButton(int i) {
        if (applyBt != null && i > 0 && i < COLOR_OVERLAY.length) {
            applyBt.getBackground().setColorFilter(getContext().getColor(COLOR_OVERLAY[i]), PorterDuff.Mode.SRC);
        }
    }
}
