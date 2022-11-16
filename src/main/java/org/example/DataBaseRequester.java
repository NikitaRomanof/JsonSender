package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseRequester {

    private static final String URL = "jdbc:postgresql://localhost:5432/testDB";
    private static final String USER = "postgres";
    private static final String PASS = "0000";
    private String error = null;

    public String getError() {
        return error;
    }

    public void requestHelperFirstCriteria(List<String> resultRequest, Statement stat, CriteriaJson tmp) throws SQLException {

        ResultSet result = stat.executeQuery("select Customer_Surname, Customer_Name from customer " +
                    "where customer_surname = '" + tmp.getLastName() + "'");
        resultRequest.add("lastName");
        addResult(resultRequest, result);
    }

    public void requestHelperSecondCriteria(List<String> resultRequest, Statement stat, CriteriaJson tmp) throws SQLException {

        ResultSet result = stat.executeQuery("select customer.customer_surname,  customer.customer_name from buy \n" +
                "join products ON buy.Product_ID = products.product_id " +
                "join customer ON buy.Customer_ID = customer.Customer_ID " +
                "where products.product_name = '" + tmp.getProductName() +
                "' AND Count_product >= " + tmp.getMinTimes());
        resultRequest.add("productName");
        addResult(resultRequest, result);
    }

    public void requestHelperThirdCriteria(List<String> resultRequest, Statement stat, CriteriaJson tmp) throws SQLException {

        ResultSet result = stat.executeQuery("select Customer_Surname, Customer_Name from " +
                "(select Customer_Surname, Customer_Name, SUM(Product_Price * Count_product) AS price from buy " +
                "join customer ON buy.Customer_ID = customer.Customer_ID " +
                "join products on buy.Product_ID = products.Product_ID " +
                "group by Customer_Surname, Customer_Name) AS unitTab " +
                "where unitTab.price >=" + tmp.getMinExpenses() + " and unitTab.price <= " + tmp.getMaxExpenses());
        resultRequest.add("minExpenses");
        addResult(resultRequest, result);
    }

    public void requestHelperFourthCriteria(List<String> resultRequest, Statement stat, CriteriaJson tmp) throws SQLException {

        ResultSet result = stat.executeQuery("select Customer_Surname, Customer_Name from " +
                "(select Customer_Surname, Customer_Name, SUM(Product_Price * Count_product) AS price from buy " +
                "join customer ON buy.Customer_ID = customer.Customer_ID " +
                "join products on buy.Product_ID = products.Product_ID " +
                "group by Customer_Surname, Customer_Name) AS unitTab " +
                "Order by price ASC Limit " + tmp.getBadCustomers());
        resultRequest.add("badCustomers");
        addResult(resultRequest, result);
    }

    public void addResult(List<String> resultRequest, ResultSet result) throws SQLException {

        while(result.next()) {
            String buf = result.getString("Customer_Surname") + " " +
                    result.getString("Customer_Name");
            resultRequest.add(buf);
        }
        resultRequest.add("end");
    }

    public List<String> requestSearch(List<CriteriaJson> criteriaJson) {

        List<String> resultRequest = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            Class.forName("org.postgresql.Driver");
            Statement stat = conn.createStatement();
            for (CriteriaJson tmp : criteriaJson) {
                if (tmp.numberCriteria == 1) {
                    requestHelperFirstCriteria(resultRequest, stat, tmp);
                } else if (tmp.numberCriteria == 2) {
                    requestHelperSecondCriteria(resultRequest, stat, tmp);
                } else if (tmp.numberCriteria == 3) {
                    requestHelperThirdCriteria(resultRequest, stat, tmp);
                } else if (tmp.numberCriteria == 4) {
                    requestHelperFourthCriteria(resultRequest, stat, tmp);
                } else {
                    throw new IllegalArgumentException("Missing valid request criteria");
                }
            }

        } catch (ClassNotFoundException e) {
            error = "Driver not fount";
        } catch (SQLException e) {
            error = "Database not connected or request error";
        }
        return resultRequest;
    }

    public List<String> requestStat(List<String> dateList) {

        List<String> resultRequest = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            Class.forName("org.postgresql.Driver");
            Statement stat = conn.createStatement();
            ResultSet result = stat.executeQuery("select Customer_Surname, Customer_Name, Product_Name, price from " +
                    "(select Buy_DateTime, Customer_Surname, Customer_Name, Product_Name, (Product_Price * Count_product) AS price from buy " +
                    "join customer ON buy.Customer_ID = customer.Customer_ID " +
                    "join products on buy.Product_ID = products.Product_ID " +
                    ") AS unitTab " +
                    "where Buy_DateTime >= '" + dateList.get(0) + " 00:00:00' " + "AND Buy_DateTime <= '" + dateList.get(1) + " 23:59:59'");

            while(result.next()) {
                resultRequest.add("Start");
                String buf = result.getString("Customer_Surname") + " " +
                        result.getString("Customer_Name") + " " + result.getString("Product_Name") +
                        " " + result.getLong("price");
                resultRequest.add(buf);
                resultRequest.add("end");
            }

        } catch (ClassNotFoundException e) {
            error = "Driver not fount";
        } catch (SQLException e) {
            error = "Database not connected or request error";
        }

        return resultRequest;
    }


}
