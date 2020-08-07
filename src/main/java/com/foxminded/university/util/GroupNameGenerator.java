package com.foxminded.university.util;

import java.util.Random;

public class GroupNameGenerator{

    private static Random rnd = new Random();

    public static String getRandomName(){
        return GroupNameGenerator.getSymbolString(2) + "-" + GroupNameGenerator.getDigitString(2);
    }

    static String getSymbolString(int n)
    {
        String symbolString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = rnd.nextInt(symbolString.length());
            sb.append(symbolString.charAt(index));
        }
        return sb.toString();
    }

    static String getDigitString(int n)
    {
        String symbolString = "0123456789";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = rnd.nextInt(symbolString.length());
            sb.append(symbolString.charAt(index));
        }
        return sb.toString();
    }
}
