import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * @FileName ThirtyOne.java
 * @Note Using the double map reduce programming style, concurrency
 * @author Fuyao Li
 * @Date 12/1/2018
 */

public class ThirtyOne {

    // split word class, parse the paragraphs into seperate words and exclude the stop_words
    static class SplitWord {

        List<Pair> counts = new ArrayList<>();
        Set<String> stopWords = new HashSet<>();

        public SplitWord() {
            String s;
            try {
                BufferedReader br = new BufferedReader(new FileReader("../stop_words.txt"));
                //store the stop_words file into String sb
                StringBuffer sb = new StringBuffer();
                //read the words line by line
                while ((s = br.readLine()) != null) {
                    sb.append(s);
                }
                String str = sb.toString();
                // sparse the individual words and put them into map
                String[] stopWordArray = str.split(",");
                Collections.addAll(stopWords, stopWordArray);
                // add individual letters
                for (char c = 'a'; c <= 'z'; c++) {
                    stopWords.add(String.valueOf(c));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // parse the words and put into the hashmap if it is not included in the stopWords
        public void parseWord(String[] lines) {
            for (String s : lines) {
                String[] words = s.toLowerCase().split("[^a-z]+");
                for (String word : words) {
                    if (!stopWords.contains(word) && word.length() > 0) {
                        counts.add(new Pair(word, 1));
                    }
                }
            }
        }
    }

    // "Reduce" function in the MapReduce model
    static class Reducer implements Runnable {
        List<Pair> list = new ArrayList<>();
        HashMap<String, Integer> PartialFreq = new HashMap<>();

        public void run() {
            for (Pair p : list) {
                String word = p.value;
                if (PartialFreq.containsKey(word)) {
                    PartialFreq.put(word, PartialFreq.get(word) + 1);
                } else {
                    PartialFreq.put(word, 1);
                }
            }
        }
    }

    // define a data structure, used to regroup the counts produced by paragraphs
    static class Pair {
        public String value;
        public Integer count;

        public Pair (String value, Integer count) {
            this.value = value;
            this.count = count;
        }
    }


    // split the original book into lineNum of paragraphs
    public static List<String[]> readFile(String filePath, int lineNum) {
        List<String[]> paragraphs = new ArrayList<>();
        String s;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            // store the file file into String sb
            StringBuffer sb = new StringBuffer();
            //read the words line by line
            while ((s = br.readLine()) != null) {
                sb.append(s);
                sb.append("\n");
            }
            String[] lines = sb.toString().split("\n");
            
            // System.out.println("length of lines" + lines.length);

            int paragraphNum = lines.length / lineNum;
            for (int i = 0; i < paragraphNum; i++) {
                String[] paragraph = Arrays.copyOfRange(lines, i * lineNum, i * lineNum + lineNum);
                paragraphs.add(paragraph);
            }

            // deal with the residual lines
            if (lines.length > paragraphNum * lineNum) {
                String[] paragraph = Arrays.copyOfRange(lines, paragraphNum * lineNum, lines.length);
                paragraphs.add(paragraph);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paragraphs;
    }


    // peform the parseWord function
    public static void mapping(SplitWord sw, List<String[]> paragraphs) {
        for (String[] p : paragraphs) {
            sw.parseWord(p);
        }
    }

    // 31.3 style of regrouping
    public static void regroup(SplitWord sw, Reducer[] r) {
        for (Pair pair : sw.counts) {
            char c = pair.value.charAt(0);
            if (c >= 'a' && c <= 'e') {
                r[0].list.add(pair);
            } else if (c >= 'f' && c <= 'j') {
                r[1].list.add(pair);
            } else if (c >= 'k' && c <= 'o') {
                r[2].list.add(pair);
            } else if (c >= 'p' && c <= 't') {
                r[3].list.add(pair);
            } else if (c >= 'u' && c <= 'z') {
                r[4].list.add(pair);
            }
        }
    }

    // merge several parts together, which is group by regroup function
    public static void merge(Reducer[] r) {
        List<Pair> sortedList = new ArrayList<>();

        // the word owned by different reducer must be different since they have different starting letter
        // we can just add them into the sortedList one by one
        for (Reducer reducer : r) {
            for (String key : reducer.PartialFreq.keySet()) {
                sortedList.add(new Pair(key, reducer.PartialFreq.get(key)));
            }
        }

        // sort the sortedList
        Collections.sort(sortedList, (o1, o2) -> {
            if (o2.count > o1.count) return 1;
            if (o2.count < o1.count) return -1;
            return 0;
        });
        
        // print the top25 words
        for (int i = 0; i < 25; i++) {
            Pair cur = sortedList.get(i);
            System.out.println(cur.value + " - " + cur.count);
        }
    }

    // main function
    public static void main(String[] args) {
        SplitWord sw = new SplitWord();
        mapping(sw, readFile(args[0], 200));

//        System.out.println(sw.counts);
//        System.out.println(sw.stopWords);

        Reducer[] r = new Reducer[5];
        for (int i = 0; i < 5; i++) {
            r[i] = new Reducer();
        }

        // send words to different reducer based on first letter
        regroup(sw, r);

        // start the reducing process
        Thread[] t = new Thread[5];
        for (int i = 0; i < 5; i++) {
            t[i] = new Thread(r[i]);
            t[i].start();
        }

        for (int i = 0; i < 5; i++) {
            try {
                t[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        merge(r);
    }

}
