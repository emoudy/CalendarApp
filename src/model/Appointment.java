/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import controller.CalendarController;
import static controller.CalendarController.createTimeStamp;
import static controller.CalendarController.userName;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import static util.DBConnection.getConnection;


public class Appointment {
    private static ResourceBundle calendarRS = null;
    
    public static void passResourceBundle(ResourceBundle currentRB){
        calendarRS = currentRB;
        System.out.println("passing the ResourceBundle to the Appointment.java");
        String language = calendarRS.getLocale().getDisplayLanguage();
        System.out.println("The RB in the Appointment.java says language = " + language);
    }
    
    public static void updateAppointment(
            String appointmentId, String customerId, String title, String description,
            String location, String contact, String url, String start, String end,
            String lastUpdateBy, String type) {
       int apptId = Integer.parseInt(appointmentId);
       int custID = Integer.parseInt(customerId);
       
       String sqlInsert = 
            "UPDATE U03rqG.appointment " +
            "SET customerId = " + custID +
                    ", title = '" + title +
                    "', description = '" + description +
                    "', location = '" + location +
                    "', contact = '" + contact +
                    "', url = '" + url +
                    "', start = '" + start +
                    "', end = '" + end +
                    "', lastUpdateBy ='" + lastUpdateBy +
                    "', type = '" + type + "'" +
            "WHERE appointmentId = "+ apptId +";";
       CalendarController.addItemToSQL(sqlInsert);
   }
   
    public static void createAppointment(
            String customerId, String title, String description, String location, String contact,
            String url, String start, String end, String createDate, String createdBy,
            String lastUpdateBy, String type) {
       
       String sqlInsert = 
            "INSERT INTO U03rqG.appointment (customerId, title, description, location, contact, url, " +
                    "start, end, createDate, createdBy, lastUpdateBy, type)" +
            "VALUES ('" + customerId + "', '" + title + "', '" + description + "', '" + location +
                    "', '" + contact + "', '" + url + "', '" +
                 start + "', '" + end + "', '"+ createDate + "', '" + createdBy + "', '" +
                    lastUpdateBy + "', '" + type + "');";
       CalendarController.addItemToSQL(sqlInsert);
   }
   
    public static void saveAppointment(
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
        //https://www.mkyong.com/java/java-convert-date-and-time-between-timezone/
        //https://www.mkyong.com/java8/java-8-how-to-format-localdatetime/
        
        //first check for null values
        Boolean contactCheck = !(apptCustomer.getSelectionModel().isEmpty());
        Boolean titleCheck = !(apptTitle.getText().trim().isEmpty());
        Boolean dateCheck = !(datePicker.getValue() == null);
        Boolean startCheck = !(startTime.getSelectionModel().isEmpty());
        Boolean typeCheck = !(appointmentType.getSelectionModel().isEmpty());
        
        //find the local start/end time per Calendar zone
        LocalDate apptDay = datePicker.getValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZoneId calendarAppZoneID = ZoneId.of(calendarTimeZoneDisplayed.getValue().toString());
        
        String apptStart = startTime.getValue().toString();
        LocalTime startTimeObtained = LocalTime.parse(apptStart);
        String apptEnd = getEndAppointment(startTimeObtained, apptDay, timeBlock);
        LocalTime endTimeObtained = LocalTime.parse(apptEnd, formatter);        
        
        Boolean noConflicts = !(apptOverlap(apptDay, startTimeObtained, endTimeObtained));
        //check for scheduling conflicts
        if (noConflicts) {
            //If fields are not null
            if(contactCheck && titleCheck && dateCheck && startCheck && typeCheck){
                //Customer
                String contactNameID = apptCustomer.getValue().toString();
                int colon = contactNameID.indexOf(":");
                String contactId = contactNameID.substring(0, colon);

                //Fields
                String title = apptTitle.getText();
                String description = apptDescription.getText();
                String location = apptLocation.getText();
                String url = apptURL.getText();
                String type = appointmentType.getValue().toString();

                //Appointment Type
                String appoitmentType = appointmentType.getValue().toString();

                //change Appointment TimeZone from localTimeZone to UTC TimeZone
                ZoneId dbZoneID = ZoneId.of("Etc/UTC");
                String start = localStartTimeZoneToUTC(calendarAppZoneID, startTime, datePicker);
                String end = localEndTimeZoneToUTC(formatter, apptEnd, calendarAppZoneID, dbZoneID, startTimeObtained);

                //check if appointment exists
                String appointmentId = apptId.getText();



                if (!(apptId.getText().trim().isEmpty())) {
                    //if there is an appointment ID, update appointment
                    Appointment.updateAppointment(appointmentId, contactId, title, description, location, userName(), url, start, end, userName(), type);
                } else {
                    //if there is no appointmentID, create new appointment
                    Appointment.createAppointment(contactId, title, description, location, userName(), url, start, end, createTimeStamp(), userName(), userName(), type);
                }
                clearAppointmentFields(apptCustomer, apptTitle, apptLocation, apptURL, datePicker, startTime, 
                        timeBlock, appointmentType, apptDescription, apptId);
                }
            else {
                //alerts for null fields
                HashMap checkHashMap = new HashMap();
                checkHashMap.put("Contacts", contactCheck);
                checkHashMap.put("Title", titleCheck);
                checkHashMap.put("Date", dateCheck);
                checkHashMap.put("Start Date", startCheck);
                checkHashMap.put("Appointment Type", typeCheck);

                Set set = checkHashMap.entrySet();
                Iterator i = set.iterator();
                String alertMessage = calendarRS.getString("contactFieldMissing");

                while(i.hasNext()) {
                    Map.Entry field = (Map.Entry)i.next();
                    if (field.getValue().toString() == "false"){
                        alertMessage += " " + field.getKey() + ",";
                    }
                 }
                int lastIndex = alertMessage.indexOf(",", alertMessage.length()-2);
                alertMessage = alertMessage.substring(0, lastIndex) + ".";

                Alert a = new Alert(Alert.AlertType.INFORMATION, alertMessage);
                a.showAndWait();
            }
        }
    }
       
