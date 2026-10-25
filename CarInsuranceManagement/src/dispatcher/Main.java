package dispatcher;

import fileio.BinaryStorage;
import java.util.NoSuchElementException;
import service.CarManager;
import service.StatementManager;
import validation.CarValidator;
import validation.StatementValidator;
import view.Menu;

public class Main {

    public static void main(String[] args) {
        CarValidator carValidator = new CarValidator();
        StatementValidator statementValidator = new StatementValidator();
        CarManager carManager = new CarManager(carValidator, new BinaryStorage<>());
        StatementManager statementManager = new StatementManager(statementValidator, new BinaryStorage<>());
        Menu menu = new Menu(carManager, statementManager, carValidator, statementValidator);
        menu.loadOnStartup();
        try {
            while (true) {
                menu.showMenu();
                int choice = menu.readChoice();

                switch (choice) {
                    case 1:
                        menu.addCars();
                        break;
                    case 2:
                        menu.findCar();
                        break;
                    case 3:
                        menu.updateCar();
                        break;
                    case 4:
                        menu.deleteCar();
                        break;
                    case 5:
                        menu.addInsurances();
                        break;
                    case 6:
                        menu.listInsuranceStatements();
                        break;
                    case 7:
                        menu.reportUninsuredCars();
                        break;
                    case 8:
                        menu.saveData();
                        break;
                    case 9:
                        menu.loadData();
                        break;
                    case 10:
                        if (menu.handleQuit()) {
                            return;
                        }
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1 to 10.");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nInput closed. Exiting.");
        }
    }
}