import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class SaveToFileService implements SavingService {
    private final LoggingService loggingService;
    private String filepath;
    private String filename;

    public SaveToFileService() {
        loggingService = new LoggingService();
    }

    public void save(List<Map<String, Object>> params) {
        String now = CommonMethod.getCurrentDatetimeString();
        String dateStr = CommonMethod.getCurrentDateString();

        filepath = new StringBuilder(System.getProperty("user.dir")).append("/currencies/").toString();
        filename = new StringBuilder(filepath).append(dateStr).append(".txt").toString();

        StringBuilder writingData = new StringBuilder();
        params.stream().forEach(e -> {
            String data = "기준일: {0}, 통화코드: {1}, 통화명: {2}, 전신환(송금)받을 때: {3}, 전신환(송금)보낼 때: {4}, 매매기준율: {5}, 장부가격: {6}, 년환가료율: {7}, 10일환가료율: {8}, 서울외국환중개매매기준율: {9}, 서울외국환중개장부가격: {10}, 데이터수집일시: {11}";

            Object[] args = {
                dateStr,
                e.get("cur_unit").toString().substring(0, 3),
                e.get("cur_nm").toString(),
                e.get("ttb").toString(),
                e.get("tts").toString(),
                e.get("deal_bas_r").toString(),
                e.get("bkpr").toString(),
                e.get("yy_efee_r").toString(),
                e.get("ten_dd_efee_r").toString(),
                e.get("kftc_deal_bas_r").toString(),
                e.get("kftc_bkpr").toString(),
                now
            };

            writingData.append(MessageFormat.format(data, args)).append(System.lineSeparator());
        });

        try {
            System.out.println(writingData);

            Path path = Paths.get(filepath);
            Path file = Paths.get(filename);

            if(Files.notExists(file)) {
                Files.createDirectories(path);
                Files.createFile(file);
            }

            Files.write(file, writingData.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            loggingService.writeLog("File writing is completed.");

        } catch(Exception ex) {
            loggingService.writeErr("File writing error. " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
