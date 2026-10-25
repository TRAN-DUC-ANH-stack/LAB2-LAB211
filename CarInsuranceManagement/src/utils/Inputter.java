package utils;

import java.util.Scanner;
import java.util.function.Function;

public class Inputter {

    private final Scanner scanner = new Scanner(System.in);

    public String readString(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    public int readInt(String message) {
        while (true) {
            try {
                return Integer.parseInt(readString(message));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    public int readInt(String message, int minimum, int maximum) {
        while (true) {
            int value = readInt(message);
            if (value >= minimum && value <= maximum) {
                return value;
            }
            System.out.println("Please enter a number from " + minimum + " to " + maximum + ".");
        }
    }

    public String readRequiredString(String message) {
        while (true) {
            String value = readString(message);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    public boolean readYesNo(String message) {
        while (true) {
            String answer = readString(message).toUpperCase();
            if (answer.equals("Y") || answer.equals("YES")) {
                return true;
            }
            if (answer.equals("N") || answer.equals("NO")) {
                return false;
            }
            System.out.println("Please enter Y or N.");
        }
    }

    public String readValid(String message, Function<String, String> checker) {
        while (true) {
            String text = readString(message);
            String error = checker.apply(text);
            if (error == null) {
                return text;
            }
            System.out.println(error);
        }
    }

    public String readValidOrKeep(String message, String current, Function<String, String> checker) {
        while (true) {
            String text = readString(message);
            if (text.isEmpty()) {
                return current;
            }
            String error = checker.apply(text);
            if (error == null) {
                return text;
            }
            System.out.println(error);
        }
    }
}
