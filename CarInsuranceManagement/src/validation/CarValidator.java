package validation;

import java.util.Arrays;
import java.util.List;
import model.Car;
import utils.DateUtils;

public class CarValidator {

    private final List<Integer> vehicleTypes = Arrays.asList(5, 7, 9);
    private final DateUtils dateUtils = new DateUtils();
    private final String ownerError = "Car owner must contain 2 to 35 characters.";
    private final String valueError = "Vehicle value must be greater than 999.";
    private final String typeError = "Vehicle type must be one of " + vehicleTypes + ".";
    private final String numberError = "Please enter a valid whole number.";

    public String getValidationError(Car car) {
        if (car == null) {
            return "Car information cannot be null.";
        }
        if (isBlank(car.getLicensePlate())) {
            return "License plate cannot be empty.";
        }
        if (!isValidOwner(car.getCarOwner())) {
            return ownerError;
        }
        if (isBlank(car.getCarBrand())) {
            return "Car brand cannot be empty.";
        }
        if (!isValidValue(car.getVehicleValue())) {
            return valueError;
        }
        if (car.getRegistrationDate() == null) {
            return "Registration date is invalid.";
        }
        if (isBlank(car.getRegistrationPlace())) {
            return "Registration place cannot be empty.";
        }
        if (!isValidVehicleType(car.getVehicleType())) {
            return typeError;
        }
        return null;
    }

    public boolean isValid(Car car) {
        return getValidationError(car) == null;
    }

    public boolean isValidOwner(String owner) {
        return owner != null && owner.trim().length() >= 2 && owner.trim().length() <= 35;
    }

    public boolean isValidValue(long value) {
        return value > 999;
    }

    public boolean isValidVehicleType(int vehicleType) {
        return vehicleTypes.contains(vehicleType);
    }

    public String parseOwner(String text) {
        if (!isValidOwner(text)) {
            throw new IllegalArgumentException(ownerError);
        }
        return text.trim();
    }

    public long parseValue(String text) {
        try {
            long value = Long.parseLong(text.trim());
            if (isValidValue(value)) {
                return value;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(numberError);
        }
        throw new IllegalArgumentException(valueError);
    }

    public int parseVehicleType(String text) {
        int type = parseInt(text);
        if (!isValidVehicleType(type)) {
            throw new IllegalArgumentException(typeError);
        }
        return type;
    }

    public String checkRequired(String label, String text) {
        return isBlank(text) ? label + " cannot be empty." : null;
    }

    public String checkOwner(String text) {
        return errorOf(() -> parseOwner(text));
    }

    public String checkValue(String text) {
        return errorOf(() -> parseValue(text));
    }

    public String checkVehicleType(String text) {
        return errorOf(() -> parseVehicleType(text));
    }

    public String checkDate(String text) {
        return errorOf(() -> dateUtils.parseDate(text));   
    }

    private int parseInt(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(numberError);
        }
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    private String errorOf(Runnable parser) {
        try {
            parser.run();
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}
