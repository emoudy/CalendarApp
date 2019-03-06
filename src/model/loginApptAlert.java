/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import static model.Appointment.utcTimeToCurrentLocalDateTime;

/**
 *
 * @author emoud
 */
public class loginApptAlert {
    private static ResourceBundle calendarRS = null;
        
    public static void apptInFifteen(Connection conn, ComboBox calendarTimeZoneDisplayed) throws SQLException{
        //alerts for appointments within 15 min when userLogs
        System.out.println("Looking for appointments starting in the next 15 min....");
        //number of items in the result set
        int rowCount = 0; 
        //items to get the result set
        String column1 = "title";
        String column2 = "start";
        Statement stmt = null;
        ResultSet rs = null;
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();
        
         String sqlQuery = 
                "SELECT appointment.start " + 
                "FROM U03rqG.appointment " + 
                "WHERE start BETWEEN '" + year + "-" + month + "-" + day + " 00:00:00' " 
                 + "AND " + "'" + year + "-" + month + "-" + day + " 23:059:59';";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            
                
            if(rs.last()){
                rowCount = rs.getRow();
                rs.beforeFirst();
            }

            if (rowCount > 0) {
                while (rs.next()){
                    String start = rs.getString(column2);
                    
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    
                    String afterDash = start.substring(start.indexOf("-")+1, start.indexOf("-")+2);
                    if (afterDash.equalsIgnoreCase("0")){formatter = DateTimeFormatter.ofPattern("yyyy-M-dd HH:mm:ss");}

                    //Start TimeDB - assume UTC time
                    LocalDateTime appStartDateTime = LocalDateTime.parse(start, formatter);
                    String dbTime = appStartDateTime.toString();

                    //Start TimeDB to Calendar time zone time
                    String timezone = calendarTimeZoneDisplayed.toString();
                    LocalDateTime localDateStartTime = utcTimeToCurrentLocalDateTime(dbTime, calendarTimeZoneDisplayed);
                    LocalTime apptStartTime = localDateStartTime.toLocalTime();
                            
                    //getCurrentTime
                    LocalTime currentTime = LocalTime.now();
                    LocalTime inFifteen = currentTime.plusMinutes(15);
                    
                    if (apptStartTime.isAfter(currentTime) && apptStartTime.isBefore(inFifteen)) {
                        String midMsg = calendarRS.getString("fifteenWarming");
                        Alert a = new Alert(Alert.AlertType.INFORMATION, midMsg);
                        a.showAndWait();
                    } else {
                        System.out.println("No appointments in the next 15min");
                    }
                }
            }
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the Month Calendar ResultSet");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {System.out.println(e.getMessage());};
        }
    }
    public static void passResourceBundle(ResourceBundle currentRB){
        calendarRS = currentRB;
        System.out.println("passing the ResourceBundle to the loginApptAlert");
        String language = calendarRS.getLocale().getDisplayLanguage();
        System.out.println("The RB in the loginApptAlert says language = " + language);
    }
}
