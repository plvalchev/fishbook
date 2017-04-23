package com.valchev.plamen.fishbook.home;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.google.firebase.database.DatabaseError;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.models.PersonalInformation;
import com.valchev.plamen.fishbook.models.User;

/**
 * Created by admin on 23.4.2017 г..
 */

public class SettingsFragment extends PreferenceFragment {

    protected FishbookUser mFishBookUser;
    protected User mUserData;
    protected EditTextPreference mNamePreference;

    public SettingsFragment() {

        mUserData = new User();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        mNamePreference = (EditTextPreference) findPreference("pref_display_name");

        initUserData();

        mFishBookUser = FishbookUser.getCurrentUser();

        mFishBookUser.addUserValueEventListener(new FishbookUser.UserValueEventListener() {

            @Override
            public void onDataChange(User user) {

                mUserData = user;

                initUserData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mNamePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                String name = (String) newValue;

                if( mUserData.personalInformation == null ) {

                    mUserData.personalInformation = new PersonalInformation();
                }

                mUserData.personalInformation.name = name;

                mFishBookUser.saveUserData();

                return false;
            }
        });
    }

    public void initUserData() {

        String displayName = mUserData.getDisplayName();

        mNamePreference.setText( displayName );
        mNamePreference.setDefaultValue( displayName );
        mNamePreference.setSummary( displayName );
    }

}
