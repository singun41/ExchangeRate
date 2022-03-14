import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonMethod {
    private static final String RUNTIME_ROOT_PATH = System.getProperty("user.dir");

    public static String getAppPath() {
        return RUNTIME_ROOT_PATH;
    }

    public static Path getPropertiesPath(Props props) {
        return Paths.get(RUNTIME_ROOT_PATH + "/properties/" + props.getValue());
    }

    public static String getCurrentDateString() {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String getCurrentDatetimeString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
}
