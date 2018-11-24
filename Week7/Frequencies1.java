import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Frequencies1 implements Function<List<String>, List<Map.Entry<String, Integer>>> {

    @Override
    public List<Map.Entry<String, Integer>> apply(List<String> words) {
        return words.stream().
                collect(Collectors.toMap(w -> w, w -> 1, (currentValue, one) -> currentValue + one)).     // create the mapping
                entrySet().stream().sorted((o1, o2) -> -o1.getValue().compareTo(o2.getValue())).          // sort the list
                limit(25).collect(Collectors.toList());                                                   // choose the top25 elements
    }

}