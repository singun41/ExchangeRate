import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiService {
    private final LoggingService loggingService;
    private Properties apiProps;
    private String apiCallUrl;
    private String apiAuthKey;

    private SavingService savingService;
    private boolean status = false;

    public ApiService() {
        loggingService = new LoggingService();

        if(loadApiAuthKey()) {
            Path saveOptFile = CommonMethod.getPropertiesPath(Props.APP);
            try(
                FileReader reader = new FileReader(saveOptFile.toFile());
            ) {
                Properties saveProps = new Properties();
                saveProps.load(reader);
    
                switch(SaveOption.valueOf(saveProps.getProperty("save.option").toUpperCase())) {
                    case DB:
                        savingService = new SaveToDbService();
                        break;
                        
                    case FILE:
                    default:
                        savingService = new SaveToFileService();
                        break;
                }
                status = true;
                loggingService.writeLog("api auth key load completed.");

            } catch(Exception e) {
                status = false;
                loggingService.writeErr("Load api auth key error. " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            loggingService.writeErr("api.auth.key is null. entered api.auth.key string to api.properties file.");
        }
    }

    private boolean loadApiAuthKey() {
        Path propFile = CommonMethod.getPropertiesPath(Props.API);
        try(
            FileReader reader = new FileReader(propFile.toFile());
        ) {
            apiProps = new Properties();
            apiProps.load(reader);

            apiAuthKey = apiProps.getProperty("api.auth.key");
            if(apiAuthKey == null || apiAuthKey.isBlank())
                return false;
            
            setApiCallUrl(apiAuthKey);
            return true;

        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setApiCallUrl(String key) {
        apiCallUrl = new StringBuilder("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON").append("?")
            .append("authkey=").append(key).append("&")
            .append("searchdate=").append(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("&")
            .append("data=AP01").toString();
    }

    public void callAndSave() {
        if(!status) {
            return;
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(apiCallUrl).openConnection();
            conn.setRequestMethod("GET");

            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                conn.disconnect();
                return;
            }

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                String res = null;
                String jsonStr = null;

                while((res = bufferedReader.readLine()) != null) {
                    jsonStr = res;
                }

                JSONArray arrJson = new JSONArray(jsonStr);
                if(arrJson.isEmpty()) {
                    return;
                }

                List<Map<String, Object>> params = new ArrayList<>();
                for(int i=0; i<arrJson.length(); i++) {
                    JSONObject jsonObj = arrJson.getJSONObject(i);

                    if(jsonObj.get("cur_unit").equals("KRW"))
                        continue;
                    
                    int resultCode = Integer.parseInt(jsonObj.get("result").toString());
                    switch(resultCode) {
                        case 1:
                            params.add(jsonObj.toMap());
                            break;

                        case 2: System.out.println("DATA코드 오류. origin data: " + jsonObj.toString()); break;
                        case 3: System.out.println("인증코드 오류. origin data: " + jsonObj.toString()); break;
                        case 4: System.out.println("일일제한횟수 마감. origin data: " + jsonObj.toString()); break;
                        default: break;
                    }
                }

                savingService.save(params);

            } catch(Exception e) {
                loggingService.writeErr("ApiService.callAndSave() method error. " + e.getMessage());
                e.printStackTrace();
            }

        } catch(Exception e) {
            loggingService.writeErr("ApiService.callAndSave() method error. " + e.getMessage());
            e.printStackTrace();
        }
    }
}
