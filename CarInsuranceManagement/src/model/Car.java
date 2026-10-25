package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;
import utils.DateUtils;

public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String licensePlate;
    private String carOwner;
    private String carBrand;
    private long vehicleValue;
    private LocalDate registrationDate;
    private String registrationPlace;
    private int vehicleType;

    public Car(String licensePlate, String carOwner, String carBrand,
            long vehicleValue, LocalDate registrationDate,
            String registrationPlace, int vehicleType) {
        this.licensePlate = normalizeKey(licensePlate);
        this.carOwner = trim(carOwner);
        this.carBrand = trim(carBrand);
        this.vehicleValue = vehicleValue;
        this.registrationDate = registrationDate;
        this.registrationPlace = trim(registrationPlace);
        this.vehicleType = vehicleType;
    }

    public static String normalizeKey(String text) {
        return text == null ? "" : text.trim().toUpperCase(Locale.ROOT);
    }

    private static String trim(String text) {
        return text == null ? null : text.trim();
    }

    public String getLicensePlate() {return licensePlate;}

    public String getCarOwner() {return carOwner;}
    public void setCarOwner(String carOwner) {this.carOwner = trim(carOwner);}

    public String getCarBrand() {return carBrand;}
    public void setCarBrand(String carBrand) {this.carBrand = trim(carBrand);}

    public long getVehicleValue() {return vehicleValue;}
    public void setVehicleValue(long vehicleValue) {this.vehicleValue = vehicleValue;}

    public LocalDate getRegistrationDate() {return registrationDate;}
    public void setRegistrationDate(LocalDate registrationDate) {this.registrationDate = registrationDate;}

    public String getRegistrationPlace() {return registrationPlace;}
    public void setRegistrationPlace(String registrationPlace) {this.registrationPlace = trim(registrationPlace);}

    public int getVehicleType() {return vehicleType;}
    public void setVehicleType(int vehicleType) {this.vehicleType = vehicleType;}

    @Override
    public String toString() {
        return "License plate: " + licensePlate
                + "\nCar owner: " + carOwner
                + "\nCar brand: " + carBrand
                + "\nVehicle value: " + vehicleValue
                + "\nRegistration date: " + new DateUtils().formatDate(registrationDate)
                + "\nRegistration place: " + registrationPlace
                + "\nVehicle type: " + vehicleType;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Car)) {
            return false;
        }
        return licensePlate.equalsIgnoreCase(((Car) object).licensePlate);
    }

    @Override
    public int hashCode() {
        return licensePlate.toUpperCase(Locale.ROOT).hashCode();
    }
}
