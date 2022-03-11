import java.io.FileReader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SaveToDbService implements SavingService {
    private Properties dbProps;
    private String dbConnStr;
    private String username;
    private String password;

    public SaveToDbService() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch(Exception e) {
            e.printStackTrace();
        }
        loadDbConnectionInfo();
    }

    private void loadDbConnectionInfo() {
        Path propFile = PropertyPath.get(Props.DB);
        try (FileReader reader = new FileReader(propFile.toFile());) {
            dbProps = new Properties();
            dbProps.load(reader);

            dbConnStr = dbProps.getProperty("db.server.connection.url");
            username = dbProps.getProperty("db.server.username");
            password = dbProps.getProperty("db.server.password");

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void save(List<Map<String, Object>> params) {
        try(
            Connection dbConn = DriverManager.getConnection(dbConnStr, username, password);
            Statement statement = dbConn.createStatement();
        ) {
            LocalDateTime now = LocalDateTime.now();
            String dt = now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String createdDatetime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


            statement.execute("delete from exchange_rate where dt = '" + dt + "'");

            String insertQuery = "insert into exchange_rate ("
                                + "dt, cur_unit, cur_nm, ttb, tts, deal_bas_r, bkpr, yy_efee_r, ten_dd_efee_r, kftc_deal_bas_r, kftc_bkpr, created_datetime"
                                + ") values ({0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11})";

            params.stream().forEach(e -> {
                // Float.valueOf() 로 래핑해서 데이터타입에 맞추게 되면 숫자에 comma가 다시 생성되면서 column이 맞지 않는다는 에러가 발생하므로, 그대로 String 타입으로 처리한다.
                Object[] args = {
                    "'" + dt + "'",
                    "'" + e.get("cur_unit").toString().substring(0, 3) + "'",
                    "'" + e.get("cur_nm").toString() + "'",
                    e.get("ttb").toString().replace(",", ""),
                    e.get("tts").toString().replace(",", ""),
                    e.get("deal_bas_r").toString().replace(",", ""),
                    e.get("bkpr").toString().replace(",", ""),
                    e.get("yy_efee_r").toString().replace(",", ""),
                    e.get("ten_dd_efee_r").toString().replace(",", ""),
                    e.get("kftc_deal_bas_r").toString().replace(",", ""),
                    e.get("kftc_bkpr").toString().replace(",", ""),
                    "'" + createdDatetime + "'"
                };

                String query = MessageFormat.format(insertQuery, args);
                try {
                    System.out.println(query);
                    statement.execute(query);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
