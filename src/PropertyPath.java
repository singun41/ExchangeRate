import java.nio.file.Path;
import java.nio.file.Paths;

public class PropertyPath {
    public static Path get(Props props) {
        return Paths.get(System.getProperty("user.dir") + "/properties/" + props.getValue());
    }
}
