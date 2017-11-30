package test.alona;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LoomMain {
    // this map stores all the not patterned lines. Integer is the number of words in line
    private final static Map<Integer, Set<List<String>>> fileLines = new HashMap<Integer, Set<List<String>>>();

    // this map just created in order to reduce serach time,. Integer is the number of words in line
    private final static  Map<Integer, Set<String>> foundPatterns = new HashMap<Integer, Set<String>>();

    private final static Map<String, Result> results= new HashMap<String, Result>();

    private static final int PREAMBULA_LENGTH = "01-01-2012 19:45:00".length();

    public static void main(String[] args) {

        // reading file
        BufferedReader br = null;
        FileReader fr = null;

        try {

            fr = new FileReader(args[0]);
            br = new BufferedReader(fr);
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {

                // next two lines are the main functionality
                if (!findInPatterns(sCurrentLine)){
                    findInLinesMap(sCurrentLine);
                }

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {
            try {
                if (br != null) br.close();

                if (fr != null) fr.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // printing result
            printResults();

        }
    }

    private static void printResults() {
        for(Map.Entry<String, Result> entry: results.entrySet()){
            System.out.println("-----");
            Result result = entry.getValue();
            result.getLines().stream().forEach(line -> System.out.println(line));
            System.out.println("tThe changing word was: "+result.getWords().stream().collect(Collectors.joining(", ")));
        }
    }

    private static void findInLinesMap(String sCurrentLine) {

        String line = sCurrentLine.substring(PREAMBULA_LENGTH);
        String[] words = line.split(" ");
        // I split only once, on order not split the strings in further checks in findNewPattern
        Set<List<String>> oldLines= fileLines.get(line.length());
        boolean foundNewPettrnFlag = false;


        for (Iterator<List<String>> iterator = oldLines.iterator(); iterator.hasNext(); ) {
            List<String> strList = iterator.next();
            foundNewPettrnFlag = true;

            // if found matching pattern
            int wordNumber;
            if(( wordNumber = findNewPattern(strList, line)) !=-1){
                iterator.remove();

                createPattern(sCurrentLine, strList, wordNumber);

                // break the loop
                break;
            }
        }

        // if no previous pattern found
        if (!foundNewPettrnFlag){
            List<String> listedWords =  Arrays.asList(words);
            listedWords.add(sCurrentLine); // to preserve old line
            oldLines.add(listedWords);
        }

    }

    private static void createPattern(String sCurrentLine, List<String> strList, int wordNumber) {
        StringBuilder builder = new StringBuilder();
        Result result = new Result();

        for (int i=0; i<strList.size() -1; i++){
            if (i==wordNumber) {
                builder.append("[a-zA-Z]*");
                result.addLIne(sCurrentLine, strList.get(i));

            }
            else
                builder.append(strList.get(i));
            if (i != strList.size()-2)
                builder.append(" ");
        }

        results.put(builder.toString(), result);

        // put new pattern
        Set<String> patternsSet ;
        if (!foundPatterns.containsKey(strList.size() -1)){
            patternsSet = new HashSet<String>();
            foundPatterns.put(strList.size() -1, patternsSet);
        }
        else
            patternsSet = foundPatterns.get(strList.size() -1);
        patternsSet.add(builder.toString());
    }

    private static int findNewPattern(List<String> strList, String line) {
        String[] splittedLines = line.split(" ");
        int counter = 0, pointer = -1;
        for (int i=0; i<splittedLines.length && counter<2; i++) {
            // stps if counter greater than 1
            if (strList.get(i).equals(splittedLines[i]))
                counter++ ;
        }
        if (counter >1)
            pointer = -1;
        return pointer;
    }

    private static boolean findInPatterns(String sCurrentLine) {
        String line = sCurrentLine.substring(PREAMBULA_LENGTH);

        boolean flag = false;

        Set<String> set= foundPatterns.get(line.length());
        for (String pattern: set){
            if (line.matches(pattern)){
                Result result = results.get(pattern.toString());
                result.addLIne(sCurrentLine, extractTheDelta(line, pattern) );
                flag = true;
                break;
            }
        }
        return flag;
    }

    private static String extractTheDelta(String line, String pattern) {

        String[] lineSplited = line.split(" ");
        String[] patternSplited = pattern.split(" ");
        int i = 0;
        while (lineSplited[i].equals(patternSplited[i])) {
            i++;
        }
        return lineSplited[i];
    }
}
