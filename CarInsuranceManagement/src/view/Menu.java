package view;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import model.Car;
import model.InsuranceStatement;
import service.CarManager;
import service.StatementManager;
import utils.DateUtils;
import utils.Inputter;
import validation.CarValidator;
import validation.StatementValidator;

public class Menu {

    private final Inputter inputter = new Inputter();
    private final DateUtils dateUtils = new DateUtils();
    private final CarValidator carValidator;
    private final StatementValidator statementValidator;
    private final CarManager carManager;
    private final StatementManager statementManager;

    public Menu(CarManager carManager, StatementManager statementManager,
            CarValidator carValidator, StatementValidator statementValidator) {
        this.carManager = carManager;
        this.statementManager = statementManager;
        this.carValidator = carValidator;
        this.statementValidator = statementValidator;
    }

    public void showMenu() {
        System.out.println();
        System.out.println("====================================");
        System.out.println("     CAR INSURANCE MANAGEMENT");
        System.out.println("====================================");
        System.out.println("1. Add car information");
        System.out.println("2. Find a car");
        System.out.println("3. Update a car");
        System.out.println("4. Delete a car");
        System.out.println("5. Add an insurance statement");
        System.out.println("6. List of insurance statements");
        System.out.println("7. Report on uninsured cars");
        System.out.println("8. Save data");
        System.out.println("9. Load data");
        System.out.println("10. Quit");
        System.out.println("====================================");
    }

    public int readChoice() {
        return inputter.readInt("Choose an option: ");
    }

    public void loadOnStartup() {
        if (!carManager.isDataFileReadable() || !statementManager.isDataFileReadable()) {
            System.out.println("No saved data found. Starting with empty lists.");
            return;
        }
        try {
            carManager.load().forEach(System.out::println);
            statementManager.load(carManager).forEach(System.out::println);
            System.out.println("Loaded " + carManager.size() + " cars and "
                    + statementManager.size() + " insurance statements.");
        } catch (IOException | ClassCastException e) {
            System.out.println("Cannot load saved data: " + e.getMessage());
        }
    }

    // FUNCTION 1: ADD CAR INFORMATION
    public void addCars() {
        while (true) {
            System.out.println();
            System.out.println("--- Add Car Information ---");

            Car car = readCarDetails();

            if (carManager.addCar(car)) {
                System.out.println("Car added successfully.");
            } else {
                System.out.println("Cannot add car.");
            }

            if (!inputter.readYesNo("Add another car? (Y/N): ")) {
                return;
            }
        }
    }

    private Car readCarDetails() {
        String plate = inputter.readValid("License plate: ", text -> {
            String error = carValidator.checkRequired("License plate", text);
            if (error == null && carManager.containsCar(text)) {
                return "License plate already exists.";
            }
            return error;
        });

        String owner = inputter.readValid("Car owner: ", carValidator::checkOwner);
        String brand = inputter.readValid("Car brand: ",
                text -> carValidator.checkRequired("Car brand", text));
        long value = carValidator.parseValue(
                inputter.readValid("Vehicle value: ", carValidator::checkValue));
        LocalDate registrationDate = dateUtils.parseDate(inputter.readValid("Registration date (MM/dd/yyyy): ", carValidator::checkDate));
        String place = inputter.readValid("Registration place: ",
                text -> carValidator.checkRequired("Registration place", text));
        int type = carValidator.parseVehicleType(
                inputter.readValid("Vehicle type (5, 7, or 9): ", carValidator::checkVehicleType));

        return new Car(plate, owner, brand, value, registrationDate, place, type);
    }

    private Car readUpdatedDetails(Car current) {
        System.out.println("License plate: " + current.getLicensePlate());
        System.out.println("Press Enter to keep the current value.");

        String owner = inputter.readValidOrKeep("Car owner [" + current.getCarOwner() + "]: ",
                current.getCarOwner(), carValidator::checkOwner);
        String brand = inputter.readValidOrKeep("Car brand [" + current.getCarBrand() + "]: ",
                current.getCarBrand(), text -> null);
        long value = carValidator.parseValue(inputter.readValidOrKeep(
                "Vehicle value [" + current.getVehicleValue() + "]: ",
                String.valueOf(current.getVehicleValue()), carValidator::checkValue));
        String currentDate = dateUtils.formatDate(current.getRegistrationDate());
        LocalDate registrationDate = dateUtils.parseDate(inputter.readValidOrKeep(
                "Registration date (MM/dd/yyyy) [" + currentDate + "]: ",
                currentDate, carValidator::checkDate));
        String place = inputter.readValidOrKeep(
                "Registration place [" + current.getRegistrationPlace() + "]: ",
                current.getRegistrationPlace(), text -> null);
        int type = carValidator.parseVehicleType(inputter.readValidOrKeep(
                "Vehicle type (5, 7, or 9) [" + current.getVehicleType() + "]: ",
                String.valueOf(current.getVehicleType()), carValidator::checkVehicleType));

        return new Car(current.getLicensePlate(), owner, brand, value, registrationDate, place, type);
    }

