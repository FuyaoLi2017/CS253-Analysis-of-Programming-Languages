import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @FileName TwentyEight.java
 * @Note Using the data space programming style, concurrency
 * @author Fuyao Li
 * @Date 11/30/2018
 */

public class TwentyNine {
    private static final int N = 5;

    // the word data space
    private static ConcurrentLinkedQueue<String> wordSpace = new ConcurrentLinkedQueue<>();

    // the partial frequencies data space
    private static ConcurrentLinkedQueue<Map<String, Integer>> freqSpace = new ConcurrentLinkedQueue<>();

    // the set of stop words
    private static Set<String> stopWords = new HashSet<>();

    // global word frequency
    private static ConcurrentHashMap<String, Integer> wordFreqs = new ConcurrentHashMap<>();

    public TwentyNine() {
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
//            System.out.println(stopWords);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // read and parses the file at filePath, adding all words to wordspace
    private void fillWordSpace(String filePath) {
        String s;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            // store the file file into String sb
            StringBuffer sb = new StringBuffer();
            //read the words line by line
            while ((s = br.readLine()) != null) {
                sb.append(s);
                sb.append(" ");
            }
            String[] split = sb.toString().toLowerCase().split("[^a-z]+");
            for (String str : split) {
                wordSpace.offer(str);
//                System.out.println(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // print the top 25 entries
    private void top25() {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(wordFreqs.entrySet());
        Collections.sort(list, (o1, o2) -> {
            return o2.getValue().compareTo(o1.getValue());
        });
        int num = 1;
        for (Map.Entry<String, Integer> result : list) {
            if (num <= 25) {
                System.out.println(result.getKey() + "  -  " + result.getValue());
                num++;
            } else break;
        }
    }

    // worker1 is used to count the frequency map in one thread
    static class Worker1 implements Runnable {
        Map<String, Integer> freqs;

        public Worker1(Map<String, Integer> freqs) {
            this.freqs = freqs;
        }

        // poll words from wordSpace (until no more words is available) and keeps count of the words seen.
        private void processWords() {
            String word;
            while ((word = wordSpace.poll()) != null) {
                if (!stopWords.contains(word)) {
                    if (freqs.containsKey(word))
                        freqs.put(word, freqs.get(word) + 1);
                    else freqs.put(word, 1);
                }
            }
            freqSpace.offer(freqs);
//            System.out.println("Size of freqSpace: " + freqSpace.size());
//            System.out.println(freqs);
        }

        @Override
        public void run() {
            try {
                processWords();
//                System.out.println(freqs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // worker2 is used to merge the frequency maps
    static class Worker2 implements Runnable {
        public Worker2() {
            super();
        }
        // merge partialFreqs into wordFreqs
        private void mergePartialFreqs() {
            // The partial word frequencies to be merged into the global word frequencies.
            Map<String, Integer> partialFreqs;
            while ((partialFreqs = freqSpace.poll()) != null) {
//                System.out.println(partialFreqs.size());
                List<Map.Entry<String, Integer>> list = new ArrayList<>(partialFreqs.entrySet());
                // merge the result
                for(Map.Entry<String, Integer> result : list) {
                    String key = result.getKey();
                    if (wordFreqs.containsKey(key)) {
                        wordFreqs.put(key, wordFreqs.get(key) + result.getValue());
                    } else {
                        wordFreqs.put(key, result.getValue());
                    }
                }
            }

        }

        @Override
        public void run() {
            try {
                mergePartialFreqs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // main function
    public static void main(String[] args) throws InterruptedException {
        TwentyNine twentyNine = new TwentyNine();
        twentyNine.fillWordSpace(args[0]);
        // worker threads, we should store the threads in a array to keep track it and join() it after start() it!
        Thread[] worker1s = new Thread[N];
        for (int i = 0; i < worker1s.length; i++) {
            Worker1 th = new Worker1(new HashMap<>());
            worker1s[i] = new Thread(th);
        }
        // start the worker thread
        for (Thread worker : worker1s) {
            worker.start();
        }
        // wait for the worker to terminate
        for (Thread worker : worker1s) {
            worker.join();
        }

//        System.out.println(freqSpace);

        // create workers to merge
        Thread[] worker2s = new Thread[N];
        for (int i = 0; i < worker2s.length; i++) {
            Worker2 th = new Worker2();
            worker2s[i] = new Thread(th);
        }
        // start the worker2 thread
        for (Thread worker : worker2s) {
            worker.start();
        }
        // wait for the worker2 to terminate
        for (Thread worker : worker2s) {
            worker.join();
        }
        // print the result
//        System.out.println(wordFreqs);
        twentyNine.top25();
    }
}