    public static void clearAppointmentFields(
               ComboBox apptCustomer, 
               TextField apptTitle, 
               TextField apptLocation,
               TextField apptURL,
               DatePicker datePicker,
               ComboBox startTime,
               ComboBox timeBlock,
               ComboBox appointmentType,
               TextArea apptDescription, 
               TextField apptId){
        apptCustomer.setValue(null);
        apptTitle.setText("");
        apptLocation.setText("");
        apptURL.setText("");
        datePicker.setValue(null);
        startTime.setValue(null);
        timeBlock.setValue(null);
        appointmentType.setValue(null);
        apptDescription.setText("");
        apptId.setText("");   
    }
    
    //cumputes when the appointment ends bu adding TimeBlock
    public static String getEndAppointment(LocalTime startTimeObtained, LocalDate apptDay, ComboBox timeBlock){
        String apptEnd = null;
        int apptTimeBlock = Integer.parseInt(timeBlock.getValue().toString());
        LocalTime endTimeObtained = startTimeObtained.plusMinutes(apptTimeBlock);
        apptEnd = apptDay + " " + endTimeObtained + ":00";
        return apptEnd;
    }
    
    public static String localStartTimeZoneToUTC(ZoneId calendarAppZoneID, ComboBox startTime, DatePicker datePicker){
        ZoneId dbZoneID = ZoneId.of("Etc/UTC");
        String apptStartInDBldt = null;
        String apptStart = startTime.getValue().toString();
        LocalDate apptDay = datePicker.getValue();
        LocalTime startTimeObtained = LocalTime.parse(apptStart);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        String startAppointment = apptDay + " " + startTimeObtained + ":00";
        
        if (apptStart.length() < 5) {apptStart = "0" + startTime.getValue().toString();}
        
        LocalDateTime startLocaltDateAndTime = LocalDateTime.parse(startAppointment, formatter);
        //setting up the appointment to the Zone displayed in the calendar
        ZonedDateTime calendarAppZoneDateTimeStart = startLocaltDateAndTime.atZone(calendarAppZoneID);
        //change the appointment time to the database timeZone
        ZonedDateTime databaseAppZoneDateTimeStart = calendarAppZoneDateTimeStart.withZoneSameInstant(dbZoneID);
        
        System.out.println("CalendarApp startTime: " + calendarAppZoneDateTimeStart);
        System.out.println("Database startTime: " + databaseAppZoneDateTimeStart);
        
        apptStartInDBldt = databaseAppZoneDateTimeStart.toLocalDateTime().toString();
        
        return apptStartInDBldt;
    }
    
