/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainApp;


import controller.LoginScreenController;
import controller.CalendarController;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import resources.GetRB;
import static resources.GetRB.addLocalesSupportedToList;


/**
 *
 * @author emoud
 */
public class CalendarApp extends Application {
    private Stage primaryStage;
    private AnchorPane loginScreen;
    private ResourceBundle currentRB;
    
    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Calendar Application");
        
        addLocalesSupportedToList();
        getDefaultResourceBundle();
        initRootLayout();
        LoginScreenController.passResourceBundle(currentRB);
        //CalendarController.passResourceBundle(currentRB);
        //Appointment.passResourceBundle(currentRB);
        
    }
    
    public Stage getPrimaryStage(){
        return primaryStage;
    }

    public void initRootLayout() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(CalendarApp.class.getResource("/view/LoginScreen.fxml"));
        loader.setResources(currentRB);

        //load window
        loginScreen = (AnchorPane)loader.load();
        Scene scene = new Scene(loginScreen);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        LoginScreenController loginScreenController = loader.getController();
        loginScreenController.setMainApp(this);
    }
    
    public void mainScreen() throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(CalendarApp.class.getResource("/view/MainScreen.fxml"));
        loader.setResources(currentRB);
        
        AnchorPane anchorPane = (AnchorPane) loader.load();
        
        Scene scene = new Scene(anchorPane);
        Stage stage = new Stage();
        scene.getStylesheets().add("/util/CalendarApp.css");
        stage.setTitle("Calendar Application WGU");
        stage.setScene(scene);
        stage.show();
        
        
        
        CalendarController calendarController = loader.getController();
        calendarController.setMainApp(this);
    }
    
    public String startAppLanguage(){
        Locale currentLoc = Locale.getDefault();
        String langAtStartApp = currentLoc.getDisplayLanguage();
        return langAtStartApp;
    }
    
    public void getDefaultResourceBundle(){
        //get default locales to get the language
        String currentLanguage = startAppLanguage();
        ResourceBundle currentLanguageBundle = GetRB.getResourceBundle(currentLanguage);
        currentRB = currentLanguageBundle;
    }
    public void changeResourceBundle(String newLanguage){
        ResourceBundle newRS = GetRB.getResourceBundle(newLanguage);
        currentRB = newRS;
    }
    public ResourceBundle getCurrentResourceBundle(){
        return currentRB;
    }
    
    public Locale getCurrentLocale(){
        return currentRB.getLocale();
    }

    public String getCurrentLanguage(){
        String language = currentRB.getLocale().getDisplayLanguage();
        return language;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
