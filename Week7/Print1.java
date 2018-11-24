import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Print1 implements Consumer<List<Map.Entry<String, Integer>>> {

    @Override
    public void accept(List<Map.Entry<String, Integer>> list) {
        System.out.println("method1");
        list.stream().forEach(e -> System.out.println(String.format("%s  -  %d", e.getKey(), e.getValue())));
    }
}