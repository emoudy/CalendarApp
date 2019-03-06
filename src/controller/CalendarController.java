/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import MainApp.CalendarApp;
import model.TabCalendarMonth;
import model.TabCalendarWeek;

import java.net.URL;
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
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.Contact;
import model.TabContacts;
import javafx.scene.control.DatePicker;
import model.Appointment;
import static model.Appointment.utcTimeToCurrentLocalDateTime;
import model.ApptPerMonth;
import model.Country;
import model.ReportClientApp;
import model.ReportProspectApp;
import model.ReportUserApp;
import model.TabReports;
import model.loginApptAlert;
import static util.DBConnection.getConnection;

/**
 * FXML Controller class
 *
 * @author emoud
 */
public class CalendarController implements Initializable {
    
    private LocalDate today;
    private ZonedDateTime calendarTimeZone;
    private LocalDate monthCalendarDate, weekCalendarDate;
    private static String appointmentIDFromLabel;
    private CalendarApp calendarApp;
    private static String userNameFromLogIn;
    private static ResourceBundle calendarRS = null;
    private String language = calendarRS.getLocale().getDisplayLanguage();
    
    //Calendars
    @FXML private GridPane monthCalendarGrid, weekCalendarApptsGrid;
    @FXML private Label titleDate, monthCalendarDateLabel, weekCalendarDateLabel;
    @FXML private Label sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    
    //Buttons to change the calendar display date
    @FXML private Button backwardsMonthBtn, forwardMonthBtn, backwardsWeekBtn, forwardWeekBtn;
    @FXML private ComboBox calendarTimeZoneDisplayed;
    
    //Create Appointment section
    @FXML private Button editApptBtn, deleteApptBtn, clearApptBtn, saveApptBtn;
    @FXML private ComboBox apptCustomer, startTime, timeBlock, appointmentType;
    @FXML private TextField apptTitle, apptLocation, apptURL;
    @FXML private TextField apptId, apptUserNameText;
    @FXML private TextArea apptDescription;
    @FXML private DatePicker datePicker;
    
    //Contacts TableView
    @FXML private TableView<Contact> contactsTableView;
    @FXML private TableColumn<Contact, String> colId;
    @FXML private TableColumn<Contact, String> colActive;
    @FXML private TableColumn<Contact, String> colName;
    @FXML private TableColumn<Contact, String> colPhone;
    @FXML private TableColumn<Contact, String> colAddress;
    @FXML private TableColumn<Contact, String> colAddress2;
    @FXML private TableColumn<Contact, String> colCity;
    @FXML private TableColumn<Contact, String> colCountry;
    @FXML private TableColumn<Contact, String> colZip;
    @FXML private Button saveContactBtn, editContactBtn, deleteContactBtn, clearContactBtn;
    @FXML private TextField contactCityTextField, contactAddressTextField;
    @FXML private TextField contactAddress2TextField, contactZipCodeTextField, contactNameTextField;
    @FXML private TextField contactPhoneTextField, contactIdTextField; 
    @FXML private ComboBox contactActiveComboBox, contactCountryComboBox;
    
    //Reports Client Appointments
    @FXML private TableView<ReportClientApp>clientApptReportTableView;
    @FXML private TableColumn<ReportClientApp, String> crApptId;
    @FXML private TableColumn<ReportClientApp, String> crCustomerId;
    @FXML private TableColumn<ReportClientApp, String> crTitle;
    @FXML private TableColumn<ReportClientApp, String> crDateTime;
    @FXML private TableColumn<ReportClientApp, String> crDescription;
    @FXML private TableColumn<ReportClientApp, String> crLocation;
    
    //Prospecting User Appointments
    @FXML private TableView<ReportProspectApp>prospectReportTableView;
    @FXML private TableColumn<ReportProspectApp, String> prApptId;
    @FXML private TableColumn<ReportProspectApp, String> prCustomer;
    @FXML private TableColumn<ReportProspectApp, String> prLastUpdate;
    @FXML private TableColumn<ReportProspectApp, String> prDateTime;
    @FXML private TableColumn<ReportProspectApp, String> prDescription;
    @FXML private TableColumn<ReportProspectApp, String> prLocation;
    
    //Reports User Appointments
    @FXML private TableView<ReportUserApp>userReportTableView;
    @FXML private TableColumn<ReportUserApp, String> urApptId;
    @FXML private TableColumn<ReportUserApp, String> urCustomer;
    @FXML private TableColumn<ReportUserApp, String> urType;
    @FXML private TableColumn<ReportUserApp, String> urDateTime;
    @FXML private TableColumn<ReportUserApp, String> urDescription;
    @FXML private TableColumn<ReportUserApp, String> urLocation;
    
