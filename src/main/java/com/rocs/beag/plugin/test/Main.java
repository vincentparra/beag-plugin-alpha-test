package com.rocs.beag.plugin.test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static List<String> todos = new ArrayList<>();
    static int choices;
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {

        while(true){
            System.out.println("TODO List Manager");
            System.out.println("1: Add new item");
            System.out.println("2: View item");
            System.out.println("3: Delete item");
            choices = scanner.nextInt();
            scanner.nextLine();
            switch (choices){
                case 1 :
                    System.out.println("please add an item: ");
                    String items = scanner.nextLine();
                    addItem(items);
                    break;
                case 2 :
                    System.out.println("ITEM in inventory");
                    for(String item : todos){
                        System.out.println(item);
                    }
                    break;
                case 3 :
                    for(String item : todos){
                        System.out.println(item);
                    }
                    System.out.print("Enter Item to delete: ");
                    String removeItem = scanner.nextLine();
                    removeItem(removeItem);
                    break;
            }
        }
    }
    static void addItem(String item){
        todos.add(item);
    }
    static void removeItem(String item){
        todos.remove(item);
    }
}