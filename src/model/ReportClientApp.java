/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author emoud
 */
public class ReportClientApp {
    private StringProperty id;
    private StringProperty name;
    private StringProperty title;
    private StringProperty dateTime;
    private StringProperty description;
    private StringProperty location;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     * @param id
     * @param name
     * @param title
     * @param dateTime
     * @param description
     * @param location
     */
    
    public ReportClientApp(String id, String name, String title, String dateTime, String description, String location) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.title = new SimpleStringProperty(title);
        this.dateTime = new SimpleStringProperty(dateTime);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty dateTimeProperty() {
        return dateTime;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty locationProperty() {
        return location;
    }

}
