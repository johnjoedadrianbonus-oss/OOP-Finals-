import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

final class TrackerInput {
    private TrackerInput() {}

    static BigDecimal parseAmount(String text) {
        if (!text.matches("[+-]?(?:[0-9]+(?:[.][0-9]*)?|[.][0-9]+)")) {
            throw new IllegalArgumentException("Enter a peso amount in decimal form.");
        }
        return positiveAmount(new BigDecimal(text));
    }

    static BigDecimal positiveAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        try {
            return amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Amount must be a whole number of centavos.");
        }
    }

    static String date(String text) {
        try {
            return LocalDate.parse(text == null ? "" : text.trim()).toString();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Enter a valid date in YYYY-MM-DD format.");
        }
    }
}
