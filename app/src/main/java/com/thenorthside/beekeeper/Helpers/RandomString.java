package com.thenorthside.beekeeper.Helpers;

import java.util.Random;

public class RandomString {

    private final String Letters = "abcdefghijklmopqrstuvwxyz";
    private final String Numbers = "0123456789";
    private final char[] ALPHANUMERIC = (Letters+Letters.toUpperCase()+Numbers).toCharArray();
    private final char[] ALPHANUMERIC2 = (Numbers).toCharArray();


    public String generateAlphaNumeric(int length){

        StringBuilder res = new StringBuilder();

        for (int i = 0; i<length; i++){

            res.append(ALPHANUMERIC[new Random().nextInt(ALPHANUMERIC.length)]);

        }
        return res.toString();

    }

    public String generateId(int length){

        StringBuilder res = new StringBuilder();

        for (int i = 0; i<length; i++){

            res.append(ALPHANUMERIC[new Random().nextInt(ALPHANUMERIC.length)]);

        }
        return res.toString();

    }

    public String generateNumeric(int length){

        StringBuilder res = new StringBuilder();

        for (int i = 0; i<length; i++){

            res.append(ALPHANUMERIC2[new Random().nextInt(ALPHANUMERIC2.length)]);

        }
        return res.toString();

    }

}
