
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * In the program, we need to obey the kick forward programming style
 * @FileName Eight.java
 * @author  Fuyao Li
 * @Date 10/20/2018
 */
public class Eight {


    private static void readFile(String filePath, BiConsumer<String, BiConsumer> function) {
        String s;
        String inputFile = null;
        try{
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            // store the file file into String sb
            StringBuffer sb = new StringBuffer();
            //read the words line by line
            while((s = br.readLine()) != null) {
                sb.append(s);
                sb.append(" ");
            }
            function.accept(sb.toString(), simple(Eight::scan));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void filterChars(String inputFile, BiConsumer<String, BiConsumer> function) {
        inputFile = inputFile.toLowerCase();
        function.accept(inputFile, simple(Eight::removeStopWords));
    }

    // scan inputFile and split it
    private static void scan(String inputFile, BiConsumer<String[], BiConsumer> function) {
        String[] split = inputFile.split("[^a-z]+");
        function.accept(split, simple(Eight::frequency));
    }

    // remove stop words
    private static void removeStopWords(String[] split, BiConsumer<List<String>, BiConsumer> function) {
        Set<String> stop_words = new HashSet<>();
        List<String> list = new ArrayList<>();
        // read the stop_words.txt file and store the stop words in a list
        String s;
        try {
            BufferedReader br = new BufferedReader(new FileReader("../stop_words.txt"));
            //store the stop_words file into String sb
            StringBuffer sb = new StringBuffer();
            //read the words line by line
            while((s = br.readLine()) != null) {
                sb.append(s);
            }
            String str = sb.toString();
            // sparse the individual words and put them into map
            String[] stopWordArray = str.split(",");
            for(int i = 0; i < stopWordArray.length; i++){
                stop_words.add(stopWordArray[i]);
            }
            // add individual letters
            for (char c = 'a'; c <= 'z'; c++) {
                stop_words.add(String.valueOf(c));
            }
            //remove stop words
            for (int i = 0; i < split.length; i++) {
                if (stop_words.contains(split[i])) {
                    continue;
                }
                else list.add(split[i]);
            }
            function.accept(list, simple(Eight::sort));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // count the frequency of each word
    private static void frequency(List<String> words, BiConsumer<Map<String, Integer>, BiConsumer> function) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < words.size(); i++) {
            if (map.containsKey(words.get(i))) {
                map.put(words.get(i), map.get(words.get(i)) + 1);
            }
            else map.put(words.get(i), 1);
        }
        function.accept(map, simple(Eight::printAll));
    }

    // sort the map
    private static void sort(Map<String, Integer> map, BiConsumer<List<Map.Entry<String, Integer>>,Consumer<?>> function) {
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        //realize the compare by defining a comparator
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            // sort in a decreasing order
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        function.accept(list, Eight::noOp);
    }

    // print top 25 words
    private static void printAll(List<Map.Entry<String, Integer>> list, Consumer<?> lastFunction) {
        int num = 1;
        for(Map.Entry<String, Integer> result : list) {
            if(num <= 25) {
                System.out.println(result.getKey() + "  -  " + result.getValue());
                num++;
            }
            else break;
        }
        lastFunction.accept(null);
    }

    // no operation function
    public static <T> void noOp(T t) {
        return;
    }

    // create a nested Biconsumer structure to make the program simple
    private static <T, B> BiConsumer<T, BiConsumer> simple(BiConsumer<T, B> f) {
        return (BiConsumer<T, BiConsumer>) f;
    }

    public static void main(String[] args) {
        readFile(args[0], Eight::filterChars);
    }
}