    public static String localEndTimeZoneToUTC(DateTimeFormatter formatter, String apptEnd, 
        ZoneId calendarAppZoneID, ZoneId dbZoneID, LocalTime startTimeObtained){
        String apptEndInDBldt = null;
        if (apptEnd.length() < 5) {apptEnd = "0" + apptEnd;}
        LocalDateTime endLocaltDateAndTime = LocalDateTime.parse(apptEnd, formatter);
        ZonedDateTime calendarAppZoneDateTimeEnd = endLocaltDateAndTime.atZone(calendarAppZoneID);
        //change the appointment time to the database timeZone
        ZonedDateTime databaseAppZoneDateTimeEnd = calendarAppZoneDateTimeEnd.withZoneSameInstant(dbZoneID);
        
        System.out.println("CalendarApp endTime: " + calendarAppZoneDateTimeEnd);
        System.out.println("Database endTime: " + databaseAppZoneDateTimeEnd);
        
        apptEndInDBldt = databaseAppZoneDateTimeEnd.toLocalDateTime().toString();
        
        return apptEndInDBldt;
    }
    //load appointment from the calendar to the fields pressing the "EDIT" button, need appointment ID from the Label
    public static void editAppointment(
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
               ComboBox calendarTimeZoneDisplayed, 
               String appointmentIDFromLabel) throws SQLException{
        
        int appointmentID = Integer.parseInt(appointmentIDFromLabel);
        Connection conn = null;  
        Statement stmt = null;
        ResultSet rs = null;
        int rowCount = 0;
         
        try{
            //get connection
            conn = getConnection();
            stmt = conn.createStatement();
            String getApptQuerry = 
                "SELECT appointment.appointmentId, appointment.customerId, customer.customerId, customer.customerName, appointment.title, "
                    + "appointment.description, appointment.location, appointment.url, appointment.start, appointment.end, appointment.type " +               
                "FROM (U03rqG.appointment JOIN U03rqG.customer ON appointment.customerId = customer.customerId) " + 
                "WHERE appointment.appointmentId = " + appointmentID + ";";           
            rs = stmt.executeQuery(getApptQuerry);
            
            if(rs.last()){
                rowCount = rs.getRow();
                rs.beforeFirst();
            }

            /**
             * Set appointment in fields
             */
            if (rowCount > 0) {
                while (rs.next()){
                //check if customer has changed
                apptTitle.setText(rs.getString("title"));
                apptLocation.setText(rs.getString("location"));
                apptURL.setText(rs.getString("url"));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                apptDescription.setText(rs.getString("description"));
                apptId.setText(appointmentIDFromLabel);

                //Customer                
                //customer registed in the database
                String name = rs.getString("customerName");
                int id = Integer.parseInt(rs.getString("customer.customerId"));
                String customerDisplay = id + ": " + name;
                apptCustomer.setValue(customerDisplay);

                //DatePicker
                String startString = rs.getString("start");
                System.out.println("CalendarApp UTC startDateTime: " + startString);
                
                String afterDash = startString.substring(startString.indexOf("-")+1, startString.indexOf("-")+2);
                if (afterDash.equalsIgnoreCase("0")){formatter = DateTimeFormatter.ofPattern("yyyy-M-dd HH:mm:ss");}
                
                //Start Time DB - assume UTC time
                LocalDateTime appStartDateTime = LocalDateTime.parse(startString, formatter);
                String dbTime = appStartDateTime.toString();
                
                //Start Time DB to Calendar time zone time
                LocalDateTime localDateStartTime = utcTimeToCurrentLocalDateTime(dbTime, calendarTimeZoneDisplayed);
                System.out.println("CalendarApp LocalStartDateTime: " + localDateStartTime.toString());
                LocalDate localDate = localDateStartTime.toLocalDate();
                datePicker.setValue(localDate);
                startTime.setValue(localDateStartTime.toLocalTime());
                
                //End Time DB
                String endString = rs.getString("end");
                System.out.println("CalendarApp UTC endDateTime: " + endString);
                LocalDateTime appEndDateTime = LocalDateTime.parse(endString, formatter);
                
                //End Time DB to Calendar time zone time
                LocalDateTime localDateEndTime = utcTimeToCurrentLocalDateTime(appEndDateTime.toString(), calendarTimeZoneDisplayed);
                System.out.println("CalendarApp LocalEndDateTime: " + localDateEndTime.toString());

                //Time Block
                Long timeBlockDisplayed = Duration.between(localDateStartTime.toLocalTime(), localDateEndTime.toLocalTime()).toMinutes();
                timeBlock.setValue(timeBlockDisplayed);
                
                //appointment type
                String appointmentTypeDisplayed = rs.getString("type");
                appointmentType.setValue(appointmentTypeDisplayed);
                }
            }
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the CalendarController editAppointment connection");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (conn != null) conn.close(); } catch (SQLException e) {System.out.println(e.getMessage());}
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {System.out.println(e.getMessage());};
        }
    }
    
