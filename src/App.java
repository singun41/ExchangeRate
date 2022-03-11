public class App {
    // 한국 수출입 은행 api 명세
    // https://www.koreaexim.go.kr/site/program/openapi/openApiView?menuid=001003002002001&apino=2&viewtype=C

    public static void main(String[] args) throws Exception {
        ApiService apiService = new ApiService();
        apiService.callAndSave();
    }
}
