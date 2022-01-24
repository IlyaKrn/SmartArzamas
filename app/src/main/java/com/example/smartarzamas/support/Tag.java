package com.example.smartarzamas.support;

import java.util.ArrayList;

// класс с категориями
public class Tag {

    // строки категорий
    public static final String PITS_ON_ROADS = "Ямы на дорогах";
    public static final String PUDDLES = "Лужи";
    public static final String OTHER = "Другое";
    public static final String SIGHTS = "Достопримечательности";
    public static final String SNOW = "Неубранный снег";

    // получение списка всеж категорий
    public static ArrayList<String> getAllTags() {
        ArrayList<String> arr = new ArrayList<>();
        arr.add(PITS_ON_ROADS);
        arr.add(PUDDLES);
        arr.add(OTHER);
        arr.add(SIGHTS);
        arr.add(SNOW);

        return arr;
    }

}
