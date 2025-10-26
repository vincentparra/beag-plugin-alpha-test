package com.rocs.beag.plugin.test.human;


import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.readAllLines;
import static java.nio.file.Files.write;

public class BathroomStalls {

    public static void main(String... args) throws IOException {
        String fileName = "test";

        Path file = new File("src/main/resources/bathroom/" + fileName + ".in.txt").toPath();

        List<String> inputFile = readAllLines(file, StandardCharsets.UTF_8);

        int noTests = Integer.parseInt(inputFile.get(0));

        List<String> outputList = new ArrayList<>();
        for (int i=0; i<=noTests; i++) {
            String line[] = inputFile.get(i).split("\\s");
            int noStalls = Integer.parseInt(line[0]);
            int noPeople = Integer.parseInt(line[1]);
            BigInteger test = new BigInteger("1000000000000000000");
        }

        Path outFile = new File("src/main/resources/bathroom/" + fileName + ".out.txt").toPath();
//        write(outFile, outputList, StandardCharsets.UTF_8);
    }
}
