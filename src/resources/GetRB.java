/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import controller.LoginScreenController;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author emoud
 */
public class GetRB {
    private static ObservableList<Locale> localesSupported = FXCollections.observableArrayList();
    
    public static ResourceBundle getResourceBundle(String language) {
    //Default value if the languge is not supported    
    Locale defaultLocale = localesSupported.get(0);
    
    Locale currentLocale = null;
    ResourceBundle resource = null;
    
    System.out.println(language + " has been suggested as a language");
    for (int i = 0; i < localesSupported.size(); ++i) {
      if (language.equalsIgnoreCase(localesSupported.get(i).getDisplayLanguage())) {
        currentLocale = localesSupported.get(i);
        System.out.println(language + " has been set as a language");
        break;
      }
    }
    if (currentLocale == null) {
        System.out.println(language + " is not yet supported.  Language default is " + defaultLocale.getDisplayLanguage());
        currentLocale = defaultLocale;
    }
    
    resource = ResourceBundle.getBundle("resources.Language", currentLocale);
    System.out.println(resource.getLocale() + " is the locale");
    
    LoginScreenController.passResourceBundle(resource);
    return resource;
  }
    
    public static void addLocalesSupportedToList(){
        if (localesSupported.isEmpty()){
            Locale mexico = new Locale("es", "MX");
            //Locale locales[] = {mexico, Locale.US};
            localesSupported.addAll(Locale.US, mexico);
        }
        String localesLanguages = "";
        for (int i = 0; i < localesSupported.size(); i++){
            localesLanguages = localesLanguages + " " + localesSupported.get(i).getDisplayLanguage();
        }
        System.out.println("languages supported are " +  localesLanguages);
    }
    
    public static ObservableList<Locale> getListOfLocalesSupported(){
        return localesSupported;
    }
}
