import java.io.*;  
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @FileName Five.java
 * @author Fuyao Li
 * @Date 10/13/2018
 * @param input txt file
 */
class Five {
    
// read the input file and store it in a global String
    private String readFile(String filePath) {
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
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputFile;
    }
    
    // filter the characters and normalize them
    // replace all the characters which are not letters with white space
    private String filterChars(String inputFile) {        
        inputFile = inputFile.toLowerCase();
        return inputFile;
    }    
    
    // scan inputFile and split it
    private String[] scan(String inputFile) {
        String[] split = inputFile.split("[^a-z]+");
        // System.out.println("split: " + split.length);
        return split;
    }

    // remove stop words
    private Function<String, List<String>> removeStopWords(String[] split) {
        // Make use of "Currying": Return a function that takes a path to the stop words file
        return stopWordsPath -> {
            Set<String> stop_words = new HashSet<>();
            List<String> list = new ArrayList<>();
            // read the stop_words.txt file and store the stop words in a list
            String s;
            try {
                BufferedReader br = new BufferedReader(new FileReader(stopWordsPath));
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
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        };
    }

    
    // count the frequency of each word
    private Map<String, Integer> frequency(List<String> words) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < words.size(); i++) {
            if (map.containsKey(words.get(i))) {
                map.put(words.get(i), map.get(words.get(i)) + 1);
            }
            else map.put(words.get(i), 1);
        }
        return map;
    }
    
    // sort the map
    private List<Map.Entry<String, Integer>> sort(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        //realize the compare by defining a comparator 
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {  
        // sort in a decreasing order 
        public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {  
        return o2.getValue().compareTo(o1.getValue());
            }  
        });
        return list;
    }
    
    // print top 25 words
    private void printAll(List<Map.Entry<String, Integer>> list) {
        int num = 1;
            for(Map.Entry<String, Integer> result : list) {  
                if(num <= 25) {
                System.out.println(result.getKey() + "  -  " + result.getValue());
                num++;     
                }
                else break;
            }    
    }
    
    //main function
    public static void main (String[] args) {
		// create a new object Four
        Five test = new Five();
        test.printAll(test.sort(test.frequency(test.removeStopWords(test.scan(test.filterChars(test.readFile(args[0])))).apply(args[1]))));

    }
}