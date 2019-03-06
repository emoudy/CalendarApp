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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.MINUTES;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static model.TabCalendarMonth.setText;
import static util.DBConnection.getConnection;

/**
 *
 * @author emoud
 */
public class TabCalendarWeek {
    private static HashMap<LocalTime, LocalTime> calendarTimesForMap = new HashMap();
    private static HashMap<LocalDate, HashMap<LocalTime, LocalTime>> startEndHashMapAppts = new HashMap();
    private CalendarController calendarController = new CalendarController();
    private static LocalDate mo;
    private static LocalDate tu;
    private static LocalDate we;
    private static LocalDate th;
    private static LocalDate fr;
    private static LocalDate sa;
    private static LocalDate su;
    
    //returns current month as "January"
    public static Month currentMonth(){
        LocalDate today = LocalDate.now();
        return today.getMonth();
    }
    
    public static int dayOfMonth(){
        LocalDate today = LocalDate.now();
        return today.getDayOfMonth();
    }
    
    public static DayOfWeek dayOfWeek(){
        LocalDate today = LocalDate.now();
        return today.getDayOfWeek();
    }
    
    public static LocalDate getMonDate(){ return mo;}
    public static LocalDate getTueDate(){ return tu;}
    public static LocalDate getWedDate(){ return we;}
    public static LocalDate getThuDate(){ return th;}
    public static LocalDate getFriDate(){ return fr;}
    public static LocalDate getSatDate(){ return sa;}
    public static LocalDate getSunDate(){ return su;}

    public static void calendarWeekDates(Label monday, Label tuesday, Label wednesday, Label thursday, Label friday, Label saturday, Label sunday, LocalDate weekCalendarDate) {
        LocalDate today = weekCalendarDate;

        for (int d = 0; d < 7; d++){
            String dateString = today.toString();
            int dayOfWeek = today.getDayOfWeek().getValue();
            
            switch (dayOfWeek) {
                case 1: monday.setText(dateString);
                        mo = today;
                        break;
                case 2: tuesday.setText(dateString);
                        tu = today;
                        break;
                case 3: wednesday.setText(dateString);
                        we = today;
                        break;
                case 4: thursday.setText(dateString);
                        th = today;
                        break;
                case 5: friday.setText(dateString);
                        fr = today;
                        break;
                case 6: saturday.setText(dateString);
                        sa = today;
                        today = today.minusDays(7);
                        break;
                case 7: sunday.setText(dateString);
                        su = today;
                        dayOfWeek = 0;
                        break;
            }
            today = today.plusDays(1);
            dayOfWeek++;
        }
        System.out.println("Calendar weekdays dates for the Week Calendar have been set");
    }
    
