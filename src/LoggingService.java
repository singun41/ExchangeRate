import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LoggingService {
    private static final String LOG_PATH = CommonMethod.getAppPath() + "/logs/";
    private static final String LOG_RESULT = LOG_PATH + "log_result.log";
    private static final String LOG_ERR = LOG_PATH + "log_err.log";

    public LoggingService() {
        createLogDir();
    }

    private void createLogDir() {
        Path path = Paths.get(LOG_PATH);
        try {
            if(Files.notExists(path)) {
                Files.createDirectories(path);
                Files.createFile(Paths.get(LOG_RESULT));
                Files.createFile(Paths.get(LOG_ERR));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String getLogStyle(String data) {
        return new StringBuilder(CommonMethod.getCurrentDatetimeString()).append(" : ").append(data).append(System.lineSeparator()).toString();
    }

    public void writeLog(String result) {
        try {
            Files.write(Paths.get(LOG_RESULT), getLogStyle(result).getBytes(), StandardOpenOption.APPEND);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void writeErr(String err) {
        try {
            Files.write(Paths.get(LOG_ERR), getLogStyle(err).getBytes(), StandardOpenOption.APPEND);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
