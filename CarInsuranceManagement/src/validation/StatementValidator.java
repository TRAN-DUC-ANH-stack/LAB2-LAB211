package validation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.InsuranceStatement;
import utils.DateUtils;

public class StatementValidator {

    private final Map<Integer, Integer> feePercent = new LinkedHashMap<>();
    private final DateUtils dateUtils = new DateUtils(); 
    
    public StatementValidator() {
        feePercent.put(12, 25);
        feePercent.put(24, 40);
        feePercent.put(36, 45);
    }

    public String getInputValidationError(InsuranceStatement statement) {
        if (statement == null) {
            return "Insurance information cannot be null.";
        }
        if (isBlank(statement.getInsuranceId())) {
            return "Insurance ID cannot be empty.";
        }
        if (statement.getEstablishedDate() == null) {
            return "Established date is invalid.";
        }
        if (isBlank(statement.getLicensePlate())) {
            return "License plate cannot be empty.";
        }
        if (isBlank(statement.getCustomerName())) {
            return "Customer name cannot be empty.";
        }
        if (!isValidPeriod(statement.getInsurancePeriod())) {
            return getPeriodError();
        }
        return null;
    }

    /** Kiểm tra đầy đủ (đã có phí). */
    public String getValidationError(InsuranceStatement statement) {
        String error = getInputValidationError(statement);
        if (error == null && statement.getInsuranceFees() <= 0) {
            return "Insurance fees must be a positive integer.";
        }
        return error;
    }

    public boolean isValid(InsuranceStatement statement) {
        return getValidationError(statement) == null;
    }

    public boolean isValidPeriod(int period) {
        return feePercent.containsKey(period);
    }

    public long calculateFees(long vehicleValue, int period) {
        return Math.round(vehicleValue * feePercent.get(period) / 100.0);
    }

    public String getPeriodChoices() {
        List<String> periods = new ArrayList<>();
        feePercent.keySet().forEach(period -> periods.add(String.valueOf(period)));
        int last = periods.size() - 1;
        return String.join(", ", periods.subList(0, last)) + " or " + periods.get(last);
    }

    public String getPeriodError() {
        return "Insurance period must be " + getPeriodChoices() + " months.";
    }

    public int parsePeriod(String text) {
        int period = parseInt(text);
        if (!isValidPeriod(period)) {
            throw new IllegalArgumentException(getPeriodError());
        }
        return period;
    }

    public String checkPeriod(String text) {
        try {
            parsePeriod(text);
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String checkRequired(String label, String text) {
        return isBlank(text) ? label + " cannot be empty." : null;
    }

   public String checkDate(String text) {
        try {
            dateUtils.parseDate(text);                   
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }


    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    private int parseInt(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid whole number.");
        }
    }
}