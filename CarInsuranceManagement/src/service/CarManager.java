package service;

import fileio.Storage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import model.Car;
import validation.CarValidator;

public class CarManager {

    public enum SortField {
        LICENSE_PLATE("License plate", Comparator.comparing(Car::getLicensePlate)),
        OWNER("Vehicle Owner", Comparator.comparing(Car::getCarOwner, String.CASE_INSENSITIVE_ORDER)),
        REGISTRATION_DATE("Registration Date", Comparator.comparing(Car::getRegistrationDate)),
        VEHICLE_TYPE("Vehicle type", Comparator.comparingInt(Car::getVehicleType));

        private final String label;
        private final Comparator<Car> comparator;

        SortField(String label, Comparator<Car> comparator) {
            this.label = label;
            this.comparator = comparator;
        }

        public String label() {return label;}

        public Comparator<Car> comparator() {return comparator;}
    }

    private final Map<String, Car> cars = new LinkedHashMap<>();
    private final CarValidator validator;
    private final Storage<Map<String, Car>> storage;
    private final String fileName = "carInfo.dat";
    private boolean modified;

    public CarManager(CarValidator validator, Storage<Map<String, Car>> storage) {
        this.validator = validator;
        this.storage = storage;
    }

    private String key(String text) {
        return text == null ? "" : text.trim().toUpperCase(Locale.ROOT);
    }

    // 1. Add a car
    public boolean addCar(Car car) {
        if (!validator.isValid(car) || containsCar(car.getLicensePlate())) {
            return false;
        }
        cars.put(key(car.getLicensePlate()), car);
        modified = true;
        return true;
    }

    // 2. Find a car
    public Car findCar(String licensePlate) {
        return cars.get(key(licensePlate));
    }

    public boolean containsCar(String licensePlate) {
        return cars.containsKey(key(licensePlate));
    }

    // 3. Update a car 
    public boolean updateCar(String licensePlate, Car updatedCar) {
        if (!containsCar(licensePlate) || !validator.isValid(updatedCar)
                || !key(licensePlate).equals(key(updatedCar.getLicensePlate()))) {
            return false;
        }
        cars.put(key(licensePlate), updatedCar);
        modified = true;
        return true;
    }

    // 4. Delete a car
    public boolean deleteCar(String licensePlate) {
        boolean removed = cars.remove(key(licensePlate)) != null;
        modified |= removed;
        return removed;
    }

    // 7. 
    public List<Car> getUninsuredCars(Set<String> insuredPlates,
            SortField sortField, boolean ascending) {
        Comparator<Car> order = ascending ? sortField.comparator() : sortField.comparator().reversed();
        return cars.values().stream()
                .filter(car -> !insuredPlates.contains(key(car.getLicensePlate())))
                .sorted(order)
                .collect(Collectors.toList());
    }

    public Map<String, Car> getCars() {
        return new LinkedHashMap<>(cars);
    }

    public String getFileName() {return fileName;}

    public boolean isDataFileReadable() {
        try {
            return storage.load(fileName) != null;
        } catch (IOException | ClassCastException e) {
            return false;
        }
    }

    public List<String> load() throws IOException {
        List<String> warnings = new ArrayList<>();
        Map<String, Car> loaded;
        try {
            loaded = storage.load(fileName);
        } catch (ClassCastException e) {
            throw new IOException("Unexpected data format in " + fileName, e);
        }
        if (loaded == null) {
            warnings.add(fileName + " not found. Keeping the current car list.");
            return warnings;
        }
        Map<String, Car> valid = new LinkedHashMap<>();
        for (Car car : loaded.values()) {
            String error = validator.getValidationError(car);
            if (error == null) {
                valid.put(key(car.getLicensePlate()), car);
            } else {
                warnings.add("Skipped invalid car in " + fileName + ": " + error);
            }
        }
        cars.clear();
        cars.putAll(valid);
        modified = false;
        return warnings;
    }

    public String validateForSave() {
        for (Car car : cars.values()) {
            String error = validator.getValidationError(car);
            if (error != null) {
                return "Car " + car.getLicensePlate() + ": " + error;
            }
        }
        return null;
    }

    public void save() throws IOException {
        storage.save(fileName, getCars());
        modified = false;
    }

    public int size() {return cars.size();}

    public boolean isEmpty() {return cars.isEmpty();}

    public boolean isDirty() {return modified;}
}