    // FUNCTION 2: FIND A CAR
    public void findCar() {
        System.out.println();
        System.out.println("--- Find a Car ---");

        Car car = carManager.findCar(inputter.readRequiredString("Enter license plate: "));
        if (car == null) {
            System.out.println("Unregistered vehicle");
            return;
        }

        String line = "----------------------------------------------------------------";
        System.out.println();
        System.out.println(line);
        System.out.printf("| %-22s | %-35s |%n", "Field", "Information");
        System.out.println(line);
        System.out.printf("| %-22s | %-35s |%n", "License plate", car.getLicensePlate());
        System.out.printf("| %-22s | %-35s |%n", "Car owner", car.getCarOwner());
        System.out.printf("| %-22s | %-35s |%n", "Car brand", car.getCarBrand());
        System.out.printf("| %-22s | %-35s |%n", "Vehicle value", car.getVehicleValue());
        System.out.printf("| %-22s | %-35s |%n", "Registration date",
                dateUtils.formatDate(car.getRegistrationDate()));
        System.out.printf("| %-22s | %-35s |%n", "Registration place", car.getRegistrationPlace());
        System.out.printf("| %-22s | %-35s |%n", "Vehicle type", car.getVehicleType());
        System.out.println(line);
    }

    // FUNCTION 3: UPDATE A CAR
    public void updateCar() {
        System.out.println();
        System.out.println("--- Update a Car ---");

        String plate = inputter.readRequiredString("Enter license plate: ");
        Car existingCar = carManager.findCar(plate);
        if (existingCar == null) {
            System.out.println("Unregistered vehicle");
            return;
        }

        System.out.println("Enter the new car information. The license plate cannot be changed.");

        Car updatedCar = readUpdatedDetails(existingCar);

        String error = carValidator.getValidationError(updatedCar);
        if (error != null) {
            System.out.println("Cannot update car: " + error);
            return;
        }

        if (carManager.updateCar(plate, updatedCar)) {
            System.out.println("Car updated successfully.");
        } else {
            System.out.println("Cannot update car. Please check the information.");
        }
    }

    // FUNCTION 4: DELETE A CAR
    public void deleteCar() {
        System.out.println();
        System.out.println("--- Delete a Car ---");

        String plate = inputter.readRequiredString("Enter license plate: ");
        Car car = carManager.findCar(plate);
        if (car == null) {
            System.out.println("Unregistered vehicle");
            return;
        }

        if (statementManager.hasInsuranceForPlate(car.getLicensePlate())) {
            System.out.println("Cannot delete this car because it has an insurance statement.");
            return;
        }

        System.out.println(car);
        if (!inputter.readYesNo("Are you sure you want to delete this car? (Y/N): ")) {
            System.out.println("Delete operation cancelled.");
            return;
        }

        if (carManager.deleteCar(plate)) {
            System.out.println("Car deleted successfully.");
        } else {
            System.out.println("Cannot delete car.");
        }
    }

