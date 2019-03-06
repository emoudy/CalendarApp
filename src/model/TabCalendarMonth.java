/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import controller.CalendarController;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import static util.DBConnection.getConnection;

/**
 *
 * @author emoud
 */
public class TabCalendarMonth {
    private CalendarController calendarController = new CalendarController();
    
    public static int daysInCurrentMonth(LocalDate monthCalendarDate){
        LocalDate today = monthCalendarDate;
        int days = today.lengthOfMonth();
        return days;
    }
    
    public static int dayOfWeek(LocalDate monthCalendarDate){        
        LocalDate today = monthCalendarDate;
        int dayOfWeekNum = today.getDayOfWeek().getValue();
        return dayOfWeekNum;
    }
    
    public static int startOfMonth(LocalDate monthCalendarDate){        
        LocalDate today = monthCalendarDate;
        LocalDate monthFirstDay = LocalDate.of(today.getYear(), today.getMonth(), 1);
        int firstDayOfMonth = monthFirstDay.getDayOfWeek().getValue();
        if (firstDayOfMonth == 7){firstDayOfMonth = 0;}
        return firstDayOfMonth;
    }

    public static TextArea setTextAreaText(String day){
        TextArea numberDay = new TextArea();
        numberDay.setText(day);
        numberDay.setPrefHeight(17);
        numberDay.setPrefWidth(100.0);
        numberDay.setPadding(new Insets(5,5,5,5));
        return numberDay;
    }
    
    public static Label setText(String day){
        Label label = new Label();
        label.setText(day);
        label.setPrefHeight(17);
        label.setPrefWidth(100.0);
        return label;
    }
    
    public static VBox generateVBox(Label numberDay, int column, int row){
        VBox vbox = new VBox();
        VBox.setVgrow(numberDay, Priority.NEVER);
        vbox.getChildren().addAll(numberDay);
        vbox.setPadding(new Insets(5,5,5,5));
        vbox.getStyleClass().addAll("monthCalendarGrid");
        return vbox;
    }
    
    public static HBox generateHBox(Label numberDay, int column, int row){
        HBox hbox = new HBox();
        hbox.setHgrow(numberDay, Priority.NEVER);
        hbox.setMaxWidth(100);
        hbox.setMaxHeight(17);
        hbox.setSpacing(10);
        hbox.getChildren().addAll(numberDay);
        hbox.setPadding(new Insets(5,5,5,5));
        return hbox;
    }
   
    public static void addApptLabel(
            VBox vbox, 
            int monthDisplayed, 
            int yearDisplayed, 
            int day, 
            Connection conn,
               ComboBox apptCustomer, 
               TextField apptTitle, 
               TextField apptLocation,
               TextField apptURL,
               DatePicker datePicker,
               ComboBox startTime,
               ComboBox timeBlock,
               ComboBox appointmentType,
               TextArea apptDescription, 
               TextField apptId, 
               ComboBox calendarTimeZoneDisplayed
) throws SQLException{
        //number of items in the result set
        int rowCount = 0; 
        //items to get the result set
        String column1 = "title";
        String column2 = "appointmentId";
        Statement stmt = null;
        ResultSet rs = null; 
        
         String sqlQuery = 
                "SELECT appointment.title, appointment.appointmentId " + 
                "FROM U03rqG.appointment " + 
                "WHERE start BETWEEN '" + yearDisplayed + "-" + monthDisplayed + "-" + day + " 00:00:00' " 
                 + "AND " + "'" + yearDisplayed + "-" + monthDisplayed + "-" + day + " 23:059:59';";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);
                
            if(rs.last()){
                rowCount = rs.getRow();
                rs.beforeFirst();
            }

