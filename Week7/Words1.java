import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Words1 implements Function<String, List<String>> {

    @Override
    public List<String> apply(String filepath) {
        // Load stop words and input file
        try (Stream<String> stopWordsFile = Files.lines(Paths.get("../stop_words.txt"));
             Stream<String> inputFile = Files.lines(Paths.get(filepath))) {
            Set<String> stopWords = stopWordsFile.map(line -> line.split(",")).
                    flatMap(Arrays::stream).collect(Collectors.toCollection(HashSet::new));
            // Add all one-character words to stopWords
            for (char c = 'a'; c <= 'z'; c++) {
                stopWords.add(String.valueOf(c));
            }
            // transform the input file's lines to lower case and separate into words
            return inputFile.map(line -> line.toLowerCase().replaceAll("[^a-zA-Z\\d\\s]", " ").split("\\s+")).
                    flatMap(Arrays::stream).
                    filter(word -> !stopWords.contains(word)).
                    collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}