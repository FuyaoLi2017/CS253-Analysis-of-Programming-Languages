import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.function.Consumer;

/**
 * @fileName Fourteen.java
 * @author lifuyao
 * @Date 11/2/2018
 */

public class Fourteen {

    // The "I’ll call you back" Word Frequency Framework
    public static class WordFrenquencyFramework {
        private final List<Consumer<String>> _load_event_handlers = new ArrayList<>();
        private final List<Runnable> _dowork_event_handlers = new ArrayList<>();
        private final List<Runnable> _end_event_handlers = new ArrayList<>();

        public void register_for_load_event(Consumer<String> handler) {
            _load_event_handlers.add(handler);
        }

        public void register_for_dowork_event(Runnable handler) {
            _dowork_event_handlers.add(handler);
        }

        public void register_for_end_event(Runnable handler) {
            _end_event_handlers.add(handler);
        }

        public void run(String filePath) {
            for(Consumer<String> a : _load_event_handlers) {
                a.accept(filePath);
            }

            for(Runnable b : _dowork_event_handlers) {
                b.run();
            }

            for(Runnable c : _end_event_handlers) {
                c.run();
            }
        }
    }

    // The entities of the application
    private static class DataStorage {

        private String data;
        private final StopWordFilter _stop_word_filter;
        private final List<Consumer<String>> _word_event_handlers = new ArrayList<>();

        private DataStorage(WordFrenquencyFramework wfapp, StopWordFilter stop_word_filter) {
            wfapp.register_for_load_event(this::load);
            wfapp.register_for_dowork_event(this::produceWords);
            _stop_word_filter = stop_word_filter;
        }

        private void load(String filePath) {
            String s;
            try{
                BufferedReader br = new BufferedReader(new FileReader(filePath));
                // store the file file into String sb
                StringBuffer sb = new StringBuffer();
                //read the words line by line
                while((s = br.readLine()) != null) {
                    sb.append(s);
                    sb.append(" ");
                }
                data = sb.toString().toLowerCase().replaceAll("[^a-zA-Z\\d\\s]", " ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void produceWords() {
            if (data == null) {
                throw new IllegalStateException("The file is not loaded.");
            }
            for (String word : data.split("\\s+")) {
                if (!_stop_word_filter.isStopWord(word)) {
                    for (Consumer<String> c : _word_event_handlers) {
                        c.accept(word);
                    }
                }
            }
        }

        private void registerWordEventHandler(Consumer<String> handler) {
            _word_event_handlers.add(handler);
        }
    }

    // model the stop word filter
    private static class StopWordFilter {
        private Set<String> _stop_words = new HashSet<>();

        private StopWordFilter(WordFrenquencyFramework wfapp) {
            wfapp.register_for_load_event(this::load);
        }

        private void load(String ignore) { // ignore is not used
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
                for (int i = 0; i < stopWordArray.length; i++) {
                    _stop_words.add(stopWordArray[i]);
                }
                // add individual letters
                for (char c = 'a'; c <= 'z'; c++) {
                    _stop_words.add(String.valueOf(c));
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean isStopWord(String word) {
            if(_stop_words == null) {
                throw new IllegalStateException("the stop word array is not initialized");
            }
            return _stop_words.contains(word);
        }
    }

    // Manage the word frequency data
    private static class WordFrenquencyCounter {

        private final Map<String, Integer> map = new HashMap<>();

        private WordFrenquencyCounter(WordFrenquencyFramework wfapp, DataStorage data_Storage) {
            data_Storage.registerWordEventHandler(this::_increment_count);
            wfapp.register_for_end_event(this::_print_freqs);
        }

        private void _increment_count(String word) {
            if (map.containsKey(word)) {
                map.put(word, map.get(word) + 1);
            } else {
                map.put(word, 1);
            }
        }

        private void _print_freqs() {
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
            //realize the compare by defining a lambda expression
            Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
            int num = 1;
            for(Map.Entry<String, Integer> result : list) {
                if(num <= 25) {
                    System.out.println(result.getKey() + "  -  " + result.getValue());
                    num++;
                }
                else break;
            }
        }
    }

    // provide the function required for exercise 14.2
    private static class ZWordsCounter {
        private final Set<String> zWords = new HashSet<>();
        private final StopWordFilter stop_word_filter;

        private ZWordsCounter(StopWordFilter stopWordFilter) {
            stop_word_filter = stopWordFilter;
        }

        private void ZwordEncountered(String word) {
            if(!stop_word_filter.isStopWord(word) && word.contains("z")) {
                zWords.add(word);
            }
        }

        private void printZwords() {
            System.out.println(zWords.size());
        }
    }

    // main function
    public static void main(String[] args) {
        WordFrenquencyFramework wfapp = new WordFrenquencyFramework();
        StopWordFilter stopWordFilter = new StopWordFilter(wfapp);
        DataStorage dataStorage = new DataStorage(wfapp, stopWordFilter);
        // WordFrequencyCount object
        new WordFrenquencyCounter(wfapp, dataStorage);
        // 14.2
        ZWordsCounter zCount = new ZWordsCounter(stopWordFilter);
        // register word events
        dataStorage.registerWordEventHandler(zCount::ZwordEncountered);
        // register the additional event handler
        wfapp.register_for_end_event(zCount::printZwords);
        // start the event chain
        wfapp.run(args[0]);
    }
}
