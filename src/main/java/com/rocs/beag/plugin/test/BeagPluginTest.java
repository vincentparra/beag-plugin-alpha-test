package com.rocs.beag.plugin.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BeagPluginTest {
    public static void main(String[] args) {
        List<String> stringList = new ArrayList<>();
        stringList.add("Apple");
        stringList.add("Anything");
        stringList.add("but");
        stringList.add("simple");

        List<String> startsWithA = stringList.stream().filter((s) -> s.contains("A")).collect(Collectors.toList());
        startsWithA.forEach(System.out::println);
    }
}
