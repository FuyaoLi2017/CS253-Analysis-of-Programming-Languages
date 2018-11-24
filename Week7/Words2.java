import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class Words2 implements Function<String, List<String>> {

    @Override
    public List<String> apply(String filepath) {
        try (BufferedReader stopWordsReader = new BufferedReader(new FileReader("../stop_words.txt"));
             BufferedReader inputReader = new BufferedReader(new FileReader(filepath))) {
            // Read all stop words into set
            Set<String> stopWords = new HashSet<>();
            String line;
            while ((line = stopWordsReader.readLine()) != null) {
                for (String word : line.split(",")) {
                    stopWords.add(word);
                }
            }
            for (char c = 'a'; c <= 'z'; c++) {
                stopWords.add(String.valueOf(c));
            }
            List<String> filteredWords = new ArrayList<>();
            while ((line = inputReader.readLine()) != null) {
                String[] split = line.toLowerCase().replaceAll("[^a-zA-Z\\d\\s]", " ").split("\\s+");
                for (int i = 0; i < split.length; i++) {
                    if (stopWords.contains(split[i])) {
                        continue;
                    }
                    else filteredWords.add(split[i]);
                }
            }
            return filteredWords;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}