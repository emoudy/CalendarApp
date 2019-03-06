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
public class ReportProspectApp {
    private StringProperty id;
    private StringProperty customer;
    private StringProperty lastUpdate;
    private StringProperty dateTime;
    private StringProperty description;
    private StringProperty location;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     * @param id
     * @param customer
     * @param lastUpdate
     * @param dateTime
     * @param description
     * @param location
     */
    
    public ReportProspectApp(String id, String customer, String lastUpdate, String dateTime, String description, String location) {
        this.id = new SimpleStringProperty(id);
        this.customer = new SimpleStringProperty(customer);
        this.lastUpdate = new SimpleStringProperty(lastUpdate);
        this.dateTime = new SimpleStringProperty(dateTime);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty customerProperty() {
        return customer;
    }

    public StringProperty lastUpdateProperty() {
        return lastUpdate;
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
