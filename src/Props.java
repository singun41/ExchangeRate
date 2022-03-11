public enum Props {
    APP("app.properties"), API("api.properties"), DB("database.properties"), MAIL("mail.properties"), MAIL_OPT("mail-option.properties");

    private String value;
    private Props(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }
}
