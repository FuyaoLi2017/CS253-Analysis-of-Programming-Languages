import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @FileName TwentyEight.java
 * @Note Using the Actor programming style
 * @author Fuyao Li
 * @Date 11/23/2018
 * @param  txt file
 */

public class TwentyEight {
    public static void main(String[] args) {
        WordFrequencyManager wfm = new WordFrequencyManager();
        StopWordManager swm = new StopWordManager(wfm);
        DataStorageManager dsm = new DataStorageManager(args,swm);

        dsm.queue.add(new Message("process_word", ""));
    }
}

// create a base class to model the objects
class ActiveWFObject implements Runnable {
    String name;
    ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();
    boolean stop = false;

    public void dispatch(Message message) {
        if (message.key.equals("die")){
            this.stop = true;
        }
    }

    @Override
    public void run() {
        while(!stop) {
            if (!queue.isEmpty()){
                Message m = queue.poll();
                dispatch(m);
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void send(ActiveWFObject receiver, Message message) {
        receiver.queue.offer(message);
    }
}

// define the class message
class Message {
    public String key;
    public Object value;
    public Message(String key, Object value) {
        this.key = key;
        this.value = value;
    }
}

class DataStorageManager extends ActiveWFObject {
    String data = "";
    StopWordManager swm;

    public DataStorageManager(String[] args, StopWordManager swm) {
        // the line below can be omitted since it is acquiesce executed
        super();
        this.swm = swm;
        String path_to_file = args[0];
        String s;
        try {
            BufferedReader br = new BufferedReader(new FileReader(path_to_file));
            // store the file file into String sb
            StringBuffer sb = new StringBuffer();
            //read the words line by line
            while ((s = br.readLine()) != null) {
                sb.append(s);
                sb.append(" ");
            }
            data = sb.toString().toLowerCase();
//            System.out.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        (new Thread(this)).start();
    }

    @Override
    public void dispatch(Message message) {
        super.dispatch(message);
        if (message.key.equals("process_word")) {
            String[] inputFile = data.toLowerCase().split("[^a-z]+");
            for (String str : inputFile) {
                send(swm, new Message("filter", str));
            }
            // after processing all words, send message "top25"
            send(swm, new Message("top25", ""));
            // kill this thread obj
            send(this, new Message("die", ""));
        }
    }
}

// stop word manager
class StopWordManager extends ActiveWFObject {
    Set<String> stop_words = new HashSet<>();
    WordFrequencyManager wfm;

    public StopWordManager(WordFrequencyManager wfm) {
        this.wfm = wfm;
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
//            System.out.println(stop_words);
        } catch (Exception e) {
            e.printStackTrace();
        }
        (new Thread(this)).start();
    }

    public void dispatch(Message message) {
        super.dispatch(message);
        if (message.key.equals("filter")) {
            String word = (String) message.value;
            if (!stop_words.contains(word)) {
                send(wfm, new Message("word", word));
            }
        }
        // if the message is not filtered, pass the message to WordFrenquencyManager and kill itself
        else {
            send(wfm, message);
            send(this, new Message("die", ""));
        }
    }
}

// calculate the word frequency and print the result
class WordFrequencyManager extends ActiveWFObject {
    Map<String, Integer> map = new HashMap<>();
    List<Map.Entry<String, Integer>> list = null;

    public WordFrequencyManager() {
        (new Thread(this)).start();
    }

    public void dispatch(Message message) {
        super.dispatch(message);
        if (message.key.equals("word")) {
            String word = (String) message.value;
            if (map.containsKey(word))
                map.put(word, map.get(word) + 1);
            else map.put(word, 1);
        }
//        System.out.println(map);
        if (message.key.equals("top25")) {
            list = new ArrayList<>(map.entrySet());


            //realize the compare by defining a comparator
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
            send(this, new Message("die", ""));
        }
    }
}


