package com.testapp.travel.utils;

import java.util.Arrays;
import java.util.List;

public  class Airport {
    static String[] placesName = {"San Francisco", "New York"};
    static String[]  placesAirport = {"sfoa", "nyca"};

    public static String getAirport(String cityName){
        List<String> abcd  = Arrays.asList(placesName);
        int i = abcd.indexOf(cityName);

        return  placesAirport[i].toString();

    }
}