    // FUNCTION 5: ADD AN INSURANCE STATEMENT
    public void addInsurances() {
        while (true) {
            System.out.println();
            System.out.println("--- Add an Insurance Statement ---");

            String id = inputter.readValid("Insurance ID: ", text -> {
                String error = statementValidator.checkRequired("Insurance ID", text);
                if (error == null && statementManager.containsInsurance(text)) {
                    return "Insurance ID already exists.";
                }
                return error;
            });
            LocalDate date = dateUtils.parseDate(
                    inputter.readValid("Established date (MM/dd/yyyy): ", statementValidator::checkDate));
            String plate = inputter.readValid("License plate: ",
                    text -> carManager.containsCar(text) ? null : "Unregistered vehicle");
            String customerName = inputter.readValid("Customer name: ",
                    text -> statementValidator.checkRequired("Customer name", text));
            int period = statementValidator.parsePeriod(
                    inputter.readValid("Insurance period (12, 24, or 36 months): ",
                            statementValidator::checkPeriod));

            InsuranceStatement statement = statementManager.createStatement(
                    id, date, plate, customerName, period, carManager);

            if (statement != null && statementManager.addInsurance(statement, carManager)) {
                System.out.println("Insurance statement added successfully.");
                printInsuranceStatements(Collections.singletonList(statement));
            } else {
                System.out.println("Cannot add insurance statement.");
            }

            if (!inputter.readYesNo("Add another insurance statement? (Y/N): ")) {
                return;
            }
        }
    }

    // FUNCTION 6: LIST INSURANCE STATEMENTS
    public void listInsuranceStatements() {
        System.out.println();
        System.out.println("--- List of Insurance Statements ---");

        int year = inputter.readInt("Enter year: ", 1, 9999);
        StatementManager.SortField sortField = readInsuranceSortField();
        boolean ascending = readAscendingDirection();

        printInsuranceReport(year, sortField, ascending,
                statementManager.getInsuranceByYear(year, sortField, ascending));
    }

    private StatementManager.SortField readInsuranceSortField() {
        System.out.println();
        System.out.println("Sort insurance statements by:");
        System.out.println("1. Insurance ID");
        System.out.println("2. Established date");
        System.out.println("3. License plate");
        System.out.println("4. Insurance period");

        switch (inputter.readInt("Choose sort field: ", 1, 4)) {
            case 1:
                return StatementManager.SortField.ID;
            case 2:
                return StatementManager.SortField.ESTABLISHED_DATE;
            case 3:
                return StatementManager.SortField.LICENSE_PLATE;
            default:
                return StatementManager.SortField.PERIOD;
        }
    }

    private void printInsuranceReport(int year, StatementManager.SortField sortField,
            boolean ascending, List<InsuranceStatement> statements) {
        System.out.println();
        System.out.println("Report : INSURANCE STATEMENTS");
        System.out.println("From : " + dateUtils.formatDate(LocalDate.of(year, 1, 1))
                + " To: " + dateUtils.formatDate(LocalDate.of(year, 12, 31)));
        System.out.println("Sorted by: " + sortField.label());
        System.out.println("Sort type : " + (ascending ? "ASC" : "DESC"));
        System.out.println();
        printInsuranceStatements(statements);
    }

    private void printInsuranceStatements(List<InsuranceStatement> statements) {
        if (statements.isEmpty()) {
            System.out.println("No insurance statements found for this year.");
            return;
        }

        String format = "| %3s | %-12s | %-16s | %-15s | %-25s | %-16s | %14s |%n";
        String border = buildBorder(3, 12, 16, 15, 25, 16, 14);

        System.out.println(border);
        System.out.printf(format, "No.", "Insurance Id", "Established Date", "License plate",
                "Customer", "Insurance period", "Insurance fees");
        System.out.println(border);

        int no = 1;
        for (InsuranceStatement statement : statements) {
            System.out.printf(format,
                    no++,
                    statement.getInsuranceId(),
                    dateUtils.formatDate(statement.getEstablishedDate()),
                    statement.getLicensePlate(),
                    statement.getCustomerName(),
                    statement.getInsurancePeriod(),
                    String.format(Locale.US, "$ %,d", statement.getInsuranceFees()));
        }
        System.out.println(border);
    }

    private String buildBorder(int... widths) {
        StringBuilder line = new StringBuilder("-");
        for (int width : widths) {
            for (int i = 0; i < width + 2; i++) {
                line.append('-');
            }
            line.append('-');
        }
        return line.toString();
    }

    // FUNCTION 7: REPORT ON UNINSURED CARS
    public void reportUninsuredCars() {
        System.out.println();
        System.out.println("--- Report on Uninsured Cars ---");

        CarManager.SortField sortField = readCarSortField();
        boolean ascending = readAscendingDirection();

        printUninsuredReport(sortField, ascending,
                carManager.getUninsuredCars(statementManager.getInsuredPlates(), sortField, ascending));
    }

