/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import controller.CalendarController;
import static controller.CalendarController.addItemToSQL;
import static controller.CalendarController.createTimeStamp;
import static controller.CalendarController.userName;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import static util.DBConnection.getConnection;



public class TabContacts {  
    //https://befused.com/mysql/reset-auto-increment
    
    public static ObservableList<Contact> contactsFromDB() throws SQLException{
        //storage for the contacts
        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        //items in the result set
        int rowCount = 0;
        //items for the collection
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sqlQuery = 
                "SELECT customer.customerId, customer.active, customer.customerName, address.phone, address.address, address.address2, city.city, country.country, address.postalCode " + 
                "FROM U03rqG.customer " +
                "JOIN ((U03rqG.city JOIN U03rqG.country ON city.countryId = country.countryId ) " + 
                    "JOIN U03rqG.address ON address.cityId = city.cityId) " +
                "ON customer.addressId = address.addressId;";
 
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            
            if(rs.last()){
                rowCount = rs.getRow();
                rs.beforeFirst();
                //System.out.println("Number of rows in this rs: " + rowCount);
            }
            if (rowCount > 0) {
                while (rs.next()) {
                    String id = rs.getString("customerId");
                    String active = rs.getString("active");
                    String customerName = rs.getString("customerName");
                    String phone = rs.getString("phone");
                    String address = rs.getString("address");
                    String address2 = rs.getString("address2");
                    String city = rs.getString("city");
                    String country = rs.getString("country");
                    String postalCode = rs.getString("postalCode");
                    
                    if (active.equalsIgnoreCase("1")) {active = "yes";}
                    if (active.equalsIgnoreCase("0")) {active = "no";}

                    Contact contactRow = new Contact(id, active, customerName, phone, address, address2, city, country, postalCode); 
                    contacts.add(contactRow);
                    //System.out.println("New Contact ");
                    //System.out.println(
                    //        "Name " + contactRow.getName() + 
                    //        " Phone " + contactRow.getPhone() + 
                    //        " Address " +contactRow.getAddress() + 
                    //        " Address2 "+ contactRow.getAddress2() + 
                    //        " City " + contactRow.getCity() + 
                    //        " ZipCode " + contactRow.getZipCode());
                    //System.out.println(contacts.size());
                }
            }
            System.out.println("Contacts table has been set");
        }
        catch (SQLException ex){
            System.out.println("There was a problem with the connection getting the Contacts from the DB");
            System.out.println(ex.getMessage());
        }
        finally{
            try { if (rs != null) rs.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {System.out.println(e.getMessage());};
            try { if (conn != null) conn.close(); } catch (Exception e) {System.out.println(e.getMessage());};
        }
        return contacts;
    }
    public static void populateContactTableView(TableView contactsTableView) throws SQLException{
        contactsTableView.setItems(contactsFromDB());
    }    

    public static void updateContact(Contact selectedContact, String nameInTextField, String phoneInTextField, String activeInTextField, 
            String countryInTextField, String cityInTextField, String addressInTextField,  String address2InTextField,
            String zipCodeInTextField, String contactIdInTextField) throws SQLException{
        
        //info from TableView
        String phoneTableView = selectedContact.getPhone();
        String countryTableView = selectedContact.getCountry();
        String cityTableView = selectedContact.getCity();
        String addressTableView = selectedContact.getAddress();
        String address2TableView = selectedContact.getAddress2();
        String zipCodeTableView = selectedContact.getZipCode();

        //info from TextFields
        if (activeInTextField.equalsIgnoreCase("no")){activeInTextField = "0";}
        if (activeInTextField.equalsIgnoreCase("yes")){activeInTextField = "1";}
        
        Boolean checkAddress = addressTableView.equalsIgnoreCase(addressInTextField);
        Boolean checkAddress2 = address2TableView.equalsIgnoreCase(address2InTextField);
        Boolean checkZipCode = zipCodeTableView.equalsIgnoreCase(zipCodeInTextField);
        Boolean checkPhoneAddress = phoneTableView.equalsIgnoreCase(phoneInTextField);
        Boolean checkCountryAddress = countryTableView.equalsIgnoreCase(countryInTextField);
        Boolean checkCityAddress = cityTableView.equalsIgnoreCase(cityInTextField);
        
        //check if fields have changed....
        if((checkAddress || checkAddress2 || checkZipCode || checkPhoneAddress || checkCountryAddress || checkCityAddress)){
            String countryIdFromSQL = getCountryId(countryInTextField);
            String cityIdFromSQL = getCityID(cityInTextField, countryIdFromSQL);
            String addressIdFromSQL = getAddressID(addressInTextField, address2InTextField, cityIdFromSQL, zipCodeInTextField, phoneInTextField);
            
            if (countryIdFromSQL == null){
                addCountryToSQL(countryInTextField);
                countryIdFromSQL = getCountryId(countryInTextField);
            }
            
            if (cityIdFromSQL == null){
                addCityToSQL(cityInTextField, countryIdFromSQL);
                cityIdFromSQL = getCityID(cityInTextField, countryIdFromSQL);
            }
            
            if (addressIdFromSQL == null){
                addAddressToSQL(addressInTextField, address2InTextField, phoneInTextField, zipCodeInTextField, cityIdFromSQL);
                addressIdFromSQL = getAddressID(addressInTextField, address2InTextField, cityIdFromSQL, zipCodeInTextField, phoneInTextField);
            }
            updateContact(nameInTextField, activeInTextField, addressIdFromSQL, contactIdInTextField);
        } else {
            updateContactSimple(nameInTextField, activeInTextField, contactIdInTextField);
        }        
    }
    public static void addNewContact(String nameInTextField, String phoneInTextField, String activeInTextField, String countryInTextField, 
           String cityInTextField, String addressInTextField, String address2InTextField, String zipCodeInTextField) throws SQLException{
        int active = 0;
        //info from TextFields
        if (activeInTextField.equalsIgnoreCase("no")){active = 0;}
        if (activeInTextField.equalsIgnoreCase("yes")){active = 1;}   
    
        String countryIdFromSQL = getCountryId(countryInTextField);
        String cityIdFromSQL = getCityID(cityInTextField, countryIdFromSQL);
        String addressIdFromSQL = getAddressID(addressInTextField, address2InTextField, cityIdFromSQL, zipCodeInTextField, phoneInTextField);

        if (countryIdFromSQL == null){
            addCountryToSQL(countryInTextField);
            countryIdFromSQL = getCountryId(countryInTextField);
        }

        if (cityIdFromSQL == null){
            addCityToSQL(cityInTextField, countryIdFromSQL);
            cityIdFromSQL = getCityID(cityInTextField, countryIdFromSQL);
        }

        if (addressIdFromSQL == null){
            addAddressToSQL(addressInTextField, address2InTextField, phoneInTextField, zipCodeInTextField, cityIdFromSQL);
            addressIdFromSQL = getAddressID(addressInTextField, address2InTextField, cityIdFromSQL, zipCodeInTextField, phoneInTextField);
        }
        
        int itemId = Integer.parseInt(addressIdFromSQL);
        String insertContactToSQL = 
            "INSERT INTO U03rqG.customer (customerName, addressId, active, createDate, createdBy, lastUpdateBy) " + 
            "VALUES ('" + 
                        nameInTextField + "', " + 
                        itemId +", " + 
                        active + ", '" + 
                        createTimeStamp() + "', '" + 
                        userName() + "', '" + 
                        userName() + "');";
        CalendarController.addItemToSQL(insertContactToSQL);
    }
    
