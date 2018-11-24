import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Print2 implements Consumer<List<Map.Entry<String, Integer>>> {

    @Override
    public void accept(List<Map.Entry<String, Integer>> list) {
        System.out.println("method2");
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