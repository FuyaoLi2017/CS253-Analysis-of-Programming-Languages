import java.io.*;  
import java.util.*;
import java.util.Map.Entry;

/**
 * @FileName Exercise_1
 * @author Fuyao Li
 * @Date 10/02/2018
 * @param input txt file
 */
class Exercise_1 {
    
    public static void main (String[] args) {
    // initiate a map to store the word and its corresponding frenquency
    HashMap<String, Integer> map = new HashMap<>();
    
    // initiate a list to store the stop words
    List<String> stop_words = new ArrayList<>();
    
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
            //String str = sb.toString().toLowerCase();  
            String str = sb.toString();
            // sparse the individual words and put them into map 
            String[] stopWordArray = str.split(",");
            for(int i = 0; i < stopWordArray.length; i++){
                stop_words.add(stopWordArray[i]);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//read the pride-and-prejudice.txt file and store the pairs of words and its corresponding frenquency in the map
		try {
			BufferedReader br = new BufferedReader(new FileReader(args[0])); 
            // store the file file into String sb
            StringBuffer sb = new StringBuffer();
            //read the words line by line
            while((s = br.readLine()) != null) {
                sb.append(s);
                sb.append(" ");
            }  
            String str = sb.toString().toLowerCase();
            String[] book = str.split("[^a-z]+");
            // put the words into the HashMap
            for(int i = 0; i < book.length; i++){
                if(map.containsKey(book[i])){
                    map.put(book[i], map.get(book[i]) + 1);
                } else {
                    map.put(book[i], 1);
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// delete the Key-Value pairs which is stored in the stop word list
		for(String str : stop_words) {
		   if(map.containsKey(str)){
		       map.remove(str);
		   } else {
		       continue;
		   }
		}
		
		//sort the list according to the frenquency of the words
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());  
            //realize the compare by defining a comparator 
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {  
            	// sort in a decreasing order 
                public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {  
                    return o2.getValue().compareTo(o1.getValue());  
                }  
        });
        
        // print the top 25 words
        int num = 1;
            for(Map.Entry<String, Integer> result : list) {  
                if(num <= 25) {
                    // output words which length is longer than 1
                    if(result.getKey().length() > 1){
                        System.out.println(result.getKey() + "  -  " + result.getValue()); 
                        num++; 
                    }else {
                        continue;
                    }
                }  
                else break;  
            }
    }
}