    public static void updateContact(String customerName, String activeStatus, String addressId, String customerId) throws SQLException{
        String updateContact =
            "UPDATE U03rqG.customer " + 
            "SET customerName = '" + customerName + "', " +
                "active = '" +  activeStatus + "', " +
                "addressId = '" +  addressId + "', " +
                "lastUpdateBy = '" +  userName() + "' " +
            "WHERE (customerId = '" + customerId + "');";
        addItemToSQL(updateContact);
    }
    public static void updateContactSimple(String customerName, String activeStatus, String customerId) throws SQLException{
        String updateContact =
            "UPDATE U03rqG.customer " + 
            "SET customerName = '" + customerName + "', " +
                "active = '" +  activeStatus + "', " +
                "lastUpdateBy = '" +  userName() + "' " +
            "WHERE (customerId = '" + customerId + "');";
        addItemToSQL(updateContact);        
    }
        
    public static void addCountryToSQL(String countryInTextField) throws SQLException{
        String insertCountrySQL = 
            "INSERT INTO U03rqG.country (country, createDate, createdBy, lastUpdateBy) " +
            "VALUES ('" + countryInTextField + "', '" + createTimeStamp() + "', '" + userName() + "', '" + userName() + "');";
        CalendarController.addItemToSQL(insertCountrySQL);
    }
    public static void addCityToSQL(String cityInTextField, String countryIdFromSQL) throws SQLException{
        int countryId = Integer.parseInt(countryIdFromSQL);
        String insertCityToSQL = 
            "INSERT INTO U03rqG.city (city, countryId, createDate, createdBy, lastUpdateBy) " + 
            "VALUES ('" + cityInTextField + "', " + countryId + ", '" + createTimeStamp() + "', "
                    + "'" + userName() + "', '" + userName() + "');";
        CalendarController.addItemToSQL(insertCityToSQL);    
    }
    public static void addAddressToSQL(String addressInTextField, String address2InTextField, String phoneInTextField, String postalCodeInTextField, String cityIdFromSQL) throws SQLException, SQLException, SQLException{
        String insertAddressToSQL = 
            "INSERT INTO U03rqG.address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) " + 
            "VALUES ('" + addressInTextField + "', '" + address2InTextField +"', " + 
                cityIdFromSQL + ", '" + postalCodeInTextField + "', '" + phoneInTextField + "', '" + 
                createTimeStamp() + "', '" + userName() + "', '" + userName() + "');";
        CalendarController.addItemToSQL(insertAddressToSQL);
    }

    public static String getCountryId(String countryName) throws SQLException{
        String title = "countryId";
        String getCountryId = 
            "SELECT country.countryId FROM U03rqG.country " + 
            "WHERE country.country = '" +  countryName + "';";
        String countryId = CalendarController.getItemID(getCountryId, title);
        return countryId;
    }
    public static String getCityID(String cityName, String countryId) throws SQLException{ //uses information from customer Tableview for the lookup
        String title = "cityId";
        String getCityId = 
            "SELECT city.cityId FROM U03rqG.city " + 
            "WHERE (city = '" +  cityName + "' " +
                "AND countryId = '" + countryId + "');";
        
        String cityID =  CalendarController.getItemID(getCityId, title);
        return cityID;
    }
    public static String getAddressID(String address, String address2, String cityId, String zipCode, String phone) throws SQLException {
        String title = "addressId";

        String getAddressId =
                "SELECT address.addressId FROM U03rqG.address " +
                        "WHERE (address.address = '" + address + "' " +
                        "AND address.address2 = '" + address2 + "' " +
                        "AND address.cityId = '" + cityId + "' " +
                        "AND address.postalCode = '" + zipCode + "' " +
                        "AND address.phone = '" + phone + "');";

        String addressID = CalendarController.getItemID(getAddressId, title);
        return addressID;
    }
}
