import java.util.*;
import java.util.function.Function;

public class Frequencies2 implements Function<List<String>, List<Map.Entry<String, Integer>>> {

    @Override
    public List<Map.Entry<String, Integer>> apply(List<String> words) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < words.size(); i++) {
            if (map.containsKey(words.get(i))) {
                map.put(words.get(i), map.get(words.get(i)) + 1);
            }
            else map.put(words.get(i), 1);
        }
        List<Map.Entry<String, Integer>> result = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        //realize the compare by defining a comparator
        Collections.sort(result, new Comparator<Map.Entry<String, Integer>>() {
            // sort in a decreasing order
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return result.subList(0, 25);
    }
}