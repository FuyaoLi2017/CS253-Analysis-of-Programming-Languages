import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Eleven {

    public static void main(String[] args) {
        WordFrequencyController wfController = new WordFrequencyController();
        wfController.dispatch(new String[]{"init", args[0]});
        wfController.dispatch(new String[]{"run"});
    }

    static class DataStorageManager {
        String inputFile = null;

        public Object dispatch(String[] message) {
            if (message[0].equals("init"))
                return this._init(message[1]);
            else if (message[0].equals("words"))
                return this._words();
            else throw new IllegalArgumentException("Message not understood: " + message[0]);
        }

        // read file pride-and-prejudice
        public String _init(String filePath) {
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
                inputFile = sb.toString();
                return inputFile;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public String[] _words() {
            return this.inputFile.toLowerCase().split("[^a-z]+");
        }
    }

    static class StopWordManager {
        Set<String> stop_words = new HashSet<>();
        List<String> list = new ArrayList<>();

        public Object dispatch(String[] message) {
            if (message[0].equals("init"))
                return this._init();
            else if (message[0].equals("is_stop_word"))
                return this._is_stop_words(message[1]);
            else throw new IllegalArgumentException("Message not understood: " + message[0]);
        }

        public Set<String> _init() {
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
                Collections.addAll(stop_words, stopWordArray);
                // add individual letters
                for (char c = 'a'; c <= 'z'; c++) {
                    stop_words.add(String.valueOf(c));
                }
                return stop_words;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // API: check whether it is a
        public boolean _is_stop_words(String word) {
            if (stop_words.contains(word))
                return true;
            return false;
        }
    }

    static class WordFrequencyManager {
        Map<String, Integer> map = new HashMap<>();

        public Object dispatch(String[] message) {
            if (message[0].equals("increment_count"))
                return this._increment_count(message[1]);
            else if (message[0].equals("sorted"))
                return this._sorted();
                else throw new IllegalArgumentException("Message not understood: " + message[0]);
        }

        public Map<String, Integer> _increment_count(String word) {
            if (map.containsKey(word))
                map.put(word, map.get(word) + 1);
            else map.put(word, 1);
            return map;
        }

        public List<Map.Entry<String, Integer>> _sorted() {
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
            //realize the compare by defining a comparator
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                // sort in a decreasing order
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            return list;
        }
    }

    static class WordFrequencyController {
        DataStorageManager _storage_manager = new DataStorageManager();
        StopWordManager _stop_word_manager = new StopWordManager();
        WordFrequencyManager _word_freq_manager = new WordFrequencyManager();
        public Object dispatch(String[] message) {
            if (message[0].equals("init"))
                return this._init(message[1]);
            else if (message[0].equals("run"))
                return this._run();
            else throw new IllegalArgumentException("Message not understood: " + message[0]);
        }

        public Object _init(String filePath) {
            _storage_manager.dispatch(new String[]{"init", filePath});
            _stop_word_manager.dispatch(new String[]{"init"});
            return null;
        }

        public Object _run() {
            String[] input = (String[])this._storage_manager.dispatch(new String[]{"words"});
            for (String w : input) {
                if(!(boolean)_stop_word_manager.dispatch(new String[]{"is_stop_word", w}))
                    _word_freq_manager.dispatch(new String[]{"increment_count", w});
            }
            List<Map.Entry<String, Integer>> list = (List<Map.Entry<String, Integer>>)this._word_freq_manager.dispatch(new String[]{"sorted"});
            int num = 1;
            for(Map.Entry<String, Integer> result : list) {
                if(num <= 25) {
                    System.out.println(result.getKey() + "  -  " + result.getValue());
                    num++;
                }
                else break;
            }
            return null;
        }
    }
}