    public static LocalDateTime utcTimeToCurrentLocalDateTime(String dbTime, ComboBox calendarTimeZoneDisplayed){
        //takes the time recorded in the DB and returns the time in the timezone displayed in the calendar
        String completeDBTime = dbTime + ":00";        
        LocalDateTime ldtFromDB = LocalDateTime.parse(completeDBTime);
        
        ZoneId dbZoneID = ZoneId.of("Etc/UTC");
        String calendarTimeZonedPassed = calendarTimeZoneDisplayed.getValue().toString();
        ZoneId calendarAppZoneID = ZoneId.of(calendarTimeZonedPassed);
        
        ZonedDateTime calendarDBZoneDateTime = ldtFromDB.atZone(dbZoneID);
        ZonedDateTime localZoneDateTimeEnd = calendarDBZoneDateTime.withZoneSameInstant(calendarAppZoneID);
        
        return localZoneDateTimeEnd.toLocalDateTime();
    }
    
    //checks if there is a conflict in adding an appointment
    public static Boolean apptOverlap(LocalDate apptDay, LocalTime startTimeObtained, LocalTime endTimeObtained){
        Boolean overLap = false;
        HashMap apptTimesSaved = TabCalendarWeek.getStartEndHashMapAppts();
        Set set = apptTimesSaved.entrySet();
        Iterator i = set.iterator();
        LocalTime closingTime = LocalTime.of(17, 00);

        while (i.hasNext()){
            //get the date
            Map.Entry date = (Map.Entry)i.next();
            LocalDate storedDate = (LocalDate)date.getKey();

            //get the times
            HashMap storedTimes = (HashMap)date.getValue();
            Set set2 = storedTimes.entrySet();
            Iterator i2 = set2.iterator();
            Map.Entry time = (Map.Entry)i2.next();
            LocalTime storedStartTimes = (LocalTime)time.getKey();
            LocalTime storedEndTimes = (LocalTime)time.getValue();

            if (storedDate.equals(apptDay)){
                //if the startTimeObtained is between the start/End of a current appointment
                if (storedStartTimes.isAfter(startTimeObtained) && storedStartTimes.isBefore(endTimeObtained)){
                    overLap = true;
                    String alertMessage = "Appointment time conflicts with another appointment.";
                    Alert a = new Alert(Alert.AlertType.INFORMATION, alertMessage);
                    a.showAndWait();
                } else if (storedEndTimes.isAfter(startTimeObtained) && storedEndTimes.isBefore(endTimeObtained)){
                //if the endTimeObtained is between the start/End of a current appointment
                    overLap = true;
                    String alertMessage = "Appointment time conflicts with another appointment.";
                    Alert a = new Alert(Alert.AlertType.INFORMATION, alertMessage);
                    a.showAndWait();
                } else if (endTimeObtained.isAfter(closingTime)) {
                    overLap = true;
                    String alertMessage = "Appointment goes past working hours of 9am to 5pm.";
                    Alert a = new Alert(Alert.AlertType.INFORMATION, alertMessage);
                    a.showAndWait();
                    //
                }
            }  
        }
        return overLap;
    }
}
