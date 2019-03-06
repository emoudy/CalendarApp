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
public class ApptPerMonth {

    private StringProperty jan;
    private StringProperty feb;
    private StringProperty mar;
    private StringProperty apr;
    private StringProperty may;
    private StringProperty jun;
    private StringProperty jul;
    private StringProperty aug;
    private StringProperty sep;
    private StringProperty oct;
    private StringProperty nov;
    private StringProperty dec;


    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     * @param jan
     * @param feb
     * @param mar
     * @param apr
     * @param may
     * @param jun
     * @param jul
     * @param aug
     * @param sep
     * @param oct
     * @param nov
     * @param dec
     */

    public ApptPerMonth(String jan, String feb, String mar, String apr, String may, String jun, String jul, String aug, String sep, String oct, String nov, String dec) {
        this.jan = new SimpleStringProperty(jan);
        this.feb = new SimpleStringProperty(feb);
        this.mar = new SimpleStringProperty(mar);
        this.apr = new SimpleStringProperty(apr);
        this.may = new SimpleStringProperty(may);
        this.jun = new SimpleStringProperty(jun);
        this.jul = new SimpleStringProperty(jul);
        this.aug = new SimpleStringProperty(aug);
        this.sep = new SimpleStringProperty(sep);
        this.oct = new SimpleStringProperty(oct);
        this.nov = new SimpleStringProperty(nov);
        this.dec = new SimpleStringProperty(dec);
    }

    public StringProperty janProperty() {
        return jan;
    }
    public StringProperty febProperty() {
        return feb;
    }
    public StringProperty marProperty() {
        return mar;
    }
    public StringProperty aprProperty() {
        return apr;
    }
    public StringProperty mayProperty() {
        return may;
    }
    public StringProperty junProperty() {
        return jun;
    }
    public StringProperty julProperty() {
        return jul;
    }
    public StringProperty augProperty() {
        return aug;
    }
    public StringProperty sepProperty() {
        return sep;
    }
    public StringProperty octProperty() {
        return oct;
    }
    public StringProperty novProperty() {
        return nov;
    }
    public StringProperty decProperty() {
        return dec;
    }
}
