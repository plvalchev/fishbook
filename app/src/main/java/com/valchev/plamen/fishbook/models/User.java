package com.valchev.plamen.fishbook.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by admin on 9.4.2017 г..
 */

@IgnoreExtraProperties
public class User {

    public PersonalInformation personalInformation;
    public ArrayList<CoverPhoto> coverPhotos;
    public ArrayList<ProfilePicture> profilePictures;

    public User() {

    }

    public User(PersonalInformation personalInformation, ArrayList<CoverPhoto> coverPhotos, ArrayList<ProfilePicture> profilePictures) {
        this.personalInformation = personalInformation;
        this.coverPhotos = coverPhotos;
        this.profilePictures = profilePictures;
    }

    @Exclude
    public String getDisplayName() {

        String displayName = getDefaultDisplayName();

        if( personalInformation != null && personalInformation.name != null ) {

            displayName = personalInformation.name;
        }

        return displayName;
    }

    @Exclude
    public String getDefaultDisplayName() {

        PersonalInformation.Gender gender = PersonalInformation.Gender.MALE;

        if( personalInformation != null && personalInformation.gender != null ) {

            gender = personalInformation.gender;
        }

        String defaultDisplayName = gender == PersonalInformation.Gender.MALE ? "John Doe" : "Jane Doe";

        return defaultDisplayName;
    }

    @Exclude
    public String getTopFishingRegionsAsString() {

        String topFishingRegions = getDefaultTopFishingRegionsAsString();

        if( personalInformation != null && personalInformation.topFishingRegions != null ) {

            topFishingRegions = "";

            for (FishingRegion region: personalInformation.topFishingRegions) {

                if( !topFishingRegions.isEmpty() )
                    topFishingRegions += ", ";

                topFishingRegions += region.name;
            }
        }

        return topFishingRegions;
    }

    @Exclude
    public String getDefaultTopFishingRegionsAsString() {

        String defaultTopFishingRegions = "Unknown";

        return defaultTopFishingRegions;
    }

    @Exclude
    public String getMostChasedSpeciesAsStringAsString() {

        String mostChasedSpeciesAsString = getDefaultMostChasedSpeciesAsString();

        if( personalInformation != null && personalInformation.mostChasedSpecies != null ) {

            mostChasedSpeciesAsString = "";

            for (Specie specie: personalInformation.mostChasedSpecies) {

                if( !mostChasedSpeciesAsString.isEmpty() )
                    mostChasedSpeciesAsString += ", ";

                mostChasedSpeciesAsString += specie.name;
            }
        }

        return mostChasedSpeciesAsString;
    }

    @Exclude
    public String getDefaultMostChasedSpeciesAsString() {

        String defaultMostChasedSpecies = "Unknown";

        return defaultMostChasedSpecies;
    }

    @Exclude
    public String getFishingMethodsAsString() {

        String fishingMethodsAsString = getDefaultFishingMethodsAsString();

        if( personalInformation != null && personalInformation.fishingMethods != null ) {

            fishingMethodsAsString = "";

            for (FishingMethod method: personalInformation.fishingMethods) {

                if( !fishingMethodsAsString.isEmpty() )
                    fishingMethodsAsString += ", ";

                fishingMethodsAsString += method.name;
            }
        }

        return fishingMethodsAsString;
    }

    @Exclude
    public String getDefaultFishingMethodsAsString() {

        String defaultFishingMethods = "Unknown";

        return defaultFishingMethods;
    }
}
