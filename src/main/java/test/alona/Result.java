package test.alona;

import java.util.ArrayList;
import java.util.List;

public class Result {
    public List<String> getLines() {
        return lines;
    }

    public List<String> getWords() {
        return words;
    }

    private List<String> lines = new ArrayList<String>();
    private List<String> words = new ArrayList<String>();


    public void addLIne(String line, String word){
        lines.add(line);
        words.add(word);
    }
}