    //User Month Appointment Report TableView
    @FXML private TableView<ApptPerMonth>userMonthReportTableView;
    @FXML private TableColumn<ApptPerMonth, String> umJan;
    @FXML private TableColumn<ApptPerMonth, String> umFeb;
    @FXML private TableColumn<ApptPerMonth, String> umMar;
    @FXML private TableColumn<ApptPerMonth, String> umApr;
    @FXML private TableColumn<ApptPerMonth, String> umMay;
    @FXML private TableColumn<ApptPerMonth, String> umJun;
    @FXML private TableColumn<ApptPerMonth, String> umJul;
    @FXML private TableColumn<ApptPerMonth, String> umAug;
    @FXML private TableColumn<ApptPerMonth, String> umSep;
    @FXML private TableColumn<ApptPerMonth, String> umOct;
    @FXML private TableColumn<ApptPerMonth, String> umNov;
    @FXML private TableColumn<ApptPerMonth, String> umDec;
    @FXML private ComboBox userReportComboBox;
    
    ObservableList<String> contactList = FXCollections.observableArrayList();
    ObservableList<String> activeStatus = FXCollections.observableArrayList();
    ObservableList<LocalTime> apptStartTimesAvailable = FXCollections.observableArrayList();
    ObservableList<String> incLength = FXCollections.observableArrayList();
    ObservableList<String> timeZone = FXCollections.observableArrayList();
    ObservableList<Country> countries = FXCollections.observableArrayList();
    ObservableList<Integer> userReportYearsAvailable = FXCollections.observableArrayList(2018);
    ObservableList<String> appointmentTypeList = FXCollections.observableArrayList("Staff Meeting", "Sales Support", "Prospecting", "Training");
    
    
    //todays date displayed in the botton right hand side of the calendar
    public void titleDate(){
        System.out.println("Today's date at the bottom right of the calendar has been set.");
        today = LocalDate.now();
        //month
        String currentMonth = today.getMonth().toString();
        int currentMonthInt = today.getMonth().getValue();
        //day of month
        int dayOfMonth = today.getDayOfMonth();
        //weekday
        String currentWeekDay = today.getDayOfWeek().toString();
        int currentWeekDayInt = today.getDayOfWeek().getValue();
        //year
        int currentYear = today.getYear();
        //locale to choose a language
        String language = calendarRS.getLocale().getDisplayLanguage();
        System.out.println("The RB in the CalendarController says language = " + language);
        
        
        if (language.equalsIgnoreCase("Spanish")){
            currentWeekDay = getSpanishWeekday(currentWeekDayInt);
        }
                
        if (language.equalsIgnoreCase("Spanish")){
            currentMonth = getSpanishMonth(currentMonthInt);
        }
        
        String todayDate = (currentWeekDay + " " + currentMonth + " " + dayOfMonth + ", "+ currentYear); 
        titleDate.setText(todayDate);
        System.out.println(todayDate);
        
        userReportComboBox.setValue(today.getYear());
        monthCalendarDate = today;
        weekCalendarDate = today;
    }
    public String getSpanishWeekday(int currentWeekDayInt){
        String currentWeekDay = null;
                switch (currentWeekDayInt) {
                case 1:  currentWeekDay = "Lunes";
                         break;
                case 2:  currentWeekDay = "Martes";
                         break;
                case 3:  currentWeekDay = "Miércoles";
                         break;
                case 4:  currentWeekDay = "Jueves";
                         break;
                case 5:  currentWeekDay = "Viernes";
                         break;
                case 6:  currentWeekDay = "Sábado ";
                         break;
                case 7:  currentWeekDay = "Domingo";
                         break;
            }
        return currentWeekDay;
    }  
    public String getSpanishMonth(int currentMonthInt){
        String currentMonth = null;
                switch (currentMonthInt) {
                case 1:  currentMonth = "Enero";
                         break;
                case 2:  currentMonth = "Febrero";
                         break;
                case 3:  currentMonth = "Marzo";
                         break;
                case 4:  currentMonth = "Abril";
                         break;
                case 5:  currentMonth = "Mayo";
                         break;
                case 6:  currentMonth = "Junio";
                         break;
                case 7:  currentMonth = "Julio";
                         break;
                case 8:  currentMonth = "Agosto";
                         break;
                case 9:  currentMonth = "Septiembre";
                         break;
                case 10:  currentMonth = "Octubre";
                         break;
                case 11:  currentMonth = "Noviembre";
                         break;
                case 12:  currentMonth = "Diciembre";
                         break;
            }
        return currentMonth;
    }
    
    public void calendarTimeZone(){
        /*
    //https://stackoverflow.com/questions/25561377/format-localdatetime-with-timezone-in-java8
    //https://www.mkyong.com/java8/java-display-all-zoneid-and-its-utc-offset/
    today = LocalDate.now();
    ZonedDateTime calendarTimeZone = ZonedDateTime.now();
    ZoneId zonedId = ZoneId.of(zoneId);
    
    today.atZone(ZoneId.of(calendarTimeZone.));
        */
    }
    
