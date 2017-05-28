package com.valchev.plamen.fishbook.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.widget.DatePicker;

import com.google.firebase.database.DatabaseError;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.utils.FishbookUtils;
import com.valchev.plamen.fishbook.models.FishingMethod;
import com.valchev.plamen.fishbook.models.FishingRegion;
import com.valchev.plamen.fishbook.models.PersonalInformation;
import com.valchev.plamen.fishbook.models.Specie;
import com.valchev.plamen.fishbook.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by admin on 23.4.2017 г..
 */

public class SettingsFragment extends PreferenceFragment {

    protected FishbookUser mFishBookUser;
    protected User mUserData;
    protected EditTextPreference mNamePreference;
    protected Preference mBirthDatePreference;
    protected SwitchPreference mGenderPreference;
    protected MultiSelectListPreference mMostChasedSpeciesPreference;
    protected MultiSelectListPreference mTopFishingRegionsPreference;
    protected MultiSelectListPreference mFishingMethodsPreference;

    public SettingsFragment() {

        mUserData = new User();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        mNamePreference = (EditTextPreference) findPreference("pref_display_name");
        mBirthDatePreference = (Preference) findPreference("pref_birth_date");
        mGenderPreference = (SwitchPreference) findPreference("pref_gender");
        mMostChasedSpeciesPreference = (MultiSelectListPreference) findPreference("pref_most_chased_species");
        mTopFishingRegionsPreference = (MultiSelectListPreference) findPreference("pref_top_fishing_regions");
        mFishingMethodsPreference = (MultiSelectListPreference) findPreference("pref_fishing_methods");

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

        initPreferences();
    }

    private void initPreferences() {

        initNamePreference();
        initBirthDatePreference();
        initGenderPreference();
        initMostChasedSpeciesPreference();
        initTopFishingRegionsPreference();
        initFishingMethodsPreference();
    }

    private void initUserData() {

        String displayName = mUserData.getDisplayName();

        mNamePreference.setText( displayName );
        mNamePreference.setDefaultValue( displayName );
        mNamePreference.setSummary( displayName );

        PersonalInformation personalInformation = mUserData.personalInformation;
        HashSet<String> mostChasedSpecies = null;
        HashSet<String> topFishingRegions = null;
        HashSet<String> fishingMethods = null;

        if( personalInformation != null) {

            if( personalInformation.birthDate != null ) {

                mBirthDatePreference.setSummary(personalInformation.birthDate);
            }

            if( personalInformation.gender != null ) {

                mGenderPreference.setChecked( personalInformation.gender == PersonalInformation.Gender.FEMALE );
            }

            if( personalInformation.mostChasedSpecies != null ) {

                mostChasedSpecies = new HashSet<>();

                for (Specie specie : personalInformation.mostChasedSpecies) {

                    mostChasedSpecies.add( specie.name );
                }
            }

            if( personalInformation.topFishingRegions != null ) {

                topFishingRegions = new HashSet<>();

                for(FishingRegion region : personalInformation.topFishingRegions) {

                    topFishingRegions.add( region.name );
                }
            }

            if( personalInformation.fishingMethods != null ) {

                fishingMethods = new HashSet<>();

                for(FishingMethod method : personalInformation.fishingMethods) {

                    fishingMethods.add( method.name );
                }
            }
        }

        mMostChasedSpeciesPreference.setSummary(mUserData.getMostChasedSpeciesAsStringAsString());

        if( mostChasedSpecies != null ) {

            mMostChasedSpeciesPreference.setValues(mostChasedSpecies);
        }

        mTopFishingRegionsPreference.setSummary(mUserData.getTopFishingRegionsAsString());

        if( topFishingRegions != null ) {

            mTopFishingRegionsPreference.setValues(topFishingRegions);
        }

        mFishingMethodsPreference.setSummary(mUserData.getFishingMethodsAsString());

        if( fishingMethods != null ) {

            mFishingMethodsPreference.setValues(fishingMethods);
        }
    }

    private void initNamePreference() {

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

    private void initBirthDatePreference() {

        mBirthDatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final Calendar calendar = Calendar.getInstance();
                String birthDate = mUserData.personalInformation != null ? mUserData.personalInformation.birthDate : null;

                calendar.setTime(FishbookUtils.stringToDate(birthDate));

                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                calendar.set(year, month, dayOfMonth);

                                if( mUserData.personalInformation == null ) {

                                    mUserData.personalInformation = new PersonalInformation();
                                }

                                mUserData.personalInformation.birthDate = FishbookUtils.dateToString(calendar.getTime());

                                mFishBookUser.saveUserData();

                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();

                return false;
            }
        });
    }

    private void initGenderPreference() {

        mGenderPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Boolean isFemale = (Boolean) newValue;

                if( mUserData.personalInformation == null ) {

                    mUserData.personalInformation = new PersonalInformation();
                }

                mUserData.personalInformation.gender = isFemale != null && isFemale.booleanValue() ? PersonalInformation.Gender.FEMALE : PersonalInformation.Gender.MALE;

                mFishBookUser.saveUserData();

                return false;
            }
        });

    }

    private void initMostChasedSpeciesPreference() {

        mMostChasedSpeciesPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                HashSet hashSet = (HashSet)newValue;
                Iterator stringIterator = hashSet.iterator();

                if( mUserData.personalInformation == null ) {

                    mUserData.personalInformation = new PersonalInformation();
                }

                mUserData.personalInformation.mostChasedSpecies = null;

                while ( stringIterator.hasNext() ) {

                    String specie = (String)stringIterator.next();

                    if( mUserData.personalInformation.mostChasedSpecies == null ) {

                        mUserData.personalInformation.mostChasedSpecies = new ArrayList<>();
                    }

                    mUserData.personalInformation.mostChasedSpecies.add(new Specie(specie));
                }

                mFishBookUser.saveUserData();

                return false;
            }
        });

    }

    private void initTopFishingRegionsPreference() {

        mTopFishingRegionsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                HashSet hashSet = (HashSet)newValue;
                Iterator stringIterator = hashSet.iterator();

                if( mUserData.personalInformation == null ) {

                    mUserData.personalInformation = new PersonalInformation();
                }

                mUserData.personalInformation.topFishingRegions = null;

                while ( stringIterator.hasNext() ) {

                    String region = (String)stringIterator.next();

                    if( mUserData.personalInformation.topFishingRegions == null ) {

                        mUserData.personalInformation.topFishingRegions = new ArrayList<>();
                    }

                    mUserData.personalInformation.topFishingRegions.add(new FishingRegion(region));
                }

                mFishBookUser.saveUserData();

                return false;
            }
        });

    }

    private void initFishingMethodsPreference() {

        mFishingMethodsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                HashSet hashSet = (HashSet)newValue;
                Iterator stringIterator = hashSet.iterator();

                if( mUserData.personalInformation == null ) {

                    mUserData.personalInformation = new PersonalInformation();
                }

                mUserData.personalInformation.fishingMethods = null;

                while ( stringIterator.hasNext() ) {

                    String method = (String)stringIterator.next();

                    if( mUserData.personalInformation.fishingMethods == null ) {

                        mUserData.personalInformation.fishingMethods = new ArrayList<>();
                    }

                    mUserData.personalInformation.fishingMethods.add(new FishingMethod(method));
                }

                mFishBookUser.saveUserData();

                return false;
            }
        });

    }

}
