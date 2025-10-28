package com.rocs.beag.plugin.test.human.sirdonnie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SirDonnie {
    public static void main(String[] args)
    {
        List<String> names = new ArrayList<>();
        names.add("Jovan");
        names.add("Vince");
        names.add("Lance");

        List<String> graduating = names.stream()
                .filter((name)  -> name.equals("Vincent"))
                .collect(Collectors.toList());

        graduating.stream().forEach(System.out::println);
    }
}
