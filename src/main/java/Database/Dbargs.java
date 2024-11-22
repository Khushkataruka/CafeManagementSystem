package Database;

public class Dbargs {
    private static final String username = "root";
    private static final String pass = "5466";
    private static final String url = "jdbc:mysql://localhost:3306/gobuy";

    public static String getUrl() {
        return url;
    }

    public static String getPass() {
        return pass;
    }
    public static String getUsername() {
        return username;
    }
}
