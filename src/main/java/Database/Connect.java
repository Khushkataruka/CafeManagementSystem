package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

    public static Connection connect()
    {
        String username = Dbargs.getUsername();
        String pass = Dbargs.getPass();
        String url = Dbargs.getUrl();
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection con = null;
        try{
            con = DriverManager.getConnection(url, username, pass);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return con;
    }
}