            if (rowCount > 0) {
                while (rs.next()){
                    String result1 = rs.getString(column1);
                    String result2 = rs.getString(column2);

                    Label title = setText(result1);
                    Label id = setText(result2);
                    id.setVisible(false);
                    
                    title.getStyleClass().addAll("titleLabel");
                    id.getStyleClass().addAll("calendarId");
                    
                    HBox hbox = new HBox();
                    hbox.getChildren().addAll(title, id);
                    
                    vbox.getChildren().addAll(hbox);
                    
                    //setOnMouseClock effects 
                    //lambda expression were used here to facilitate the code
                    hbox.setOnMousePressed(e -> {
                        System.out.println(title.getText() + " id: " + id.getText());
                        CalendarController.saveSelectedAppointmentId(result2); //stores the ID in CalendarController
                        hbox.getStyleClass().addAll("calendarApptSelected");
                    });

                    hbox.setOnMouseReleased(e -> {
                        hbox.getStyleClass().addAll("calendarApptReleased");
                        System.out.println("AppointmentID obtained: " + CalendarController.getApptID());

                        try {Appointment.editAppointment(apptCustomer, apptTitle, apptLocation, apptURL, datePicker, startTime,
                                    timeBlock, appointmentType, apptDescription, apptId, calendarTimeZoneDisplayed, CalendarController.getApptID());
                        } catch (SQLException ex) {Logger.getLogger(TabCalendarWeek.class.getName()).log(Level.SEVERE, null, ex);}
                    });
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
    
    /**
     * @param monthCalendarGrid
     * @param monthCalendarDate
     * @param apptCustomer
     * @param apptTitle
     * @param apptLocation
     * @param apptURL
     * @param datePicker
     * @param startTime
     * @param timeBlock
     * @param appointmentType
     * @param apptDescription
     * @param apptId
     * @param calendarTimeZoneDisplayed
     */
    public static void calendarMonthDates(
            GridPane monthCalendarGrid, 
            LocalDate monthCalendarDate,
            ComboBox apptCustomer, 
            TextField apptTitle, 
            TextField apptLocation,
            TextField apptURL,
            DatePicker datePicker,
            ComboBox startTime,
            ComboBox timeBlock,
            ComboBox appointmentType,
            TextArea apptDescription, 
            TextField apptId, 
            ComboBox calendarTimeZoneDisplayed){
        //items for the connection
        Connection conn = null;   
        
        
        try{
            //get connection
            conn = getConnection();
            //System.out.println("Days in current month: " + daysInCurrentMonth(monthCalendarDate));
            
            //get Row position
            int daysInFir = 7 - startOfMonth(monthCalendarDate);
            int daysInFifth = daysInCurrentMonth(monthCalendarDate) - (daysInFir + 21);
            int day = 1;

            //FirstRow
            for(int i = 0; i < daysInFir; i++){
                String dayString = Integer.toString(day);
                int row = 1;
                int column = startOfMonth(monthCalendarDate)+i;
                Label date = setText(dayString);
                VBox vbox = generateVBox(date, column, row);
                monthCalendarGrid.add(vbox, column, row);
                addApptLabel(
                        vbox, 
                        monthCalendarDate.getMonthValue(), 
                        monthCalendarDate.getYear(), 
                        day, 
                        conn,
                        apptCustomer, 
                        apptTitle, 
                        apptLocation,
                        apptURL,
                        datePicker,
                        startTime,
                        timeBlock,
                        appointmentType,
                        apptDescription, 
                        apptId, 
                        calendarTimeZoneDisplayed);
                day++;
            }

            //set Second/Third/Fourth row
            for (int r = 2; r < 5; r++){
                for(int i = 0; i < 7; i++){
                    String dayString = Integer.toString(day);
                    int column = i;
                    int row = r;
                    Label date = setText(dayString);
                    VBox vbox = generateVBox(date, column, row);
                    monthCalendarGrid.add(vbox, column, row);
                    addApptLabel(
                        vbox, 
                        monthCalendarDate.getMonthValue(), 
                        monthCalendarDate.getYear(), 
                        day, 
                        conn,
                        apptCustomer, 
                        apptTitle, 
                        apptLocation,
                        apptURL,
                        datePicker,
                        startTime,
                        timeBlock,
                        appointmentType,
                        apptDescription, 
                        apptId, 
                        calendarTimeZoneDisplayed);
                    day++;
                }
            }
            //FifthRow
            if (dayOfWeek(monthCalendarDate) > 0){
                for(int i = 0; i < 7; i++){
                    if ( day <= daysInCurrentMonth(monthCalendarDate)) {
                        String dayString = Integer.toString(day);
                        int column = i;
                        int row = 5;
                        Label date = setText(dayString);
                        VBox vbox = generateVBox(date, column, row);
                        monthCalendarGrid.add(vbox, column, row);
                        addApptLabel(
                            vbox, 
                            monthCalendarDate.getMonthValue(), 
                            monthCalendarDate.getYear(), 
                            day, 
                            conn,
                            apptCustomer, 
                            apptTitle, 
                            apptLocation,
                            apptURL,
                            datePicker,
                            startTime,
                            timeBlock,
                            appointmentType,
                            apptDescription, 
                            apptId, 
                            calendarTimeZoneDisplayed);
                        day++;
                    }
                }
            }
            //SixthRow
            if (dayOfWeek(monthCalendarDate) > 0){
                for(int i = 0; i < 7; i++){
                        if ( day <= daysInCurrentMonth(monthCalendarDate)) {
                        String dayString = Integer.toString(day);
                        int column = i;
                        int row = 6;
                        Label date = setText(dayString);
                        VBox vbox = generateVBox(date, column, row);
                        monthCalendarGrid.add(vbox, column, row);
                        addApptLabel(
                            vbox, 
                            monthCalendarDate.getMonthValue(), 
                            monthCalendarDate.getYear(), 
                            day, 
                            conn,
                            apptCustomer, 
                            apptTitle, 
                            apptLocation,
                            apptURL,
                            datePicker,
                            startTime,
                            timeBlock,
                            appointmentType,
                            apptDescription, 
                            apptId, 
                            calendarTimeZoneDisplayed);
                        day++;
                    }
                }
            }
            
            System.out.println("Calendar Month has been set");
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the Month Calendar connection");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (conn != null) conn.close(); }
            catch (SQLException e) {System.out.println(e.getMessage());}
        }
    }
    
    public static void clearMonthCalendar(GridPane monthCalendarGrid){  
        Node sun = monthCalendarGrid.getChildren().get(0);
        Node mon = monthCalendarGrid.getChildren().get(1);
        Node tue = monthCalendarGrid.getChildren().get(2);
        Node wed = monthCalendarGrid.getChildren().get(3);
        Node thr = monthCalendarGrid.getChildren().get(4);
        Node fri = monthCalendarGrid.getChildren().get(5);
        Node sat = monthCalendarGrid.getChildren().get(6);
        
        monthCalendarGrid.getChildren().clear();
        
        monthCalendarGrid.getChildren().add(0,sun);
        monthCalendarGrid.getChildren().add(1,mon);
        monthCalendarGrid.getChildren().add(2,tue);
        monthCalendarGrid.getChildren().add(3,wed);
        monthCalendarGrid.getChildren().add(4,thr);
        monthCalendarGrid.getChildren().add(5,fri);
        monthCalendarGrid.getChildren().add(6,sat);
        
        //monthCalendarGrid.getStyleClass().addAll("grid");
    }
}
