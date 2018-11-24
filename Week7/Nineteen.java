import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @FileName Nineteen.java
 * @Note Using the plugin programming style
 * @author Fuyao Li
 * @Date 11/16/2018
 * @param input txt file
 */
public class Nineteen {
    private static Function<String, List<String>> TFWORDS;
    private static Function<List<String>, List<Map.Entry<String, Integer>>> TFFREQS;
    // for exercise 19.4
    private static Consumer<List<Map.Entry<String, Integer>>> PRINT;

    public static void main(String[] args) throws Exception {
        loadPlugins();
        PRINT.accept(TFFREQS.apply(TFWORDS.apply(args[0])));
    }

    public static void loadPlugins() throws Exception {
        Properties config = new Properties();
        config.load(new BufferedReader(new FileReader("./config.properties")));
        // the class files are under the current folder directory
        URL pluginsDirUrl = Paths.get(config.getProperty("pluginDirection")).toUri().toURL();

        ClassLoader classLoader = new URLClassLoader(new URL[] { pluginsDirUrl }, Nineteen.class.getClassLoader());
        // Read the name of class
        String wordsProviderClassName = config.getProperty("words");
        String frequenciesProviderClassName = config.getProperty("frequencies");
        // Drop ".class" from class name
        wordsProviderClassName = removeTail(wordsProviderClassName);
        frequenciesProviderClassName = removeTail(frequenciesProviderClassName);
        // Load the classes
        Class<?> wordsProviderClass = classLoader.loadClass(wordsProviderClassName);
        Class<?> frequenciesProviderClass = classLoader.loadClass(frequenciesProviderClassName);
        // Create instances, cast to desired type
        Function<String, List<String>> wordsProvider = (Function<String, List<String>>) wordsProviderClass.newInstance();
        Function<List<String>, List<Map.Entry<String, Integer>>> frequenciesProvider =
                (Function<List<String>, List<Map.Entry<String, Integer>>>) frequenciesProviderClass.newInstance();
        TFWORDS = wordsProvider;
        TFFREQS = frequenciesProvider;

        // EXERCISE 19.4
        String printerClassName = config.getProperty("print");
        printerClassName = removeTail(printerClassName);
        Class<?> printerClass = classLoader.loadClass(printerClassName);
        Consumer<List<Map.Entry<String, Integer>>> printer =
                (Consumer<List<Map.Entry<String, Integer>>>) printerClass.newInstance();
        PRINT = printer;
    }

    // remove the ".class" end
    private static String removeTail(String s) {
        String tail = ".class";
        if (s.endsWith(tail)) {
            return s.substring(0, s.length() - tail.length());
        }
        return s;
    }
}
