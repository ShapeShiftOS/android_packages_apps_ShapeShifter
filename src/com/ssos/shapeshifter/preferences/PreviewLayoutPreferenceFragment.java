/*
 * Copyright (C) 2021 ShapeShiftOS
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

package com.ssos.shapeshifter.preferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public abstract class PreviewLayoutPreferenceFragment extends SettingsPreferenceFragment {
    public abstract void commit();

    public abstract int getPreviewItemResIds();

    public abstract int getPreviewSampleResIds();

    public abstract int getPreviewTitleIds();

    public int getActivityLayoutResId() {
        return R.layout.asus_style_activity;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        ViewGroup viewGroup2 = (ViewGroup) onCreateView.findViewById(android.R.id.list_container);
        viewGroup2.removeAllViews();
        View inflate = layoutInflater.inflate(getActivityLayoutResId(), viewGroup2, false);
        viewGroup2.addView(inflate);
        LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.preview_ui);
        if (linearLayout != null) {
            linearLayout.addView(layoutInflater.inflate(getPreviewSampleResIds(), (ViewGroup) linearLayout, false));
        }
        TextView textView = (TextView) inflate.findViewById(R.id.preview_item_label);
        if (textView != null) {
            textView.setText(getPreviewTitleIds());
        }
        LinearLayout linearLayout2 = (LinearLayout) inflate.findViewById(R.id.preview_item_content);
        if (linearLayout2 != null) {
            linearLayout2.addView(layoutInflater.inflate(getPreviewItemResIds(), (ViewGroup) linearLayout2, false));
        }
        Button button = (Button) inflate.findViewById(R.id.button_apply);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {

                public final void onClick(View view) {
                    PreviewLayoutPreferenceFragment.this.lightningApply(view);
                }
            });
        }
        return onCreateView;
    }

    public void lightningApply(View view) {
        commit();
    }
}
