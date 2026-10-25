package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Locale;
import utils.DateUtils;

public class InsuranceStatement implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String insuranceId;
    private final LocalDate establishedDate;
    private final String licensePlate;
    private final String customerName;
    private final int insurancePeriod;
    private final long insuranceFees;

    public InsuranceStatement(String insuranceId, LocalDate establishedDate,
            String licensePlate, String customerName,
            int insurancePeriod, long insuranceFees) {
        this.insuranceId = Car.normalizeKey(insuranceId);
        this.establishedDate = establishedDate;
        this.licensePlate = Car.normalizeKey(licensePlate);
        this.customerName = customerName == null ? null : customerName.trim();
        this.insurancePeriod = insurancePeriod;
        this.insuranceFees = insuranceFees;
    }

    public String getInsuranceId() {return insuranceId;}

    public LocalDate getEstablishedDate() {return establishedDate;}

    public String getLicensePlate() {return licensePlate;}

    public String getCustomerName() {return customerName;}

    public int getInsurancePeriod() {return insurancePeriod;}

    public long getInsuranceFees() {return insuranceFees;}

    @Override
    public String toString() {
        return "Insurance ID: " + insuranceId
                + "\nEstablished date: " + new DateUtils().formatDate(establishedDate)
                + "\nLicense plate: " + licensePlate
                + "\nCustomer name: " + customerName
                + "\nInsurance period: " + insurancePeriod
                + "\nInsurance fees: " + insuranceFees;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof InsuranceStatement)) {
            return false;
        }
        return insuranceId.equalsIgnoreCase(((InsuranceStatement) object).insuranceId);
    }

    @Override
    public int hashCode() {
        return insuranceId.toUpperCase(Locale.ROOT).hashCode();
    }
}
