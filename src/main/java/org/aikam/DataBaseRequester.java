package org.aikam;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataBaseRequester {

    private static final String URL = "jdbc:postgresql://localhost:5432/testDB";
    private static final String USER = "postgres";
    private static final String PASS = "0000";
    private String error = null;

    public String getError() {
        return error;
    }

    public void requestHelperFirstCriteria(List<String> resultRequest, Statement stat, CriteriaJson tmp) throws SQLException {
        StringBuilder request = new StringBuilder("select Customer_Surname, Customer_Name from customer ");
        request.append("where customer_surname = '").append(tmp.getLastName()).append("'");
        ResultSet result = stat.executeQuery(String.valueOf(request));
        StringBuilder builderRequest = new StringBuilder("   {\n      \"criteria\": {\"lastName\": \"");
        builderRequest.append(tmp.getLastName()).append("\"},\n      \"results\": [\n");
        resultRequest.add(String.valueOf(builderRequest));
        addResult(resultRequest, result);
    }

    public void requestHelperSecondCriteria(List<String> resultRequest, Statement stat, CriteriaJson tmp) throws SQLException {
        StringBuilder request = new StringBuilder("select customer.customer_surname,  customer.customer_name from buy \n");
        request.append("join products ON buy.Product_ID = products.product_id join customer ON buy.Customer_ID = customer.Customer_ID ");
        request.append("where products.product_name = '").append(tmp.getProductName());
        request.append("' AND Count_product >= ");
        request.append(tmp.getMinTimes());
        ResultSet result = stat.executeQuery(String.valueOf(request));

        StringBuilder builderRequest = new StringBuilder("   {\n      \"criteria\" :{\"productName\": \"");
        builderRequest.append(tmp.getProductName()).append("\",\"minTimes\": ").append(tmp.getMinTimes());
        builderRequest.append("},\n      \"results\": [\n");
        resultRequest.add(String.valueOf(builderRequest));
        addResult(resultRequest, result);
    }

    public void requestHelperThirdCriteria(List<String> resultRequest, Statement stat, CriteriaJson tmp) throws SQLException {
        StringBuilder request = new StringBuilder("select Customer_Surname, Customer_Name from ");
        request.append("(select Customer_Surname, Customer_Name, SUM(Product_Price * Count_product) AS price from buy ");
        request.append("join customer ON buy.Customer_ID = customer.Customer_ID ");
        request.append("join products on buy.Product_ID = products.Product_ID ");
        request.append("group by Customer_Surname, Customer_Name) AS unitTab ");
        request.append("where unitTab.price >= ").append(tmp.getMinExpenses());
        request.append(" and unitTab.price <= ").append(tmp.getMaxExpenses());
        ResultSet result = stat.executeQuery(String.valueOf(request));

        StringBuilder builderRequest = new StringBuilder("   {\n      \"criteria\": {\"minExpenses\": ");
        builderRequest.append(tmp.getMinExpenses()).append(",\"maxExpenses\": ");
        builderRequest.append(tmp.getMaxExpenses()).append("},\n      \"results\": [\n");
        resultRequest.add(String.valueOf(builderRequest));
        addResult(resultRequest, result);
    }

    public void requestHelperFourthCriteria(List<String> resultRequest, Statement stat, CriteriaJson tmp) throws SQLException {
        StringBuilder request = new StringBuilder("select Customer_Surname, Customer_Name from ");
        request.append("(select Customer_Surname, Customer_Name, SUM(Product_Price * Count_product) AS price from buy ");
        request.append("join customer ON buy.Customer_ID = customer.Customer_ID ");
        request.append("join products on buy.Product_ID = products.Product_ID ");
        request.append("group by Customer_Surname, Customer_Name) AS unitTab ");
        request.append("Order by price ASC Limit ").append(tmp.getBadCustomers());
        ResultSet result = stat.executeQuery(String.valueOf(request));

        StringBuilder builderRequest = new StringBuilder("   {\n      \"criteria\": {\"badCustomers\":");
        builderRequest.append(tmp.getBadCustomers()).append("},\n      \"results\": [\n");
        ;
        resultRequest.add(String.valueOf(builderRequest));
        addResult(resultRequest, result);
    }

    public void addResult(List<String> resultRequest, ResultSet result) throws SQLException {
        while (result.next()) {
            StringBuilder buffer = new StringBuilder("");
            buffer.append("      {\"lastName\": \"").append(result.getString("Customer_Surname"));
            buffer.append("\", \"firstName\": \"").append(result.getString("Customer_Name")).append("\"}, \n");
            resultRequest.add(String.valueOf(buffer));
        }

        changeLine(resultRequest.get(resultRequest.size() - 1), resultRequest, 3);
        resultRequest.add("\n    ]\n   },\n");
    }

    private void changeLine(String str, List<String> resultRequest, int pos) {
        resultRequest.set(resultRequest.size() - 1, str.substring(0, str.length() - pos));
    }

    public List<String> requestSearch(List<CriteriaJson> criteriaJson) {

        List<String> resultRequest = new ArrayList<>();
        resultRequest.add("{\n \"type\": \"search\",\n \"result\": [\n");
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
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
            changeLine(resultRequest.get(resultRequest.size() - 1), resultRequest, 2);
            resultRequest.add("\n ]\n}");
        } catch (ClassNotFoundException e) {
            error = "Driver not fount";
            e.printStackTrace();
        } catch (SQLException e) {
            error = "Database not connected or request error";
            e.printStackTrace();
        }
        return resultRequest;
    }

    private long calculateCountDays(String firstDate, String secondDate) {
        long result = 0;
        DateTimeFormatter fIn = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fDate = LocalDate.parse(firstDate, fIn);
        LocalDate sDate = LocalDate.parse(secondDate, fIn);
        result = sDate.toEpochDay() - fDate.toEpochDay();
        return result;
    }

    public void requestHelperStat(List<String> dateList, Statement stat, List<String> resultRequest) throws SQLException {
        StringBuilder request = new StringBuilder("select Customer_Surname, Customer_Name, Product_Name, price from ");
        request.append("(select Buy_DateTime, Customer_Surname, Customer_Name, Product_Name, ");
        request.append("(Product_Price * Count_product) AS price from buy ");
        request.append("join customer ON buy.Customer_ID = customer.Customer_ID ");
        request.append("join products on buy.Product_ID = products.Product_ID ) AS unitTab ");
        request.append("where Buy_DateTime >= '").append(dateList.get(0)).append(" 00:00:00' ");
        request.append("AND Buy_DateTime <= '").append(dateList.get(1)).append(" 23:59:59'");
        request.append(" order by Customer_Surname DESC, Customer_Name DESC");
        ResultSet result = stat.executeQuery(String.valueOf(request));
        StringBuilder firstSectorResults = new StringBuilder("{\n \"type\": \"stat\",\n \"totalDays\":");
        firstSectorResults.append(calculateCountDays(dateList.get(0), dateList.get(1)));
        firstSectorResults.append(",\n \"customers\": [");
        resultRequest.add(String.valueOf(firstSectorResults));
        addResultStat(resultRequest, result);
    }

    public void addResultStat(List<String> resultRequest, ResultSet result) throws SQLException {
        Set<String> bufName = new HashSet<>();
        long countSteps = 0;
        long sumBuysOnesCustomer = 0;
        long sumAll = 0;
        long countCustomers = 1;
        while (result.next()) {
            StringBuilder firstAndLastName = new StringBuilder(result.getString("Customer_Surname"));
            firstAndLastName.append(" ").append(result.getString("Customer_Name"));
            String str = String.valueOf(firstAndLastName);
            StringBuilder buffer = new StringBuilder("");
            if (!bufName.contains(str)) {
                if (countSteps > 0) {
                    changeLine(resultRequest.get(resultRequest.size() - 1), resultRequest, 1);
                    buffer.append("\n     ],\n     \"totalExpenses\":").append(sumBuysOnesCustomer);
                    buffer.append("\n   },");
                    sumAll += sumBuysOnesCustomer;
                    ++countCustomers;
                    sumBuysOnesCustomer = 0;
                }
                buffer.append("\n   {\n    \"name\": \"");
                buffer.append(result.getString("Customer_Surname")).append(" ");
                buffer.append(result.getString("Customer_Name")).append("\",\n");
                buffer.append("    \"purchases\": [");
                bufName.add(str);
                ++countSteps;
            }
            buffer.append("\n      {\n       \"name\": \"");
            buffer.append(result.getString("Product_Name")).append("\",\n");
            buffer.append("       \"expenses\": ").append(result.getLong("price"));
            buffer.append("\n      },");
            sumBuysOnesCustomer += result.getLong("price");
            resultRequest.add(String.valueOf(buffer));
        }
        changeLine(resultRequest.get(resultRequest.size() - 1), resultRequest, 1);
        StringBuilder buffer = new StringBuilder("");
        buffer.append("\n     ],\n     \"totalExpenses\":").append(sumBuysOnesCustomer);
        buffer.append("\n   }\n ],\n \"totalExpenses\": ").append(sumAll + sumBuysOnesCustomer).append(",\n");
        buffer.append(" \"avgExpenses\": ").append((double) (sumAll + sumBuysOnesCustomer) / countCustomers).append("\n}");
        resultRequest.add(String.valueOf(buffer));
    }

    public List<String> requestStat(List<String> dateList) {
        List<String> resultRequest = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            Class.forName("org.postgresql.Driver");
            Statement stat = conn.createStatement();
            requestHelperStat(dateList, stat, resultRequest);
        } catch (ClassNotFoundException e) {
            error = "Driver not fount";
            e.printStackTrace();
        } catch (SQLException e) {
            error = "Database not connected or request error";
            e.printStackTrace();
        }
        return resultRequest;
    }
}