    public static void calendarWeekApptDisplay(
               GridPane weekCalendarApptGrid,
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
        Connection conn = null; 
        Statement stmt = null;
        ResultSet rs = null;
        int column = 0;
        //start populating the calendar with Sunday
        LocalDate startAtSunday = getSunDate();
        
        //items in the Result Set
        int rowCount = 0;

        try {
            weekCalendarApptGrid.getChildren().clear();
            conn = getConnection();
            //empty the HashMap to be refilled again
            startEndHashMapAppts.clear();
            
            //for each day of the week
            for (int d = 0; d < 7; d++) {
        
                String sqlQuery = 
                    "SELECT appointment.title, appointment.start, appointment.end, appointment.appointmentId, appointment.type " + 
                    "FROM U03rqG.appointment " +
                    "WHERE start BETWEEN '" + startAtSunday + " 00:00:00' AND '" + startAtSunday + " 23:59:59';";

                stmt = conn.createStatement();
                rs = stmt.executeQuery(sqlQuery);
                int row = 0;

                if(rs.last()){
                    rowCount = rs.getRow();
                    rs.beforeFirst();
                }
                //if there are appointments
                if (rowCount > 0) {
                    System.out.println("Looking for appointments on: " + startAtSunday);
                    //System.out.println( startAtSunday + " has " + rowCount + " appointment(s)");
                    //for each appointment of the day
                    while(rs.next()){
                        String start = rs.getString("appointment.start");
                        String end = rs.getString("appointment.end");
                        String title = rs.getString("appointment.title");
                        String id = rs.getString("appointment.appointmentId");
                        String type = rs.getString("appointment.type");
                        int rowSpan = 0;
                        int colSpan = 1;
                                      
                        //convert AppointmentSQL Times to current TimeZone time
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        //System.out.println("DB Appointment for: " + start);
                        ZonedDateTime calendarAppStartTime = databaseToCalendarTime(start, calendarTimeZoneDisplayed);
                        ZonedDateTime calendarAppEndTime = databaseToCalendarTime(end, calendarTimeZoneDisplayed);
                        LocalDate calendarApptDate = calendarAppStartTime.toLocalDate();
                        LocalTime calendarStartTime = calendarAppStartTime.toLocalTime();
                        LocalTime calendarEndTime = calendarAppEndTime.toLocalTime();
                        //System.out.println("Cal Appointment for: " + calendarStartTime);
                        
                        //Load the start/end calendar times with the date in the hashmap for later use
                        calendarTimesForMap.put(calendarStartTime, calendarEndTime);
                        startEndHashMapAppts.put(calendarApptDate, calendarTimesForMap);
                        
                        //set Vbox height
                        Long apptLength = calendarStartTime.until(calendarEndTime, MINUTES);
                        if (apptLength == 15) { 
                            rowSpan = 1;
                        } 
                        else if (apptLength == 30) { 
                            rowSpan = 2;
                        }
                        else if (apptLength == 45) { 
                            rowSpan = 3;
                        }
                        else if (apptLength == 60) { 
                            rowSpan = 4;
                        }
                        
                        //create the labels
                        Label apptTitleRetrieved = setText(title);
                        Label apptIdLabel = setText(id);
                        
                        apptIdLabel.getStyleClass().addAll("calendarId");
                        apptTitleRetrieved.getStyleClass().addAll("titleLabel");
                        apptIdLabel.setVisible(false);
                        
                        //add labels to a hbox
                        HBox hbox = new HBox();
                        hbox.getChildren().addAll(apptTitleRetrieved, apptIdLabel);
                        
                        //add labels to xBox
                        VBox vbox = new VBox();
                        vbox.getChildren().addAll(hbox);
                        vbox.setFillWidth(true);
                        
                        //vbox..setPrefSize(110, heightOfvBox);
                        vbox.getStyleClass().addAll("weekLabelvBox");
                        //System.out.println("Appointment type: " + type);
                        if (type.equalsIgnoreCase("Staff Meeting")){vbox.getStyleClass().addAll("apptStaffMeeting");}
                        if (type.equalsIgnoreCase("Sales Support")){vbox.getStyleClass().addAll("apptSalesSupport");}
                        if (type.equalsIgnoreCase("Prospecting")){vbox.getStyleClass().addAll("apptProspecting");}
                        if (type.equalsIgnoreCase("Training")){vbox.getStyleClass().addAll("appTraining");}

                        //setOnMouseClick effects to the vbox
                        vbox.setOnMousePressed(e -> {
                            System.out.println("Appointment selected: " + apptTitleRetrieved.getText());
                            CalendarController.saveSelectedAppointmentId(id); //stores the ID in CalendarController
                            vbox.getStyleClass().addAll("calendarApptSelected");
                        });
                        vbox.setOnMouseReleased(e -> {
                            vbox.getStyleClass().addAll("calendarApptReleased");
                            System.out.println("AppointmentID obtained: " + CalendarController.getApptID());
                            
                            try {Appointment.editAppointment(apptCustomer, apptTitle, apptLocation, apptURL, datePicker, startTime,
                                        timeBlock, appointmentType, apptDescription, apptId, calendarTimeZoneDisplayed, CalendarController.getApptID());
                            } catch (SQLException ex) {Logger.getLogger(TabCalendarWeek.class.getName()).log(Level.SEVERE, null, ex);}
                        });
                        
                        //get the row using the apointment start time
                        LocalTime appStartLoopTime = LocalTime.parse("16:44:00");
                        LocalTime appEndLoopTime = LocalTime.parse("08:44:00");
                        int i = 31;
                        while(appStartLoopTime.isAfter(appEndLoopTime)){
                            if (calendarStartTime.isAfter(appStartLoopTime)){
                                row = i;
                                weekCalendarApptGrid.add(vbox, column, row, colSpan, rowSpan);
                                //appStartLoopTime = LocalTime.parse("16:29:00");
                                break;
                            }
                            i--;
                            appStartLoopTime = appStartLoopTime.minusMinutes(15);
                        }
                        rowCount--; //decrement the number of appointments left to set in calendar
                    }
                }
                //look in the next day
                startAtSunday = startAtSunday.plusDays(1);  //add one day to the date
                column++;  //move to the next column
            }
            System.out.println("Calendar Week Schedule has been set");
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the Week Calendar RestultSet");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {System.out.println(e.getMessage());};
        }   
         
    }
    
    public static void clearWeekCalendar(GridPane weekCalendarApptsGrid){          
        weekCalendarApptsGrid.getChildren().clear();
        //monthCalendarGrid.getStyleClass().addAll("grid");
    }
    
    public static ZonedDateTime databaseToCalendarTime(String apptStartInDBldt, ComboBox calendarTimeZoneDisplayed){
        ZoneId dbZoneID = ZoneId.of("Etc/UTC");
        ZonedDateTime calendarAppZoneDateTimeStart = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        //get the start time of the DB and assign it a timeZone
        LocalDateTime dbLocalDateAndTime = LocalDateTime.parse(apptStartInDBldt, formatter);
        ZonedDateTime dbAppZoneDateTimeStart = dbLocalDateAndTime.atZone(dbZoneID);
        
        //change the appt ZoneDateTime to the Zone from the Calendar
        ZoneId ZoneIdCalendar = ZoneId.of(calendarTimeZoneDisplayed.getValue().toString());
        calendarAppZoneDateTimeStart = dbAppZoneDateTimeStart.withZoneSameInstant(ZoneIdCalendar);
        
        return calendarAppZoneDateTimeStart;
    }
    
    public static HashMap<LocalDate, HashMap<LocalTime, LocalTime>> getStartEndHashMapAppts(){
        return startEndHashMapAppts;
    }    
}
