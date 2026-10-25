package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DateUtils {

    private final String[] inputPatterns = {
        "M/d/uuuu", // 9/29/2026, 09/29/2026
        "d/M/uuuu", // 29/9/2026
        "uuuu-M-d", // 2026-09-29
        "uuuu/M/d", // 2026/09/29
        "M-d-uuuu", // 09-29-2026
        "d-M-uuuu", // 29-09-2026
        "uuuuMMdd", // 20260929
        "d MMM uuuu", // 9 Sep 2026   
        "MMM d uuuu", // Sep 9 2026   (Sep, 9, 2026)
        "d MMMM uuuu", // 9 September 2026
        "MMMM d uuuu" // September 9 2026
    };

    private final List<DateTimeFormatter> inputFormatters = new ArrayList<>();

    private final DateTimeFormatter outputFormatter = createFormatter("MM/dd/uuuu");
    public DateUtils() {
        for (String pattern : inputPatterns) {
            inputFormatters.add(createFormatter(pattern));
        }
    }

    private DateTimeFormatter createFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive() 
                .appendPattern(pattern)
                .toFormatter(Locale.US)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    private String clean(String input) {
        return input.trim()
                .replaceAll("(?i)(?<=\\d)(st|nd|rd|th)\\b", "")
                .replaceAll("(?i)\\bsept\\b", "Sep")
                .replace(',', ' ')
                .replaceAll("\\s+", " ")
                .trim();
    }

    public LocalDate parseDate(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be empty.");
        }
        String text = clean(input);
        for (DateTimeFormatter formatter : inputFormatters) {
            try {
                return LocalDate.parse(text, formatter);
            } catch (DateTimeParseException e) {
            }
        }
        throw new IllegalArgumentException("Invalid date. Use MM/dd/yyyy or yyyy-MM-dd "
                + "(also accepted: 29/9/2026, 9th Sep, 2026, Sep, 9, 2026).");
    }

   
    public String formatDate(LocalDate date) {
        return date == null ? "N/A" : date.format(outputFormatter);
    }
}

    