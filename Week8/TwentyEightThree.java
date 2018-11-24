import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @FileName TwentyEightThree.java
 * @Note Combine Actor and Lazy River programming style, concurrency, print once every 100000 characters
 * @author Fuyao Li
 * @Date 11/24/2018
 */

/*
Actor: Use concurrent programming to complete the tasks.
Lazy river: use a data flow and pass the character one by one to the next stage and then process it.
Merge these two methods together -> this exercise!
 */

public class TwentyEightThree {
    public static void main(String[] args) {
        CountAndSort countAndSort = new CountAndSort();
        NonStopWords nonStopWords = new NonStopWords(countAndSort);
        AllWords allWords = new AllWords(nonStopWords);
        Characters characters = new Characters(args, allWords);

        characters.queue.add(new Message("process_character", ""));
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

class Characters extends ActiveWFObject {
    String data = "";
    AllWords allWords;
    int i = 0;

    public Characters(String[] args, AllWords allWords) {
        super();
        this.allWords = allWords;
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
        if (message.key.equals("process_character")) {
            for (char c : data.toCharArray()) {
//                System.out.println(c);
                send(allWords, new Message("findWord", c));
                i++;
                // send message "top25" after process every 100,000 characters
                if (i % 100000 == 0){
                    send(allWords, new Message("top25", ""));
                }
            }

            send(allWords, new Message("top25", ""));
            // kill this thread obj
            send(this, new Message("die", ""));
        }
    }


}

class AllWords extends ActiveWFObject {

    boolean startChar = true;
    NonStopWords nonStopWords;
    StringBuilder sb = new StringBuilder();

    public AllWords(NonStopWords nonStopWords) {
        this.nonStopWords = nonStopWords;
        (new Thread(this)).start();
    }

    @Override
    public void dispatch(Message message) {
        super.dispatch(message);
        if (message.key.equals("findWord")) {
            char currentChar = (char) message.value;
            if (startChar) {
                if (Character.isLetterOrDigit(currentChar)) {

                    sb.append(currentChar);
//                    System.out.println("start: " + sb);
                    startChar = false;
                }
            } else {
                if (Character.isLetterOrDigit(currentChar)) {
                    sb.append(currentChar);
//                    System.out.println(sb);
                } else {
                    // find the end of word, emit it
                    startChar = true;
//                    System.out.println(sb.toString());
                    send(nonStopWords, new Message("word", sb.toString()));
                    sb = new StringBuilder();
                }
            }
        } else {
            send(nonStopWords, message);
            send(this, new Message("die", ""));
        }
    }
}

class NonStopWords extends ActiveWFObject {

    Set<String> stop_words = new HashSet<>();
    CountAndSort countAndSort;

    public NonStopWords(CountAndSort countAndSort) {
        this.countAndSort = countAndSort;
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
        if (message.key.equals("word")) {
            String word = (String) message.value;
            if (!stop_words.contains(word)) {
                send(countAndSort, new Message("countAndSort", word));
            }
        }
        // if the message is not filtered, pass the message to WordFrenquencyManager and kill itself
        else {
            send(countAndSort, message);
            send(this, new Message("die", ""));
        }
    }
}

class CountAndSort extends ActiveWFObject {
    Map<String, Integer> map = new HashMap<>();
    List<Map.Entry<String, Integer>> list = null;

    public CountAndSort() {
        (new Thread(this)).start();
    }

    public void dispatch(Message message) {
        super.dispatch(message);
        if (message.key.equals("countAndSort")) {
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
            System.out.println("------------------------------------------");
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