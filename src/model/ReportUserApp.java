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
public class ReportUserApp {
    
    private StringProperty id;
    private StringProperty customer;
    private StringProperty type;
    private StringProperty dateTime;
    private StringProperty description;
    private StringProperty location;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     * @param id
     * @param customer
     * @param type
     * @param dateTime
     * @param description
     * @param location
     */
    
    public ReportUserApp(String id, String customer, String type, String dateTime, String description, String location) {
        this.id = new SimpleStringProperty(id);
        this.customer = new SimpleStringProperty(customer);
        this.type = new SimpleStringProperty(type);
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

    public StringProperty typeProperty() {
        return type;
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
