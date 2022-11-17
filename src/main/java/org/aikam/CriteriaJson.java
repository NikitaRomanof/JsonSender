package org.aikam;

import java.util.Objects;

public class CriteriaJson {
    //  criterion first
    private String lastName;

    //  criterion second
    private String productName;
    private Long minTimes;

    //  third criterion
    private Long minExpenses;
    private Long maxExpenses;

    //  fourth criterion
    private Long badCustomers;

    public final int numberCriteria;

    public CriteriaJson(String lastName) {
        numberCriteria = 1;
        this.lastName = lastName;
        this.productName = null;
        this.minTimes = 0L;
        this.minExpenses = 0L;
        this.maxExpenses = 0L;
        this.badCustomers = 0L;
    }

    public CriteriaJson(String productName, long minTimes) {
        numberCriteria = 2;
        this.productName = productName;
        this.minTimes = minTimes;

        this.lastName = null;
        this.minExpenses = 0L;
        this.maxExpenses = 0L;
        this.badCustomers = 0L;
    }

    public CriteriaJson(long minExpenses, long maxExpenses) {
        numberCriteria = 3;
        this.minExpenses = minExpenses;
        this.maxExpenses = maxExpenses;

        this.lastName = null;
        this.productName = null;
        this.minTimes = 0L;
        this.badCustomers = 0L;

    }

    public CriteriaJson(long badCustomers) {
        numberCriteria = 4;
        this.badCustomers = badCustomers;

        this.lastName = null;
        this.productName = null;
        this.minTimes = 0L;
        this.minExpenses = 0L;
        this.maxExpenses = 0L;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProductName() {
        return productName;
    }

    public Long getMinTimes() {
        return minTimes;
    }

    public Long getMinExpenses() {
        return minExpenses;
    }

    public Long getMaxExpenses() {
        return maxExpenses;
    }

    public Long getBadCustomers() {
        return badCustomers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CriteriaJson that = (CriteriaJson) o;
        return Objects.equals(minTimes, that.minTimes) && Objects.equals(minExpenses, that.minExpenses)
                && Objects.equals(maxExpenses, that.maxExpenses)
                && Objects.equals(badCustomers, that.badCustomers) && numberCriteria == that.numberCriteria
                && lastName.equals(that.lastName) && productName.equals(that.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastName, productName, minTimes, minExpenses, maxExpenses, badCustomers, numberCriteria);
    }

    @Override
    public String toString() {
        return "CriteriaJson{" +
                "lastName='" + lastName + '\'' +
                ", productName='" + productName + '\'' +
                ", minTimes=" + minTimes +
                ", minExpenses=" + minExpenses +
                ", maxExpenses=" + maxExpenses +
                ", badCustomers=" + badCustomers +
                ", numberCriteria=" + numberCriteria +
                '}';
    }
}
