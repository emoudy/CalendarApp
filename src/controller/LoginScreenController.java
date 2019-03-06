/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import MainApp.CalendarApp;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.loginApptAlert;
import resources.GetRB;
import static util.DBConnection.getConnection;

public class LoginScreenController implements Initializable {
    
    @FXML private TextField userNameTextField;    
    @FXML private PasswordField passwordTextField;
    @FXML private Button logInBtn, cancelBtn;
    @FXML private ComboBox languageComboBox;
    @FXML private Label timeZoneDetected;
    
    private static String userNameCalendar;
    private CalendarApp calendarApp;
    private String comboBoxLanguageValue = null;
    private static ResourceBundle calendarRS = null;

    
    
    //login
    public boolean logIn() throws IOException{
        boolean userLoggedIn = false;
        System.out.println("Checking Login credentials against our DB...27");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int rowCount = 0;
        
        String usernameRetrieved = userNameTextField.getText();
        String passwordRetrieved = passwordTextField.getText();
        
        try{
            String userNameAndPasswords = 
                "SELECT user.userName, user.password, user.active " +
                "FROM U03rqG.user;";
            conn = getConnection();
            if (conn == null) {System.out.println("Connection was null");}
            stmt = conn.createStatement();
            rs = stmt.executeQuery(userNameAndPasswords);

            if(rs.last()){
            rowCount = rs.getRow();
            rs.beforeFirst();
            }

            if (rowCount > 0) {
                while (rs.next()){
                    String userInSQL = rs.getString("userName");
                    String passwordInSQL = rs.getString("password");
                    String activeInSQL = rs.getString("active");
                    if(usernameRetrieved.equals(userInSQL) && passwordRetrieved.equals(passwordInSQL) && activeInSQL.equalsIgnoreCase("1")) {
                        userNameCalendar = userInSQL;
                        userLoggedIn = true;
                    }
                    else {
                        String msg = calendarRS.getString("wrongPasswordAlert");
                        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
                        a.showAndWait();
                    }
                }
            }
            System.out.println("finished Login credentails.");
            if (userLoggedIn == true){System.out.println("user has been logged in");
            } else {System.out.println("login failed");}
        }
        catch (SQLException ex) {
            System.out.println("There was a problem logging in");
            System.out.println(ex.getMessage());
        }
        catch (Throwable ex){
            System.out.println("Caught Throwable: " +  (ex.getMessage().equals("Throwable") ? "Expected" : "Unexpected"));
        }
        finally{
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println("Closing connection for Login");};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (conn != null) conn.close(); } catch (Exception e) {};
        }
        return userLoggedIn;
    }
    
    public void startApplicationAfterLogIn() throws IOException{
        CalendarController.passResourceBundle(calendarRS);
        Appointment.passResourceBundle(calendarRS);
        loginApptAlert.passResourceBundle(calendarRS);
        calendarApp.mainScreen();
    }
    
    public static String getUserName(){
        return userNameCalendar;
    }

    
    public void addLanguageOptionsToComboBox(){
        ObservableList<Locale> localesAvailable = GetRB.getListOfLocalesSupported();

        if (localesAvailable.size() > 0){
            ObservableList<String> locales = FXCollections.observableArrayList();
            locales.clear();
            for(int i=0; i < localesAvailable.size(); i++){
                String languages = localesAvailable.get(i).getDisplayLanguage();
                //adds the Languges from the locales to the comboBox
                locales.add(languages);
            }
            languageComboBox.getItems().addAll(locales);     
            
        } else {
            System.out.println("There are no locales available for this CalendarApp");
        }
    }
    
    public void checkLocalLanguageSupport(ResourceBundle rs){
        int i = 0;
        String language = rs.getLocale().getDisplayLanguage();
        if (language.equalsIgnoreCase("English")){
            i = 0;
            languageComboBox.setValue(language);
        } else if (language.equalsIgnoreCase("Spanish")){
            i = 1;
            languageComboBox.setValue(language);
        } else {
            String msg = calendarRS.getString("alertMessageLanguage");
            Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
            a.showAndWait();
            //https://docs.oracle.com/javase/tutorial/i18n/intro/steps.html
        }
    }
    
    
    public ComboBox getLanguageCOmboBOx(){
        return languageComboBox;
    }
    
    public String getLanguageValueOfComboBox(){
        comboBoxLanguageValue = languageComboBox.getValue().toString();
        return comboBoxLanguageValue;
    }
    
    public void changeLoginLanguage() throws IOException{
        String comboBoxValue = getLanguageValueOfComboBox();
        calendarApp.changeResourceBundle(comboBoxValue);
        calendarApp.initRootLayout();
    }
    
    public static void passResourceBundle(ResourceBundle currentRB){
        calendarRS = currentRB;
    }
    
    public void setMainApp(CalendarApp calendarApp) throws IOException {
        this.calendarApp = calendarApp;
        timeZoneDetected.setText(ZonedDateTime.now().getZone().toString());
    }
    
    public void writeInLoginFile() throws FileNotFoundException, UnsupportedEncodingException, IOException{
        LocalDateTime today = LocalDateTime.now();
        String user = userNameTextField.getText();
        
        try (Writer writer = new BufferedWriter(new FileWriter("src\\logs\\logFile.txt", true))) {
            writer.write(System.lineSeparator() + today + " -- '" + user + "' logged in");
        }
    }
    
        /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String language = calendarRS.getLocale().getDisplayLanguage();
        System.out.println("I'm initializing LoginScreenController");
        System.out.println("The RB in the LoginScreenController says language = " + language);
        checkLocalLanguageSupport(calendarRS);
        addLanguageOptionsToComboBox();
         /**
         * Buttons for the login
         */
        logInBtn.setOnAction((javafx.event.ActionEvent event) -> {
            try {
                //open the MainScreen
                if (logIn()){
                    writeInLoginFile();
                    startApplicationAfterLogIn();
                }
            }
            catch (IOException ex) {
                Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
            };
        });
        
        cancelBtn.setOnAction((javafx.event.ActionEvent event) -> {
            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            stage.close();
        });
        
        languageComboBox.setOnAction((e) -> {
            try {
                changeLoginLanguage();
            } catch (IOException ex) {
                Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }
    
}
