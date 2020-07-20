package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author emoud
 */
public class DBConnection {
    
    public static Connection getConnection() {
        Connection conn = null;
        String driver = "com.mysql.cj.jdbc.Driver";
        String dbName = "U03rqG";
        String url = "jdbc:mysql://52.206.157.109/" + dbName;
        String user = "U03rqG";
        String password = "53688064726";
        
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database : " + dbName);
        }

        catch (SQLException e){
            System.out.println("SQLException: "+ e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            System.exit(0);
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
        }
        return conn;
    }
}



