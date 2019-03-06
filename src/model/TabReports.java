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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import static util.DBConnection.getConnection;

/**
 *
 * @author emoud
 */
public class TabReports {
    
    public static void populateClientAppTableView(TableView clientApptReportTableView) throws SQLException{
        clientApptReportTableView.setItems(clientAppFromDB());
    }
    public static ObservableList<ReportClientApp> clientAppFromDB() throws SQLException{
        //storage for the contacts
        ObservableList<ReportClientApp> report = FXCollections.observableArrayList();
        //items in the result set
        int rowCount = 0;
        //items for the collection
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        String sqlQuery = 
                "SELECT appointment.appointmentId, customer.customerName, appointment.title, appointment.start, appointment.description, appointment.location " +
                "FROM (U03rqG.appointment JOIN U03rqG.customer ON appointment.customerId = customer.customerId) " +
                "WHERE appointment.type = 'Sales Support';";
 
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            
            if(rs.last()){
                rowCount = rs.getRow();
                rs.beforeFirst();
                //System.out.println("Number of rows in this rs: " + rowCount);
            }
            if (rowCount > 0) {
                while (rs.next()) {
                    String id = rs.getString("appointmentId");
                    String name = rs.getString("customerName");
                    String title = rs.getString("title");
                    String dateTime = rs.getString("start");
                    String description = rs.getString("description");
                    String location = rs.getString("location");

                    ReportClientApp newClientAppReport = new ReportClientApp(id, name, title, dateTime, description, location); 
                    report.add(newClientAppReport);
                    //System.out.println("Client Report ");
                    //System.out.println(
                    //        " id " + newClientAppReport.getId() + 
                    //        " name " + newClientAppReport.getName() + 
                    //        " title " +newClientAppReport.getTitle() + 
                    //        " dateTime "+ newClientAppReport.getDateTime() + 
                    //        " description " + newClientAppReport.getDescription() + 
                    //        " Location " + newClientAppReport.getLocation());
                    //System.out.println(Total number of contacts " + newClientAppReport.size());
                }
            }
            System.out.println("Client Appointment Report table has been set");
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the connection getting the Client Appointment Report from the DB");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (conn != null) conn.close(); } catch (Exception e) {System.out.println(e.getMessage());};
        }
        return report;
    
    }

    public static void populateProspectReportTableView(TableView prospectReportTableView) throws SQLException{
        ObservableList<ReportProspectApp> tableContent = prospectAppFromDB();
        prospectReportTableView.setItems(tableContent);
    }
    public static ObservableList<ReportProspectApp> prospectAppFromDB() throws SQLException{
        //storage for the contacts
        ObservableList<ReportProspectApp> report = FXCollections.observableArrayList();
        //items in the result set
        int rowCount = 0;
        //items for the collection
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        String sqlQuery = 
            "SELECT appointment.appointmentId, customer.customerName, appointment.lastUpdate, appointment.start, appointment.description, appointment.location " +
            "FROM U03rqG.customer " +
            "JOIN (U03rqG.user JOIN U03rqG.appointment ON user.userName = appointment.contact) " +
            "ON customer.customerId = appointment.customerId " +
            "WHERE appointment.type = 'Prospecting';";
 
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            
            if(rs.last()){
                rowCount = rs.getRow();
                rs.beforeFirst();
                //System.out.println("Number of rows in this rs: " + rowCount);
            }
            if (rowCount > 0) {
                while (rs.next()) {
                    String id = rs.getString("appointmentId");
                    String customer = rs.getString("customerName");
                    String lastUpdate = rs.getString("lastUpdate");
                    String dateTime = rs.getString("start");
                    String description = rs.getString("description");
                    String location = rs.getString("location");

                    ReportProspectApp newReport = new ReportProspectApp(id, customer, lastUpdate, dateTime, description, location); 
                    report.add(newReport);
                    //System.out.println("Client Report ");
                    //System.out.println(
                    //        " id " + newClientAppReport.getId() + 
                    //        " name " + newClientAppReport.getName() + 
                    //        " title " +newClientAppReport.getTitle() + 
                    //        " dateTime "+ newClientAppReport.getDateTime() + 
                    //        " description " + newClientAppReport.getDescription() + 
                    //        " Location " + newClientAppReport.getLocation());
                    //System.out.println(Total number of contacts " + newClientAppReport.size());
                }
            }
            System.out.println("Client Prospecting Report table has been set");
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the connection getting the Client Prospecting Report from the DB");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (conn != null) conn.close(); } catch (Exception e) {System.out.println(e.getMessage());};
        }
        return report;
    
    }

    public static void populateUserReportTableView(TableView userReportTableView) throws SQLException{
        userReportTableView.setItems(userAppFromDB());
    }
    public static ObservableList<ReportUserApp> userAppFromDB() throws SQLException{
        //storage for the contacts
        ObservableList<ReportUserApp> report = FXCollections.observableArrayList();
        //items in the result set
        int rowCount = 0;
        //items for the collection
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        String sqlQuery = 
                "SELECT appointment.appointmentId, customer.customerName, appointment.type, appointment.start, appointment.description, appointment.location " +
                "FROM U03rqG.customer " +
                "JOIN (U03rqG.user JOIN U03rqG.appointment ON user.userName = appointment.contact) " +
                "ON customer.customerId = appointment.customerId;";
 
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            
            if(rs.last()){
                rowCount = rs.getRow();
                rs.beforeFirst();
                //System.out.println("Number of rows in this rs: " + rowCount);
            }
            if (rowCount > 0) {
                while (rs.next()) {
                    String id = rs.getString("appointmentId");
                    String name = rs.getString("customerName");
                    String type = rs.getString("type");
                    String dateTime = rs.getString("start");
                    String description = rs.getString("description");
                    String location = rs.getString("location");

                    ReportUserApp newUserAppReport = new ReportUserApp(id, name, type, dateTime, description, location); 
                    report.add(newUserAppReport);
                    //System.out.println("Client Report ");
                    //System.out.println(
                    //        " id " + newClientAppReport.getId() + 
                    //        " name " + newClientAppReport.getName() + 
                    //        " title " +newClientAppReport.getTitle() + 
                    //        " dateTime "+ newClientAppReport.getDateTime() + 
                    //        " description " + newClientAppReport.getDescription() + 
                    //        " Location " + newClientAppReport.getLocation());
                    //System.out.println(Total number of contacts " + newClientAppReport.size());
                }
            }
            System.out.println("Client User Report table has been set");
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the connection getting the Client User Report from the DB");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (conn != null) conn.close(); } catch (Exception e) {System.out.println(e.getMessage());};
        }
        return report;
    
    }
    
    public static void populateUserMonthReportTableView(TableView userReportTableView, ComboBox calendarTimeZoneDisplayed, ComboBox userReportComboBox) throws SQLException{
        userReportTableView.setItems(userTypeAppFromDB(calendarTimeZoneDisplayed, userReportComboBox));
    }
    public static ObservableList<ApptPerMonth> userTypeAppFromDB(ComboBox calendarTimeZoneDisplayed, ComboBox userReportComboBox) throws SQLException{
        //storage for the contacts
        ObservableList<ApptPerMonth> report = FXCollections.observableArrayList();;
        //items in the result set
        int rowCount = 0;
        //items for the collection
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int jan = 0;
        int feb = 0;
        int mar = 0;
        int apr = 0;
        int may = 0;
        int jun = 0;
        int jul = 0;
        int aug = 0;
        int sep = 0;
        int oct = 0;
        int nov = 0;
        int dec = 0;
        
        String sqlQuery = 
                "SELECT appointment.type, appointment.start " +
                "FROM U03rqG.appointment; "; 
 
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            
            if(rs.last()){
                rowCount = rs.getRow();
                rs.beforeFirst();
                //System.out.println("Number of rows in this rs: " + rowCount);
            }
            if (rowCount > 0) {
                ArrayList<String> appTypes = new ArrayList<String>();
                appTypes.add("Sales Support");
                appTypes.add("Prospecting");
                appTypes.add("Training");
                appTypes.add("Staff Meeting");
                
                for (int i = 0; i < appTypes.size(); i++) {
                    while (rs.next()) {
                        String apptType = rs.getString("appointment.type");
                        String start = rs.getString("appointment.start");
                        int chosenYear = Integer.parseInt(userReportComboBox.getValue().toString());

                        //convert AppointmentSQL Times to current TimeZone time
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        ZonedDateTime calendarAppStartTime = TabCalendarWeek.databaseToCalendarTime(start, calendarTimeZoneDisplayed);
                        LocalDate calendarApptDate = calendarAppStartTime.toLocalDate();
                        int yearOfAppt = calendarApptDate.getYear();
                        int monthOfAppt = calendarApptDate.getMonthValue();

                        if (yearOfAppt == chosenYear && apptType.equalsIgnoreCase(appTypes.get(i))) {
                            switch (monthOfAppt) {
                                case 1:  jan++;
                                         break;
                                case 2:  feb++;
                                         break;
                                case 3:  mar++;
                                         break;
                                case 4:  apr++;
                                         break;
                                case 5:  may++;
                                         break;
                                case 6:  jun++;
                                         break;
                                case 7:  jul++;
                                         break;
                                case 8:  aug++;
                                         break;
                                case 9:  sep++;
                                         break;
                                case 10: oct++;
                                         break;
                                case 11: nov++;
                                         break;
                                case 12: dec++;
                                         break;

                            }
                        }

                    }
                    ApptPerMonth addAppType = new ApptPerMonth(Integer.toString(jan), Integer.toString(feb), Integer.toString(mar), 
                                    Integer.toString(apr), Integer.toString(may), Integer.toString(jun), Integer.toString(jul), 
                                    Integer.toString(aug), Integer.toString(sep), Integer.toString(oct), Integer.toString(nov), Integer.toString(dec)); 
                    report.add(addAppType);
                    rs.beforeFirst();
                }
            }
            System.out.println("Reports by Month table has been set");
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the connection getting the Client User Report from the DB");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (conn != null) conn.close(); } catch (Exception e) {System.out.println(e.getMessage());};
        }
        return report;
    
    }

}