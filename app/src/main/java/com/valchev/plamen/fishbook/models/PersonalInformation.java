package com.valchev.plamen.fishbook.models;

import java.util.ArrayList;

/**
 * Created by admin on 9.4.2017 г..
 */

public class PersonalInformation {

    public enum Gender {

        MALE,
        FEMALE
    }

    public String name;
    public Gender gender;
    public ArrayList<FishingRegion> topFishingRegions;
    public ArrayList<Specie> mostChasedSpecies;
    public ArrayList<FishingMethod> fishingMethods;

    public PersonalInformation() {


    }

    public PersonalInformation(String name, Gender gender, ArrayList<FishingRegion> topFishingRegions, ArrayList<Specie> mostChasedSpecies, ArrayList<FishingMethod> fishingMethods) {
        this.name = name;
        this.gender = gender;
        this.topFishingRegions = topFishingRegions;
        this.mostChasedSpecies = mostChasedSpecies;
        this.fishingMethods = fishingMethods;
    }
}
