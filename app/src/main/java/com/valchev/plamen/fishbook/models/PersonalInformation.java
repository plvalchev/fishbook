package com.valchev.plamen.fishbook.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    public String birthDate;
    public ArrayList<FishingRegion> topFishingRegions;
    public ArrayList<Specie> mostChasedSpecies;
    public ArrayList<FishingMethod> fishingMethods;

    public PersonalInformation() {


    }
}