    //General Use
    public static String createTimeStamp(){
        LocalDate today = LocalDate.now();
        String createDate = today.toString();
        return createDate;
    }
    public static  String userName(){
        return userNameFromLogIn;
    }
    public static String getComboBoxValue(ComboBox combobox){
        String string = null;
        string = combobox.getSelectionModel().getSelectedItem().toString();
        return string;
    }
    public String getTextFieldValue(TextField textfield){
        String string = null;
        string = textfield.getText();
        return string;
    }
    public String getTextAreaValue(TextArea textarea){
        String string = null;
        string = textarea.getText();
        return string;
    }
    public void printTableContent(ResultSet rs, String column) throws SQLException{
        column = null;
        
        try{
            do {
                String result = rs.getString(column);
                System.out.println("Results: " + result);
            } while (rs.next());
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public void deleteItemFromDB(String tableName, String field, String id) throws SQLException{
        int idNumber = Integer.parseInt(id);
        System.out.println("Deleting id: " + id + " from table " +  tableName);
        String sqlStatement = 
                "DELETE FROM "+tableName+" " +
                "WHERE "+field+" = "+idNumber+";";
        addItemToSQL(sqlStatement);
    }
    public void sqlToObservableList(String sqlQuery, ObservableList<String> list, String title) throws SQLException{
        //pulls information from SQL to be used in an Observable List
        System.out.println("Preparing ObservableList for '" + title + "' ComboBox");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int rowCount = 0;
        
        try{       
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);

            if(rs.last()){
            rowCount = rs.getRow();
            rs.beforeFirst();
            }

            if (rowCount > 0) {
                while (rs.next()){
                    String item = rs.getString(title);
                    //put the items in an ObservableList to be used as a ComboBox
                    list.add(item);
                }
            }
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the putting items in an Oservable list connection");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (rs != null) rs.close(); } catch (Exception e) {};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (conn != null) conn.close(); } catch (Exception e) {};
        }
    }
    public void sqlToObservableListWithIDs(String sqlQuery, ObservableList<String> list, String title1, String titleID) throws SQLException{
        //pulls information from SQL to be used in an Observable List
        System.out.println("Preparing ObservableList for 'appClient' ComboBox");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int rowCount = 0;
        
        try{       
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);

            if(rs.last()){
            rowCount = rs.getRow();
            rs.beforeFirst();
            }

            if (rowCount > 0) {
                while (rs.next()){
                    String item1 = rs.getString(title1);
                    String item2 = rs.getString(titleID);
                    String item = item2 + ": " + item1;
                    //put the items in an ObservableList to be used as a ComboBox
                    list.add(item);
                }
            }
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the putting items in an OservableList connection");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (rs != null) rs.close(); } catch (Exception e) {};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (conn != null) conn.close(); } catch (Exception e) {};
        }
    }
    public String getUserId() throws SQLException{
        System.out.println("Getting userId");
        String id = null;
        String title = "userId";
        String userName = userName();
        String sqlQuery =             
            "SELECT user.userId " +
            "FROM U03rqG.user " +
            "WHERE userName = " + userName + ");" ;
        id =  getItemID(sqlQuery, title);
        return id;
    }
    public String getCustomerID(String customerName, String addressId) throws SQLException{
        System.out.println("Getting customerId of " + customerName);
        String id = null;
        String title = "customerId";
        String sqlQuery =             
            "SELECT customer.customeName " +
            "FROM U03rqG.customer " +
            "WHERE (customeName = " + customerName + " AND addressId = " + addressId + ");" ;
        id =  getItemID(sqlQuery, title);
        return id;
    } 
    public static String getItemID(String sqlQuery, String title) throws SQLException{
        System.out.println("Getting id of " + title);
        Connection conn = null;  
        Statement stmt = null;
        ResultSet rs = null;
        String itemID = null;
        int rowCount = 0;
         
        try{
            //get connection
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            
            if(rs.last()){
                rowCount = rs.getRow();
                rs.beforeFirst();
            }

            if (rowCount > 0) {
                while (rs.next()){
                    itemID = rs.getString(title);
                }
            }
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the getItemID in the CalendarController");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (conn != null) conn.close(); } catch (SQLException e) {System.out.println(e.getMessage());}
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {System.out.println(e.getMessage());};
        }
        return itemID;
    }
    public static void addItemToSQL(String sqlInsertQuery) throws SQLException{
        System.out.println("Running a SQL statement to add/delete an entry");
        Connection conn = getConnection();  
        Statement stmt = null;
        ResultSet rs = null;        
         
        try{
            //get connection
            conn = getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sqlInsertQuery);            
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the addItemToSQL");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (conn != null) conn.close(); } catch (SQLException e) {System.out.println(e.getMessage());}
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {System.out.println(e.getMessage());};
        }
    }
    public static void updateSQL(String sqlQuery){
        System.out.println("Running a SQL statement to update an entry");
        Connection conn = null;  
        Statement stmt = null;
        ResultSet rs = null;
         
        try{
            //get connection
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the CalendarController UpdateSQL connection");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (conn != null) conn.close(); } catch (SQLException e) {System.out.println(e.getMessage());}
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {System.out.println(e.getMessage());};
        }
    }
    
    //shared by Appointments and WeekTab calendar
    public static String localStartTimeZoneToUTC(ZoneId calendarAppZoneID, ZoneId dbZoneID, ComboBox startTime, DatePicker datePicker){
        //calendar to Database
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
    
    /**
     * Calendar Month Tab
     */
    public void setMonthDateInCalendar(){
        int monthYear = monthCalendarDate.getYear();
        String monthString = monthCalendarDate.getMonth().toString();
        int monthInt = monthCalendarDate.getMonth().getValue();
        
        if (language.equalsIgnoreCase("Spanish")){
            monthString = getSpanishMonth(monthInt);
        }
        
        String monthDateAsString = monthString + " " + monthYear;
        
        System.out.println("Date displayed in the Month calendar is " + monthDateAsString);
        monthCalendarDateLabel.setText(monthDateAsString);
    }
    public LocalDate weekCalendarDate(){return weekCalendarDate;}
    public LocalDate monthCalendarDate(){return monthCalendarDate;}
    public static String getApptIDFromLabel(Label appointment){
        String id = null;
        id = appointment.getText();
        CalendarController.saveSelectedAppointmentId(id);
        return id;
    } //also used by CalendarWeek
    public static void saveSelectedAppointmentId(String id){
        appointmentIDFromLabel = id;
    }//also used by CalendarWeek
    public static String getSelectedappointmentId(){
        return appointmentIDFromLabel;
    }
    /**
     * Calendar Week Tab
     */
    public void setWeekDateInCalendar(){   
        //sets up the dates in row 1 of the grid
        String weekMonthAsString = weekCalendarDate.getMonth().toString();
        int weekYear = weekCalendarDate.getYear();
        String weekDateAsString = weekMonthAsString + " " + weekYear;
        System.out.println("Date displayed in the Week calendar is " + weekDateAsString);
        weekCalendarDateLabel.setText(weekDateAsString);
    }
    
    /**
     * Contacts Tab
     * @throws java.sql.SQLException
     */

    public void saveContact() throws SQLException{
        
        //first check for null values
        Boolean nameCheck = !(contactNameTextField.getText().trim().isEmpty());
        Boolean phoneCheck = !(contactPhoneTextField.getText().trim().isEmpty());
        Boolean activeCheck = !(contactActiveComboBox.getSelectionModel().isEmpty());
        Boolean addressCheck = !(contactAddressTextField.getText().trim().isEmpty());
        Boolean address2Check = !(contactAddress2TextField.getText().trim().isEmpty());
        Boolean cityCheck = !(contactCityTextField.getText().trim().isEmpty());
        Boolean countryCheck = null;
        //Boolean countryCheck = !(contactCountryComboBox.getSelectionModel().isEmpty());
        //-------------------------
    //    Boolean countryCheck = false;
    //    if (contactCountryComboBox.getValue().toString().isEmpty() == null) {
    //        countryCheck = true;
    //    }
        //!(contactCountryComboBox.getValue().toString().isEmpty());
        //String country = ;
        if(contactCountryComboBox == null){
            System.out.println("country is null");
            countryCheck = false;
        } else if (contactCountryComboBox.getValue() == null){
            System.out.println("country is null with value");
            countryCheck = false;
        } else if (contactCountryComboBox.getValue().toString().length() < 1) {
            System.out.println("Country is less than 1");
            countryCheck = false;
        } else {
            System.out.println("Country is: " + contactCountryComboBox.getValue().toString());
            countryCheck = true;
        }
        
        //Boolean countryCheck = !((country.length() < 1) ? true : false);
        //Boolean countryCheck = !(contactCountryComboBox.getSelectionModel().isEmpty());
        //-----------------------
        Boolean zipCheck = !(contactZipCodeTextField.getText().trim().isEmpty());
        
        //String active = contactActiveComboBox.getValue().toString();
        //String country = contactCountryComboBox.getValue().toString();
        
        //If fields are not null
        if( nameCheck && phoneCheck && activeCheck && addressCheck && cityCheck && countryCheck && zipCheck){
            //check for bad phone entries
            Boolean phoneValid = checkPhoneEntry(contactPhoneTextField.getText());
            Boolean nameValid = checkNameEntry(contactNameTextField.getText());
            if (phoneValid && nameValid) {
                Contact selectedContact = contactsTableView.getSelectionModel().getSelectedItem();
                String nameInTextField = contactNameTextField.getText();
                String phoneInTextField = contactPhoneTextField.getText();
                String activeInTextField = contactActiveComboBox.getValue().toString();
                String countryInTextField = contactCountryComboBox.getValue().toString();
                String cityInTextField = contactCityTextField.getText();
                String addressInTextField = contactAddressTextField.getText();
                String address2InTextField = contactAddress2TextField.getText();
                String zipCodeInTextField = contactZipCodeTextField.getText();
                String contactIdInTextField = contactIdTextField.getText();

                //check if contact exists
                String contactIdFromSQL = contactIdTextField.getText();
                if (contactIdFromSQL.equalsIgnoreCase("")){contactIdFromSQL = null;}

                if (contactIdFromSQL != null) {
                    //update contact
                    TabContacts.updateContact(selectedContact, nameInTextField, phoneInTextField, activeInTextField, 
                        countryInTextField, cityInTextField, addressInTextField,  address2InTextField, zipCodeInTextField, contactIdInTextField);
                    clearTextFields();
                } else {
                    //create new contact
                    TabContacts.addNewContact(nameInTextField, phoneInTextField, activeInTextField, countryInTextField, cityInTextField, addressInTextField,
                        address2InTextField, zipCodeInTextField);
                    clearTextFields();
                }
            }
        } else {
         //alerts for null fields
            HashMap checkHashMap = new HashMap();
            checkHashMap.put("Name", nameCheck);
            checkHashMap.put("Phone", phoneCheck);
            checkHashMap.put("Active", activeCheck);
            checkHashMap.put("Address", addressCheck);
            checkHashMap.put("Address2", address2Check);
            checkHashMap.put("City", cityCheck);
            checkHashMap.put("Country", countryCheck);
            checkHashMap.put("ZipCode", zipCheck);
            
            System.out.println("I found an error saving but I'm not throwing an alarm");
            
            Set set = checkHashMap.entrySet();
            Iterator i = set.iterator();
            String msg = calendarRS.getString("contactFieldMissing");
            
            while(i.hasNext()) {
                Map.Entry field = (Map.Entry)i.next();
                if (field.getValue().toString() == "false"){
                    msg += " " + field.getKey() + ",";
                }
             }
            int lastIndex = msg.indexOf(",", msg.length()-2);
            msg = msg.substring(0, lastIndex) + ".";
            
            Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
            a.showAndWait();
        }     
    }   
    
    public Boolean checkPhoneEntry(String phone){
        Boolean goodEntry = false;
        //matches numbers and dashes
        //the phone should only have numbers 0-9
        //the phone should use dashes
        String regexStr = "^[0-9\\-]*$";
        
        if(phone.length() == 12 || (phone.matches(regexStr))){
            goodEntry = true;

        } else {
            String msg = calendarRS.getString("badPhoneEntry");
            Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
            a.showAndWait();
        }
        return goodEntry;
    }
    
    public Boolean checkNameEntry(String name){
       //since we don't have a 'lastname' colum, this check only checks one name entered
       Boolean goodEntry = false;
        //matches numbers and dashes
        //the name should use letters a-z, the first letter can be capitalized
        String regexStr = "[A-Z][a-zA-Z]*";
        
        if(name.matches(regexStr)){
            goodEntry = true;
        } else {
            String msg = calendarRS.getString("badNameEntry");
            Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
            a.showAndWait();
        } 
        return goodEntry;
    }
    public void setContactInTextFields() throws SQLException{
        //sets up the TextFields at the bottom of the contact list
        Contact selectedContact = contactsTableView.getSelectionModel().getSelectedItem();
        contactIdTextField.setText(selectedContact.getId());
        contactActiveComboBox.setValue(selectedContact.getActive());
        contactCountryComboBox.setValue(selectedContact.getCountry());
        contactCityTextField.setText(selectedContact.getCity());
        contactAddressTextField.setText(selectedContact.getAddress());
        contactAddress2TextField.setText(selectedContact.getAddress2());
        contactZipCodeTextField.setText(selectedContact.getZipCode());
        contactNameTextField.setText(selectedContact.getName());
        contactPhoneTextField.setText(selectedContact.getPhone());
    }
    public void clearTextFields() throws SQLException{
        contactIdTextField.setText("");
        contactActiveComboBox.setValue(null);
        contactCountryComboBox.setValue(null);
        contactCityTextField.setText("");
        contactAddressTextField.setText("");
        contactAddress2TextField.setText("");
        contactZipCodeTextField.setText("");
        contactNameTextField.setText("");
        contactPhoneTextField.setText("");
    }
    public void getCountries(){
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            String name = locale.getDisplayCountry(Locale.ENGLISH);
            if (name.length() > 1) {countries.add(new Country(name));}
          //https://memorynotfound.com/java-display-list-countries-using-locale-getisocountries/
        }
    }
    
    public void getCustomerActiveStatus() throws SQLException{
        String title = "active";
        String sqlQuery = 
            "SELECT customer.active " +
            "FROM U03rqG.customer;";
        sqlToObservableList(sqlQuery, activeStatus, title);
    }

    /**
     * Appointment
     */
    public void setUpTimeZoneComboBox() throws SQLException{
        //makes the drop down menu for the timezone in the make appointment section
        for (String id: ZoneId.getAvailableZoneIds()){
            timeZone.add(id);
            ZoneId z = ZoneId.systemDefault();
            calendarTimeZoneDisplayed.setValue(z);
            calendarTimeZoneDisplayed.getItems().addAll(timeZone);
        }
    }
    public void setUpTimeBlockComboBox() throws SQLException{
        //sets up the time blocks for the appointment section
        String title = "incrementTypeDescription";
        String sqlQuery = 
            "SELECT incrementtypes.incrementTypeDescription " +
            "FROM U03rqG.incrementtypes;";
        sqlToObservableList(sqlQuery, incLength, title);
    }
    public void setUpContactComboBox() throws SQLException{
        
        if (contactList.size() > 1){
            contactList.clear();
        }
        apptCustomer.getItems().clear();
        String title1 = "customerName";
        String titleID = "customerId";
        String sqlQuery = 
            "SELECT customer.customerName, customer.customerId " +
            "FROM U03rqG.customer;";
        sqlToObservableListWithIDs(sqlQuery, contactList, title1, titleID);
        apptCustomer.getItems().addAll(contactList);
    }
    public void setUpAvailableTimesComboBox(){
        LocalTime startTimeOfAppts = LocalTime.of(9, 00);
        
        apptStartTimesAvailable.add(startTimeOfAppts);
        for (int i = 0; i < 16; i++){
            LocalTime moreTime = startTimeOfAppts.plusMinutes(30);
            apptStartTimesAvailable.add(moreTime);
            startTimeOfAppts = moreTime;
        }
        startTime.getItems().addAll(apptStartTimesAvailable);
    }
    public void setUpMonthReportYearComboBox(){
        userReportComboBox.getItems().addAll(userReportYearsAvailable);
    }
    
    public static String getApptID(){return appointmentIDFromLabel;}    
    
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
        //load appointment from the calendar, need appointment ID from the Label
        //String appointmentIDFromLabel = CalendarController.getApptIdOfClient();
        
        
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
                //getCustomerID(); apptCustomer.getValue.toString
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
                
                LocalDateTime appStartDateTime = LocalDateTime.parse(startString, formatter); //this is in UTC time
                String dbTime = appStartDateTime.toString();
                LocalDateTime localDateStartTime = utcTimeToCurrentLocalDateTime(dbTime, calendarTimeZoneDisplayed); //this returns Calendar TimeZone
                System.out.println("CalendarApp LocalStartDateTime: " + localDateStartTime.toString());
                LocalDate localDate = localDateStartTime.toLocalDate();
                datePicker.setValue(localDate);

                //Start Time
                startTime.setValue(localDateStartTime.toLocalTime());
                
                //End Time
                String endString = rs.getString("end");
                System.out.println("CalendarApp UTC endDateTime: " + endString);
                LocalDateTime appEndDateTime = LocalDateTime.parse(endString, formatter);
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
    public static void passResourceBundle(ResourceBundle currentRB){
        calendarRS = currentRB;
        System.out.println("passing the ResourceBundle to the CalendarApp");
        String language = calendarRS.getLocale().getDisplayLanguage();
        System.out.println("The RB in the CalendarController says language = " + language);
    }

    //communication between controllers
    public void setMainApp(CalendarApp calendarApp){
        this.calendarApp = calendarApp;
        userNameFromLogIn = LoginScreenController.getUserName();
        apptUserNameText.setText(userNameFromLogIn);
        //titleDate();
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("I'm initializing CalendarController");
        /**
         * Main Calendar
         */

        titleDate();   
        setMonthDateInCalendar();
        setWeekDateInCalendar();
        getCountries();
        
        /**
        * ComboBox for setting up the calendar's TimeZones
        */
        try {setUpTimeZoneComboBox();} 
        catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        
        /**
        * ComboBox for creating/updating an appointment
        */
        try {setUpContactComboBox();} 
        catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        setUpAvailableTimesComboBox();
        try {setUpTimeBlockComboBox();} 
        catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        try {getCustomerActiveStatus();} 
        catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        setUpMonthReportYearComboBox();
        timeBlock.getItems().addAll(incLength);
        appointmentType.getItems().addAll(appointmentTypeList);
        contactActiveComboBox.getItems().addAll("yes", "no");
        contactCountryComboBox.getItems().addAll(countries);   
        
        /**
         * Buttons to change the calendar date
         * lambda expression were used in all the buttons because it's cleaner and we're not reusing the button codes
         */
        backwardsMonthBtn.setOnAction((javafx.event.ActionEvent event) -> {
            monthCalendarDate = monthCalendarDate.minusMonths(1);
            TabCalendarMonth.clearMonthCalendar(monthCalendarGrid);
            setMonthDateInCalendar();
            TabCalendarMonth.calendarMonthDates(monthCalendarGrid, monthCalendarDate(),apptCustomer, apptTitle, apptLocation, apptURL, 
                    datePicker, startTime, timeBlock, appointmentType, apptDescription, apptId,calendarTimeZoneDisplayed);
        });
        forwardMonthBtn.setOnAction((javafx.event.ActionEvent event) -> {
            monthCalendarDate = monthCalendarDate.plusMonths(1);
            TabCalendarMonth.clearMonthCalendar(monthCalendarGrid);
            setMonthDateInCalendar();
            TabCalendarMonth.calendarMonthDates(monthCalendarGrid, monthCalendarDate(),apptCustomer, apptTitle, apptLocation, apptURL, 
                    datePicker, startTime, timeBlock, appointmentType, apptDescription, apptId,calendarTimeZoneDisplayed);
        });
        backwardsWeekBtn.setOnAction((javafx.event.ActionEvent event) -> {
            weekCalendarDate = weekCalendarDate.minusWeeks(1);
            setWeekDateInCalendar();
            TabCalendarWeek.clearWeekCalendar(weekCalendarApptsGrid);
            TabCalendarWeek.calendarWeekDates(monday, tuesday, wednesday, thursday, friday, saturday, sunday, weekCalendarDate());
            TabCalendarWeek.calendarWeekApptDisplay(weekCalendarApptsGrid, apptCustomer, apptTitle, apptLocation, apptURL, datePicker, startTime, 
                    timeBlock, appointmentType, apptDescription, apptId, calendarTimeZoneDisplayed);
        });
        forwardWeekBtn.setOnAction((javafx.event.ActionEvent event) -> {
            weekCalendarDate = weekCalendarDate.plusWeeks(1);
            setWeekDateInCalendar();
            TabCalendarWeek.clearWeekCalendar(weekCalendarApptsGrid);
            TabCalendarWeek.calendarWeekDates(monday, tuesday, wednesday, thursday, friday, saturday, sunday, weekCalendarDate());
            TabCalendarWeek.calendarWeekApptDisplay(weekCalendarApptsGrid, apptCustomer, apptTitle, apptLocation, apptURL, datePicker, startTime, 
                    timeBlock, appointmentType, apptDescription, apptId, calendarTimeZoneDisplayed);
        });
        
        
        
        /**
         * BUTTONS for the Contacts Tab
         */
        saveContactBtn.setOnAction((javafx.event.ActionEvent event) -> {
            try {saveContact();} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
            try {TabContacts.populateContactTableView(contactsTableView);} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
            try {setUpContactComboBox();} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        });
        editContactBtn.setOnAction((javafx.event.ActionEvent event) -> {
            try {setContactInTextFields();} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        });
        
        deleteContactBtn.setOnAction((javafx.event.ActionEvent event) -> {
            Contact selectedContact = contactsTableView.getSelectionModel().getSelectedItem();
            String id = selectedContact.getId();
            try {deleteItemFromDB("U03rqG.customer", "customerId", id);} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
            contactsTableView.getItems().remove(selectedContact);
            try {clearTextFields();} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
            try {setUpContactComboBox();} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        });
        
        clearContactBtn.setOnAction((javafx.event.ActionEvent event) -> {
            try {clearTextFields();} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        });

        /**
         * BUTTONS for appointments
         */
        saveApptBtn.setOnAction((javafx.event.ActionEvent event) -> {
            try {Appointment.saveAppointment( apptCustomer, apptTitle, apptLocation, apptURL, datePicker, startTime, 
                    timeBlock, appointmentType, apptDescription, apptId, calendarTimeZoneDisplayed );} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
            //refresh the calendars
            TabCalendarMonth.calendarMonthDates(monthCalendarGrid, monthCalendarDate(),apptCustomer, apptTitle, apptLocation, apptURL, 
                    datePicker, startTime, timeBlock, appointmentType, apptDescription, apptId,calendarTimeZoneDisplayed);
            TabCalendarWeek.calendarWeekApptDisplay(weekCalendarApptsGrid, apptCustomer, apptTitle, apptLocation, apptURL, datePicker, startTime, 
                    timeBlock, appointmentType, apptDescription, apptId, calendarTimeZoneDisplayed);
            //refresh the reports
            try {TabContacts.populateContactTableView(contactsTableView);} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
            try {TabReports.populateProspectReportTableView(prospectReportTableView);} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);} 
            try {TabReports.populateUserReportTableView(userReportTableView);} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
            try {TabReports.populateUserMonthReportTableView(userMonthReportTableView, calendarTimeZoneDisplayed, userReportComboBox);} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        });
        /*
        //I left this code here incase I want to add an "Edit" button for the appointments.  
        //Currently, editing an appointment involves clicking on the appointment label
        editApptBtn.setOnAction((javafx.event.ActionEvent event) -> {
            try {Appointment.editAppointment(apptCustomer, apptTitle, apptLocation, apptURL, datePicker, startTime, 
                    timeBlock, appointmentType, apptDescription, apptId, calendarTimeZoneDisplayed, appointmentIDFromLabel);} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        });
        */
        clearApptBtn.setOnAction((javafx.event.ActionEvent event) -> { Appointment.clearAppointmentFields(apptCustomer, apptTitle, apptLocation, apptURL, datePicker, startTime, 
                    timeBlock, appointmentType, apptDescription, apptId);});
        
        deleteApptBtn.setOnAction((javafx.event.ActionEvent event) -> {
            String id = appointmentIDFromLabel;
            try {deleteItemFromDB("U03rqG.appointment", "appointmentId", id);} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
            Appointment.clearAppointmentFields(apptCustomer, apptTitle, apptLocation, apptURL, datePicker, startTime, 
                    timeBlock, appointmentType, apptDescription, apptId);
            //refresh the calendars
            TabCalendarMonth.calendarMonthDates(monthCalendarGrid, monthCalendarDate(),apptCustomer, apptTitle, apptLocation, apptURL, 
                    datePicker, startTime, timeBlock, appointmentType, apptDescription, apptId,calendarTimeZoneDisplayed);
            TabCalendarWeek.calendarWeekApptDisplay(weekCalendarApptsGrid, apptCustomer, apptTitle, apptLocation, apptURL, datePicker, startTime, 
                    timeBlock, appointmentType, apptDescription, apptId, calendarTimeZoneDisplayed);
            //refresh the reports
            try {TabContacts.populateContactTableView(contactsTableView);} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
            try {TabReports.populateProspectReportTableView(prospectReportTableView);} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);} 
            try {TabReports.populateUserReportTableView(userReportTableView);} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
            try {TabReports.populateUserMonthReportTableView(userMonthReportTableView, calendarTimeZoneDisplayed, userReportComboBox);} 
            catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        });
        
        /**
         * Load Calendars
         */        
        TabCalendarMonth.calendarMonthDates(monthCalendarGrid, monthCalendarDate(),apptCustomer, apptTitle, apptLocation, apptURL, 
                    datePicker, startTime, timeBlock, appointmentType, apptDescription, apptId, calendarTimeZoneDisplayed);
        TabCalendarWeek.calendarWeekDates(monday, tuesday, wednesday, thursday, friday, saturday, sunday, weekCalendarDate());
        TabCalendarWeek.calendarWeekApptDisplay(weekCalendarApptsGrid, apptCustomer, apptTitle, apptLocation, apptURL, datePicker, startTime, 
                    timeBlock, appointmentType, apptDescription, apptId, calendarTimeZoneDisplayed);       
        
        /**
         * Contacts Tab
         */
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colActive.setCellValueFactory(cellData -> cellData.getValue().activeProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        colAddress.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        colAddress2.setCellValueFactory(cellData -> cellData.getValue().address2Property());
        colCity.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        colCountry.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        colZip.setCellValueFactory(cellData -> cellData.getValue().zipCodeProperty());    
       
        try {TabContacts.populateContactTableView(contactsTableView);} 
        catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        
        /**
         * Report Tab
         */
        //Client Report TableView
        crApptId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        crCustomerId.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        crTitle.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        crDateTime.setCellValueFactory(cellData -> cellData.getValue().dateTimeProperty());
        crDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        crLocation.setCellValueFactory(cellData -> cellData.getValue().locationProperty());  
        
        try {TabReports.populateClientAppTableView(clientApptReportTableView);} 
        catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        
        //Prospecting Report TableView
        prApptId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        prCustomer.setCellValueFactory(cellData -> cellData.getValue().customerProperty());
        prLastUpdate.setCellValueFactory(cellData -> cellData.getValue().lastUpdateProperty());
        prDateTime.setCellValueFactory(cellData -> cellData.getValue().dateTimeProperty());
        prDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        prLocation.setCellValueFactory(cellData -> cellData.getValue().locationProperty());  
        
        try {TabReports.populateProspectReportTableView(prospectReportTableView);} 
        catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}   
        
        //User Report TableView
        urApptId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        urCustomer.setCellValueFactory(cellData -> cellData.getValue().customerProperty());
        urType.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        urDateTime.setCellValueFactory(cellData -> cellData.getValue().dateTimeProperty());
        urDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        urLocation.setCellValueFactory(cellData -> cellData.getValue().locationProperty());  
        
        try {TabReports.populateUserReportTableView(userReportTableView);} 
        catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
        
        //User Reports ApptPerMonth TableView
        umJan.setCellValueFactory(cellData -> cellData.getValue().janProperty());
        umFeb.setCellValueFactory(cellData -> cellData.getValue().febProperty());
        umMar.setCellValueFactory(cellData -> cellData.getValue().marProperty());
        umApr.setCellValueFactory(cellData -> cellData.getValue().aprProperty());
        umMay.setCellValueFactory(cellData -> cellData.getValue().mayProperty());
        umJun.setCellValueFactory(cellData -> cellData.getValue().junProperty());
        umJul.setCellValueFactory(cellData -> cellData.getValue().julProperty());
        umAug.setCellValueFactory(cellData -> cellData.getValue().augProperty());
        umSep.setCellValueFactory(cellData -> cellData.getValue().sepProperty());
        umOct.setCellValueFactory(cellData -> cellData.getValue().octProperty());
        umNov.setCellValueFactory(cellData -> cellData.getValue().novProperty());
        umDec.setCellValueFactory(cellData -> cellData.getValue().decProperty());

        try {TabReports.populateUserMonthReportTableView(userMonthReportTableView, calendarTimeZoneDisplayed, userReportComboBox);} 
        catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}

        //Alerts for appt starting in the next 15min
        try {loginApptAlert.apptInFifteen(getConnection(), calendarTimeZoneDisplayed);} 
        catch (SQLException ex) {Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);}
    }    
}