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
public class Contact {
    private StringProperty id;
    private StringProperty active;
    private StringProperty name;
    private StringProperty phone;
    private StringProperty address;
    private StringProperty address2;
    private StringProperty city;
    private StringProperty country;
    private StringProperty zipCode;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     * @param active
     * @param name
     * @param phone
     * @param address
     * @param address2
     * @param city
     * @param country
     * @param zipCode
     */
    
    public Contact(String id, String active, String name, String phone, String address, String address2, String city, String country, String zipCode) {
        this.id = new SimpleStringProperty(id);
        this.active = new SimpleStringProperty(active);
        this.name = new SimpleStringProperty(name);
        this.phone = new SimpleStringProperty(phone);
        this.address = new SimpleStringProperty(address);
        this.address2 = new SimpleStringProperty(address2);
        this.city = new SimpleStringProperty(city);
        this.country = new SimpleStringProperty(country);
        this.zipCode = new SimpleStringProperty(zipCode);
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }
    public StringProperty idProperty() {
        return id;
    }
    
    public String getActive() {
        return active.get();
    }

    public void setActive(String active) {
        this.active.set(active);
    }
    public StringProperty activeProperty() {
        return active;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }
    public StringProperty nameProperty() {
        return name;
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }
    public StringProperty phoneProperty() {
        return phone;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }
    public StringProperty addressProperty() {
        return address;
    }

    public String getAddress2() {
        return address2.get();
    }

    public void setAddress2(String address2) {
        this.address2.set(address2);
    }
    public StringProperty address2Property() {
        return address2;
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
    }
    public StringProperty cityProperty() {
        return city;
    }

    public String getCountry() {
        return country.get();
    }

    public void setCountry(String country) {
        this.country.set(country);
    }
    public StringProperty countryProperty() {
        return country;
    }
    
    public String getZipCode() {
        return zipCode.get();
    }

    public void setZipCode(String zipCode) {
        this.zipCode.set(zipCode);
    }
    public StringProperty zipCodeProperty() {
        return zipCode;
    }
    
}
