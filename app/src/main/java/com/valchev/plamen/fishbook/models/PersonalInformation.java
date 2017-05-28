package com.valchev.plamen.fishbook.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 9.4.2017 г..
 */

public class PersonalInformation implements Serializable {

    public enum Gender {

        MALE,
        FEMALE
    }

    public String name;
    public Gender gender;
    public String birthDate;
    public ArrayList<FishingRegion> topFishingRegions;
    public ArrayList<Specie> mostChasedSpecies;
    public ArrayList<FishingMethod> fishingMethods;

    public PersonalInformation() {


    }
}
