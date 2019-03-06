/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author emoud
 */
public class DBConnection {
    //private static final String serverName = "52.206.157.109";
    //private final String portNumber = "3306";
    //private static final String dbName = "U03rqG";
    private static final String user = "U03rqG";
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String url = "jdbc:mysql://52.206.157.109/U03rqG";
    private static final String password = "53688064726";
    
    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);    
        }
        catch (ClassNotFoundException | SQLException e){
            System.out.println(e.getMessage());
            System.exit(0);
        }
        return conn;
    }
}