    private CarManager.SortField readCarSortField() {
        System.out.println();
        System.out.println("Sort uninsured cars by:");
        System.out.println("1. License plate");
        System.out.println("2. Car owner");
        System.out.println("3. Registration date");
        System.out.println("4. Vehicle type");

        switch (inputter.readInt("Choose sort field: ", 1, 4)) {
            case 1:
                return CarManager.SortField.LICENSE_PLATE;
            case 2:
                return CarManager.SortField.OWNER;
            case 3:
                return CarManager.SortField.REGISTRATION_DATE;
            default:
                return CarManager.SortField.VEHICLE_TYPE;
        }
    }

    private boolean readAscendingDirection() {
        System.out.println();
        System.out.println("Sort direction:");
        System.out.println("1. Ascending (ASC)");
        System.out.println("2. Descending (DESC)");

        return inputter.readInt("Choose direction: ", 1, 2) == 1;
    }

    private void printUninsuredReport(CarManager.SortField sortField, boolean ascending, List<Car> cars) {
        System.out.println();
        System.out.println("Report: UNINSURED CARS");
        System.out.println();
        System.out.println("Sorted by : " + sortField.label());
        System.out.println("Sort type : " + (ascending ? "ASC" : "DESC"));
        System.out.println();

        if (cars.isEmpty()) {
            System.out.println("No uninsured cars found.");
            return;
        }

        String format = "| %3s | %-15s | %-17s | %-25s | %-14s | %-12s | %12s |%n";
        String border = buildBorder(3, 15, 17, 25, 14, 12, 12);

        System.out.println(border);
        System.out.printf(format, "No.", "License plate", "Registration Date", "Vehicle Owner",
                "Brand", "Vehicle type", "Value");
        System.out.println(border);

        int no = 1;
        for (Car car : cars) {
            System.out.printf(format,
                    no++,
                    car.getLicensePlate(),
                    dateUtils.formatDate(car.getRegistrationDate()),
                    car.getCarOwner(),
                    car.getCarBrand(),
                    car.getVehicleType(),
                    String.format(Locale.US, "$ %,d", car.getVehicleValue()));
        }
        System.out.println(border);
    }

    // FUNCTION 8: SAVE DATA 
    public boolean saveData() {
        System.out.println();
        System.out.println("--- Save Data ---");

        String error = carManager.validateForSave();
        if (error == null) {
            error = statementManager.validateForSave();
        }
        if (error != null) {
            System.out.println("Cannot save, nothing was written: " + error);
            return false;
        }

        try {
            carManager.save();
            statementManager.save();
            System.out.println("Data saved successfully.");
            System.out.println("Car data file: " + carManager.getFileName());
            System.out.println("Insurance data file: " + statementManager.getFileName());
            return true;
        } catch (IOException e) {
            System.out.println("Some data could not be saved: " + e.getMessage());
            return false;
        }
    }

    // FUNCTION 9: LOAD DATA
    public void loadData() {
        System.out.println();
        System.out.println("--- Load Data ---");

        if (hasUnsavedChanges()
                && !inputter.readYesNo("Unsaved changes may be lost. Continue loading? (Y/N): ")) {
            System.out.println("Load operation cancelled.");
            return;
        }

        if (!carManager.isDataFileReadable() || !statementManager.isDataFileReadable()) {
            System.out.println("Cannot load data. Check whether both data files exist and are valid.");
            return;
        }

        try {
            carManager.load().forEach(System.out::println);
            statementManager.load(carManager).forEach(System.out::println);

            System.out.println("Data loaded successfully.");
            System.out.println("Number of cars: " + carManager.size());
            System.out.println("Number of insurance statements: " + statementManager.size());
        } catch (IOException | ClassCastException e) {
            System.out.println("Cannot load data: " + e.getMessage());
        }
    }

    // FUNCTION 10: QUIT 
    public boolean handleQuit() {
        System.out.println();

        if (!inputter.readYesNo("Are you sure you want to quit? (Y/N): ")) {
            System.out.println("Returning to the main menu.");
            return false;
        }

        if (hasUnsavedChanges()) {
            System.out.println("There are unsaved changes. Saving data...");
            if (!saveData() && !inputter.readYesNo("Saving failed. Quit anyway? (Y/N): ")) {
                return false;
            }
        }

        System.out.println("Program terminated.");
        return true;
    }

    private boolean hasUnsavedChanges() {
        return carManager.isDirty() || statementManager.isDirty();
    }
}
