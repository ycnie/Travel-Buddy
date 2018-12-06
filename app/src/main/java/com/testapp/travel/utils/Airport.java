package com.testapp.travel.utils;

import java.util.Arrays;
import java.util.List;

public class Airport {
    //    public static String[] placesName = {"San Francisco", "New York"};
//    public static String[]  placesAirport = {"sfoa", "nyca"};

    public static String[] placesName = {"San Francisco", "New York", "Seattle", "Sydney", "Agra", "Abu Dhabi", "Toronto", "Paris", "Barcelona", "Chicago", "Shangai", "Montreal", "Kuala Lumpur"};

    public static String[] placesAirport = {"sfoa", "nyca", "seaa", "syda", "agr", "auha", "ytoa", "pari", "bcn", "chia", "csha", "ymqa", "kul"};


    public static String getAirport(String cityName) {
        List<String> abcd = Arrays.asList(placesName);
        int i = abcd.indexOf(cityName);

        return placesAirport[i].toString();

    }
}
