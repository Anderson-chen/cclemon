package org.cclemon;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        System.out.println(CollectionUtils.isEmpty(new ArrayList<>()));
        System.out.println("Hello core !");
    }
}