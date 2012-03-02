/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.settings.profiles;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfilesPreferenceCB extends CheckBoxPreference {
    private static final String TAG = ProfilesPreferenceCB.class.getSimpleName();
    private static final float DISABLED_ALPHA = 0.4f;
    private final SettingsPreferenceFragment mFragment;
    private final Bundle mSettingsBundle;

    private ImageView mProfilesSettingsButton;
    private TextView mTitleText;
    private TextView mSummaryText;
    private View mProfilesPref;

    // constant value that can be used to check return code from sub activity.
    private static final int PROFILE_DETAILS = 1;

    private final OnClickListener mPrefOnclickListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (!isEnabled()) {
                return;
            }
            if (isChecked()) {
                setChecked(false);
            } else {
                setChecked(true);
                //callChangeListener(getKey()); //disable for now
            }
        }
    };

    public ProfilesPreferenceCB(SettingsPreferenceFragment fragment, Bundle settingsBundle) {
        super(fragment.getActivity(), null, R.style.ProfilesPreferenceStyleCheckBox);
        setLayoutResource(R.layout.preference_profiles);
        setWidgetLayoutResource(R.layout.preference_profiles_widget_checkbox);
        mFragment = fragment;
        mSettingsBundle = settingsBundle;
        //TODO: updateSummary();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mProfilesPref = view.findViewById(R.id.profiles_pref);
        mProfilesPref.setOnClickListener(mPrefOnclickListener);
        mProfilesSettingsButton = (ImageView)view.findViewById(R.id.profiles_settings);
        mTitleText = (TextView)view.findViewById(android.R.id.title);
        mSummaryText = (TextView)view.findViewById(android.R.id.summary);

        if (mSettingsBundle != null) {
            mProfilesSettingsButton.setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            try {
                                startProfileConfigActivity();
                            } catch (ActivityNotFoundException e) {
                                // If the settings activity does not exist, we can just
                                // do nothing...
                            }
                        }
                    });
        }
        if (mSettingsBundle == null) {
            mProfilesSettingsButton.setVisibility(View.GONE);
        } else {
            updatePreferenceViews();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updatePreferenceViews();
    }

    private void updatePreferenceViews() {
        final boolean checked = isChecked();
        if (mProfilesSettingsButton != null) {
            mProfilesSettingsButton.setEnabled(true);
            mProfilesSettingsButton.setClickable(true);
            mProfilesSettingsButton.setFocusable(true);
        } else {
            mProfilesSettingsButton.setEnabled(false);
            mProfilesSettingsButton.setClickable(false);
            mProfilesSettingsButton.setFocusable(false);
            mProfilesSettingsButton.setAlpha(DISABLED_ALPHA);
        }
        if (mTitleText != null) {
            mTitleText.setEnabled(true);
        }
        if (mSummaryText != null) {
            mSummaryText.setEnabled(checked);
        }
        if (mProfilesPref != null) {
            mProfilesPref.setEnabled(true);
            mProfilesPref.setLongClickable(checked);
            final boolean enabled = isEnabled();
            mProfilesPref.setOnClickListener(enabled ? mPrefOnclickListener : null);
            if (!enabled) {
                mProfilesPref.setBackgroundColor(0);
            }
        }
    }

    private void startProfileConfigActivity() {
        PreferenceActivity pa = (PreferenceActivity) mFragment.getActivity();
        pa.startPreferencePanel(ProfileConfig.class.getName(), mSettingsBundle,
                R.string.profile_profile_manage, null, mFragment, PROFILE_DETAILS);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
    }

}