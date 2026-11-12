import java.math.BigDecimal;

public class TrackerRegressionTest {
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void expectRejected(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected input to be rejected");
    }

    public static void main(String[] args) {
        for (String invalid : new String[]{"0", "-1", "0.001", "NaN", "Infinity"}) {
            expectRejected(() -> TrackerInput.parseAmount(invalid));
        }
        expectRejected(() -> new Goal("Test", 1, "2026-02-29"));
        Goal goal = new Goal("Test", TrackerInput.parseAmount("0.30"), "");
        check(goal.getDeadline() == null, "A deadline is optional");
        goal.addDeposit(1, new BigDecimal("0.10"), "2028-02-29", "first");
        expectRejected(() -> goal.addDeposit(2, 0.21, "2028-02-29", ""));
        expectRejected(() -> goal.addDeposit(2, 0.20, "2026-02-30", ""));
        expectRejected(() -> goal.addDeposit(2, 0.20, "", ""));
        check(goal.getDepositHistory().size() == 1, "Rejected deposits changed history");
        check(goal.getSavedAmount().equals(new BigDecimal("0.10")), "Rejected deposits changed balance");
        goal.addDeposit(2, new BigDecimal("0.20"), "2028-03-01", "final");
        check(goal.getSavedAmount().equals(new BigDecimal("0.30")), "Incorrect final balance");
        check(goal.getProgressPercentage() == 100 && goal.getStatus().equals("DONE"), "Goal should be complete");
        System.out.println("Tracker regression tests passed.");
    }
}
