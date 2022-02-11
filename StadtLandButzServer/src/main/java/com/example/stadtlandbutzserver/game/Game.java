package com.example.stadtlandbutzserver.game;

import java.util.ArrayList;

public class Game {
    private static ArrayList<String> categories = new ArrayList<>();

    public static void setCategories(ArrayList<String> categories) {
        Game.categories = categories;
    }

    public static ArrayList<String> getCategories() {
        return categories;
    }
}
