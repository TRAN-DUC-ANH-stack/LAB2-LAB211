package service;

import fileio.Storage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import model.Car;
import model.InsuranceStatement;
import validation.StatementValidator;

public class StatementManager {

    public enum SortField {
        ID("Insurance Id", Comparator.comparing(InsuranceStatement::getInsuranceId,
                Comparator.<String>comparingInt(String::length).thenComparing(Comparator.<String>naturalOrder()))),
        ESTABLISHED_DATE("Established Date", Comparator.comparing(InsuranceStatement::getEstablishedDate)),
        LICENSE_PLATE("License plate", Comparator.comparing(InsuranceStatement::getLicensePlate)),
        PERIOD("Insurance period", Comparator.comparingInt(InsuranceStatement::getInsurancePeriod));

        private final String label;
        private final Comparator<InsuranceStatement> comparator;

        SortField(String label, Comparator<InsuranceStatement> comparator) {
            this.label = label;
            this.comparator = comparator;
        }

        public String label() {return label;}

        public Comparator<InsuranceStatement> comparator() {return comparator;}
    }

    private final Map<String, InsuranceStatement> insurances = new LinkedHashMap<>();
    private final StatementValidator validator;
    private final Storage<Map<String, InsuranceStatement>> storage;
    private final String fileName = "insurances.dat";
    private boolean modified;

    public StatementManager(StatementValidator validator,
            Storage<Map<String, InsuranceStatement>> storage) {
        this.validator = validator;
        this.storage = storage;
    }

    private String key(String text) {
        return text == null ? "" : text.trim().toUpperCase(Locale.ROOT);
    }

    public InsuranceStatement createStatement(String id, LocalDate date, String plate,
            String customerName, int period, CarManager carManager) {
        Car car = carManager.findCar(plate);
        if (car == null) {
            return null;
        }
        long fees = validator.calculateFees(car.getVehicleValue(), period);
        return new InsuranceStatement(id, date, plate, customerName, period, fees);
    }

    public boolean addInsurance(InsuranceStatement statement, CarManager carManager) {
        if (validator.getInputValidationError(statement) != null
                || containsInsurance(statement.getInsuranceId())) {
            return false;
        }
        Car car = carManager.findCar(statement.getLicensePlate());
        if (car == null) {
            return false;
        }
        long expectedFees = validator.calculateFees(car.getVehicleValue(), statement.getInsurancePeriod());
        if (statement.getInsuranceFees() != expectedFees || !validator.isValid(statement)) {
            return false;
        }
        insurances.put(key(statement.getInsuranceId()), statement);
        modified = true;
        return true;
    }

    public boolean containsInsurance(String insuranceId) {
        return insurances.containsKey(key(insuranceId));
    }

    public Set<String> getInsuredPlates() {
        return insurances.values().stream()
                .map(statement -> key(statement.getLicensePlate()))
                .collect(Collectors.toSet());
    }

    public boolean hasInsuranceForPlate(String licensePlate) {
        return getInsuredPlates().contains(key(licensePlate));
    }

    public List<InsuranceStatement> getInsuranceByYear(int year, SortField sortField, boolean ascending) {
        Comparator<InsuranceStatement> order = ascending ? sortField.comparator() : sortField.comparator().reversed();
        return insurances.values().stream()
                .filter(statement -> statement.getEstablishedDate().getYear() == year)
                .sorted(order)
                .collect(Collectors.toList());
    }

    public Map<String, InsuranceStatement> getInsurances() {
        return new LinkedHashMap<>(insurances);
    }

    public String getFileName() {return fileName;}

    public boolean isDataFileReadable() {
        try {
            return storage.load(fileName) != null;
        } catch (IOException | ClassCastException e) {
            return false;
        }
    }

    public List<String> load(CarManager carManager) throws IOException {
        List<String> warnings = new ArrayList<>();
        Map<String, InsuranceStatement> loaded;
        try {
            loaded = storage.load(fileName);
        } catch (ClassCastException e) {
            throw new IOException("Unexpected data format in " + fileName, e);
        }
        if (loaded == null) {
            warnings.add(fileName + " not found. Keeping the current insurance list.");
            return warnings;
        }
        Map<String, InsuranceStatement> valid = new LinkedHashMap<>();
        for (InsuranceStatement statement : loaded.values()) {
            String error = validator.getValidationError(statement);
            if (error == null && !carManager.containsCar(statement.getLicensePlate())) {
                error = "car " + statement.getLicensePlate() + " is not registered";
            }
            if (error == null) {
                valid.put(key(statement.getInsuranceId()), statement);
            } else {
                warnings.add("Skipped invalid insurance statement in " + fileName + ": " + error);
            }
        }
        insurances.clear();
        insurances.putAll(valid);
        modified = false;
        return warnings;
    }

    public String validateForSave() {
        for (InsuranceStatement statement : insurances.values()) {
            String error = validator.getValidationError(statement);
            if (error != null) {
                return "Insurance " + statement.getInsuranceId() + ": " + error;
            }
        }
        return null;
    }

    public void save() throws IOException {
        storage.save(fileName, getInsurances());
        modified = false;
    }

    public int size() {return insurances.size();}

    public boolean isEmpty() {return insurances.isEmpty();}

    public boolean isDirty() {return modified;}
}